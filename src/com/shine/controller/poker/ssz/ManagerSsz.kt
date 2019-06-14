package com.shine.controller.poker.ssz

import com.shine.agent.Agent
import com.shine.amodel.Manager
import com.shine.controller.aHall.Hall
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

object ManagerSsz : Manager {

    override val type = "ssz"
    override val rand = Random()
    override val mapRoom = Hall.mapRoom
    override val mapUserRoom = Hall.mapUserRoom

    //data=create
    //command=hall_room
    override fun OnCreate(agent: Agent, root: JSONObject) {//command=hall_room,data=create,type=maj

        val uid = agent.UID
        val rid = RidSafeGet()
        val detail = root["detail"] as JSONObject
        val di = detail["di"].toString().toInt()
        //val round = detail["round"].toString().toInt()
        val pwd = detail["pwd"].toString()
        val round = 1
        //军团战相关
        var armyBoo = 0;
        if (root.has("armyBoo")) {
            armyBoo = 1
        }

        val table = TableSsz(rid, uid, pwd, di, round, 0, armyBoo)
        mapRoom[rid] = table
        Send(agent, Msg(rid, "create success", "create", "hall_room"))
        table.OnUserSit(agent.user)
        table.addPlayCards_Ai()
    }


    //data=join
    //command=hall_room
    override fun OnJoin(agent: Agent, root: JSONObject) {

        val rid = root["detail"].toString().toInt()
        val table = mapRoom[rid]

        val pwd = root["type"].toString()
        if (!table?.pwd.equals(pwd)) {
            Send(agent, Msg(5, "wrong pwd", "join", "hall_room"))
            return
        }

        val resultJoin = table?.OnUserSit(agent.user)

        when (resultJoin) {
            0 -> Send(agent, Msg(resultJoin, "no room", "join", "hall_room"))
            1 -> Send(agent, Msg(resultJoin, "success", "join", "hall_room"))
            2 -> Send(agent, Msg(resultJoin, "game started", "join", "hall_room"))
            3 -> Send(agent, Msg(resultJoin, "no seat", "join", "hall_room"))
            4 -> Send(agent, Msg(resultJoin, "wrong", "join", "hall_room"))
            5 -> Send(agent, Msg(resultJoin, "wrong pwd", "join", "hall_room"))
        }
    }


    //command=hall_room
    override fun OnList(agent: Agent, root: JSONObject) {

        val rids = MutableList(0, { 0 })
        val detail = root["detail"] as JSONObject

        val di = detail["di"].toString().toInt()
        // val round = detail["round"].toString().toInt()

        for ((rid, table) in mapRoom) {
            if (table.type != "ssz") continue
            if (table.isStart) continue
            if (table.numCur == 4) continue
            if (table.pwd != "") continue
            if (table.di != di) continue
            // if (table.roundMax != round) continue
            rids.add(rid)
        }

        Send(agent, Msg(rids.size, rids, "list", "hall_room"))
        return
    }

    //command=msg_maj
    //data =leave/ready/ chi/gang/peng/hu/pass
    override fun HandleAll(agent: Agent, root: JSONObject) {

        if (!root.has("data")) return       //must

        val doo = root["data"] as String

        val uid = agent.UID
        val user = agent.user

        if (mapUserRoom[uid] == null) return
        val table = mapUserRoom[uid] as TableSsz    //获得table
        if (table == null) {
            Send(agent, Msg(0, "illegal request", doo, "msg_ssz"))
            return
        }

        val seat = table.HasUserSeat(user)
        if (seat == -1) {
            Send(agent, Msg(0, "not in room", doo, "msg_ssz"))
            return
        }

        val seatStatus = table.arrSeats[seat]
        if (seatStatus < 1) {
            Send(agent, Msg(0, "no sit", doo, "msg_ssz"))
            return
        }

        var daMap = HashMap<String, String>()

        //避免打牌root里面不存在card
        if (doo.equals("da")) {
            if (!root.has("card")) return
            val card = root["card"].toString()
            daMap.put("card", card)
            daMap.put("data", "da")
        }
        when (doo) {

            "status" -> table.Status(seat)
            "leave" -> table.OnUserLeave(seat)
            "ready" -> table.OnUserReady(seat)

            "da" -> table.DoDa(seat, daMap)
        }


    }


}
