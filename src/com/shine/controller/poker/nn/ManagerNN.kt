package com.shine.controller.poker.nn

import com.shine.agent.Agent
import com.shine.amodel.Manager
import com.shine.controller.aHall.Hall
import org.json.JSONObject
import java.util.*

/**
 *  Create by Colin
 *  Date:2018/7/4.
 *  Time:9:18
 */
object ManagerNN : Manager {
    override val type = "nn"
    override val rand = Random()
    override val mapRoom = Hall.mapRoom
    override val mapUserRoom = Hall.mapUserRoom

    override fun OnCreate(agent: Agent, root: JSONObject) {
        val uid = agent.UID
        val rid = RidSafeGet()
        val detail = root["detail"].toString().toInt()
        var armyBoo = 0;
        if (root.has("armyBoo")) {
            armyBoo = 1
        }

        val table = TableNN(rid, uid, detail, armyBoo)
        mapRoom[rid] = table
        Send(agent, Msg(rid, "create success", "create", "hall_room"))

        table.OnUserSit(agent.user)
        table.TimeOut_DoAi()
    }

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

    override fun OnList(agent: Agent, root: JSONObject) {
        val rids = MutableList(0, { 0 })
        val di = root["detail"].toString().toInt()
        for ((rid, table) in mapRoom) {
            if (table.type != "nn") continue
            if (table.isStart) continue
            if (table.pwd != "") continue
            if (table.di != di) continue
            rids.add(rid)
        }
        Send(agent, Msg(rids.size, rids, "list", "hall_room"))
    }

    override fun OnVipList(agent: Agent, root: JSONObject) {}

    override fun HandleAll(agent: Agent, root: JSONObject) {
        if (!root.has("card")) return       //must
        if (!root.has("detail")) return     //must

        val doo = root["data"] as String
        val card = root["card"].toString().toInt()
        val detail = root["detail"].toString().toInt()

        val uid = agent.UID
        val user = agent.user

        if (mapUserRoom[uid] == null) return
        val table = mapUserRoom[uid] as TableNN
        if (table == null) {
            Send(agent, Msg(0, "illegal request", doo, "msg_" + type))
            return
        }

        val seat = table.HasUserSeat(user)
        if (seat == -1) {
            Send(agent, Msg(0, "not in room", doo, "msg_" + type))
            return
        }

        val seatStatus = table.arrSeats[seat]
        if (seatStatus < 1) {
            Send(agent, Msg(0, "no sit", doo, "msg_" + type))
            return
        }

        when (doo) {
            "status" -> table.Status(seat)
            "leave" -> table.OnUserLeave(seat)
            "ready" -> table.OnUserReady(seat)

            "qiang" -> table.OnQiang(seat)
            "bet" -> table.OnBet(seat, card)
        }
    }


    override fun RidSafeGet(): Int {

        val rid = Rand(100000, 999999)
        if (mapUserRoom[rid] == null) return rid
        else return RidSafeGet()
    }
}