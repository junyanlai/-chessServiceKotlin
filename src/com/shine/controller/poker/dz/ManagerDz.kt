package com.shine.controller.poker.dz

import com.shine.agent.Agent
import com.shine.amodel.Manager
import com.shine.controller.aHall.Hall
import com.shine.controller.poker.maj.ManagerMaj
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

object ManagerDz : Manager {

    override val type = "dz"
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
        val round = detail["round"].toString().toInt()
        val pwd = detail["pwd"].toString()
        var time = detail["time"].toString().toInt()
        var armyBoo = 0;
        if (root.has("armyBoo")) {
            armyBoo = 1
        }

        if (time < 2) time = 10

        val table = TableDz(rid, uid, pwd, di, round, time, armyBoo)
        ManagerDz.mapRoom[rid] = table
        ManagerDz.Send(agent, Msg(rid, "success", "create", "hall_room"))
        table.OnUserSit(agent.user)
    }

    //data=join
    //command=hall_room
    override fun OnJoin(agent: Agent, root: JSONObject) {

        val rid = root["detail"].toString().toInt()
        val table = ManagerDz.mapRoom[rid]

        val pwd = root["type"].toString()
        if (!table?.pwd.equals(pwd)) {
            ManagerDz.Send(agent, ManagerDz.Msg(5, "wrong pwd", "join", "hall_room"))
            return
        }

        val resultJoin = table?.OnUserSit(agent.user)

        when (resultJoin) {
            0 -> ManagerDz.Send(agent, ManagerDz.Msg(resultJoin, "no room", "join", "hall_room"))
            1 -> ManagerDz.Send(agent, ManagerDz.Msg(resultJoin, "success", "join", "hall_room"))
            2 -> ManagerDz.Send(agent, ManagerDz.Msg(resultJoin, "game started", "join", "hall_room"))
            3 -> ManagerDz.Send(agent, ManagerDz.Msg(resultJoin, "no seat", "join", "hall_room"))
            4 -> ManagerDz.Send(agent, ManagerDz.Msg(resultJoin, "wrong", "join", "hall_room"))
            5 -> ManagerDz.Send(agent, ManagerDz.Msg(resultJoin, "wrong pwd", "join", "hall_room"))
        }
    }


    //command=hall_room
    override fun OnList(agent: Agent, root: JSONObject) {

        //  var rids = MutableList(0, { 0 })
        val detail = root["detail"] as JSONObject
        val di = detail["di"].toString().toInt()
        val rids = JSONArray()
        //获取高级房间信息
        if (root.has("senior")) {
            listInfo(agent, root)
            return
        }

        for ((rid, table) in ManagerDz.mapRoom) {
            if (table.type != "dz") continue
            if (table.isStart) continue
            if (table.pwd != "") continue
            //if (table.di != di) continue
            var json = JSONObject()
            json.put("rid", table.rid)       //房间编号
            json.put("type", table.type)     //游戏类型
            json.put("di", table.di)         //低
            json.put("numCur", table.numCur)//当前人数

            rids.put(json)
        }

        //  if (rids.size >= 5) rids = rids.subList(0, 5)

        Send(agent, Msg(rids.length(), rids, "list", "hall_room"))
        return
    }


    //获得房间具体信息
    fun listInfo(agent: Agent, root: JSONObject) {

        val rids = JSONArray()
        val detail = root["detail"] as JSONObject

        val di = detail["di"].toString().toInt()

        for ((rid, table) in mapRoom) {
            if (table.type != "dz") continue
            if (table.isStart) continue
            if (di > table.di) continue
            if (table.numCur == 4) continue
            if (table.pwd != "") continue
            var json = JSONObject()
            json.put("rid", table.rid)       //房间编号
            json.put("type", table.type)     //游戏类型
            json.put("di", table.di)         //低
            json.put("numCur", table.numCur)//当前人数

            rids.put(json)
            Send(agent, Msg(rids.length(), rids, "list", "hall_room"))
            return
        }
        Send(agent, Msg(rids.length(), rids, "list", "hall_room"))
    }

    //command=msg_maj
    //data =leave/ready/ chi/gang/peng/hu/pass
    override fun HandleAll(agent: Agent, root: JSONObject) {

        if (!root.has("detail")) return     //must

        val doo = root["data"] as String
        val detail = root["detail"].toString()

        val uid = agent.UID
        val user = agent.user

        if (ManagerDz.mapUserRoom[uid] == null) return
        val table = ManagerDz.mapUserRoom[uid] as TableDz    //获得table
        if (table == null) {
            ManagerDz.Send(agent, ManagerDz.Msg(0, "illegal request", doo, "msg_dz"))
            return
        }

        val seat = table.HasUserSeat(user)
        if (seat == -1) {
            ManagerDz.Send(agent, ManagerDz.Msg(0, "not in room", doo, "msg_dz"))
            return
        }

        val seatStatus = table.arrSeats[seat]
        if (seatStatus < 1) {
            ManagerDz.Send(agent, ManagerDz.Msg(0, "no sit", doo, "msg_dz"))
            return
        }

        when (doo) {
            "status" -> table.Status(seat)
            "leave" -> table.OnUserLeave(seat)
            "ready" -> table.OnUserReady(seat)

            "stake" -> table.stake(seat, root)
            "allIn" -> table.allIn(seat,root)
            "renounce" -> table.renounce(seat)

            "da" -> table.DoDa(seat, root)
        }


    }


}
