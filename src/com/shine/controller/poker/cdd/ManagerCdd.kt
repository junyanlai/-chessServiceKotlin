package com.shine.controller.poker.cdd

import com.shine.agent.Agent
import com.shine.controller.aHall.Hall
import org.json.JSONObject
import java.util.*

/**
 *  Create by Colin
 *  Date:2018/6/7.
 *  Time:18:05
 */
object ManagerCdd {
    val type = "cdd"
    val rand = Random()
    val mapRoom = Hall.mapRoom
    val mapUserRoom = Hall.mapUserRoom

    //data=create
    //command=hall_room
    fun OnCreate(agent: Agent, root: JSONObject) {

        val uid = agent.UID
        val rid = RidSafeGet()
        val detail = root["detail"] as JSONObject

        val pwd = detail["pwd"].toString()
        val di = detail["di"].toString().toInt()
        val round = detail["round"].toString().toInt()
        var time = detail["time"].toString().toInt()

        if (time < 2) time = 10 else time

        var armyBoo = 0;
        if (root.has("armyBoo")) {
            armyBoo = 1
        }

        val table = TableCdd(rid, uid, pwd, di, round, time - 1, true, armyBoo)
        mapRoom[rid] = table
        Send(agent, Msg(rid, "create success", "create", "hall_room"))

        table.OnUserSit(agent.user)
        table.TimeOut_DoAi()
    }

    //data=join
    //command=hall_room
    fun OnJoin(agent: Agent, root: JSONObject) {

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

    //data=list
    //command=hall_room
    fun OnList(agent: Agent, root: JSONObject) {

        val rids = MutableList(0, { 0 })
        val detail = root["detail"] as JSONObject

        val di = detail["di"].toString().toInt()
        val time = detail["time"].toString().toInt()
        val round = detail["round"].toString().toInt()

        if (di == 0 && di == time && time == round) {

            for ((rid, table) in mapRoom) {
                if (table.type != type) continue
                if (table.isStart) continue
                if (table.numCur == 4) continue
                if (table.pwd != "") continue
                rids.add(rid)
            }

            Send(agent, Msg(rids.size, rids, "list", "hall_room"))
            return
        }

        for ((rid, table) in mapRoom) {
            if (table.type != type) continue

            if (table.isStart) continue
            if (table.numCur == 4) continue
            if (table.pwd != "") continue
            if (table.di != di) continue

            if (table.roundMax != round) continue

            rids.add(rid)
        }
        Send(agent, Msg(rids.size, rids, "list", "hall_room"))
    }

    //data=viplist
    //command=hall_room
    fun OnVipList(agent: Agent, root: JSONObject) {

        val rids = MutableList(0, { 0 })
        val roomlist = JSONObject()
        val detail = root["detail"] as JSONObject

        val di = detail["di"].toString().toInt()
        val time = detail["time"].toString().toInt()
        val round = detail["round"].toString().toInt()

        if (di == 0 && di == time && time == round) {

            for ((rid, table) in mapRoom) {
                if (table.type != type) continue
                if (table.numCur == 4) continue

                rids.add(rid)
                val tab = JSONObject()
                tab.put("di", table.di)
                tab.put("mTime", table.mTime)
                tab.put("roundCur", table.roundCur)
                tab.put("roundMax", table.roundMax)
                tab.put("password", !table.pwd.equals(""))
                tab.put("isStart", table.isStart)


                roomlist.put(rid.toString(), tab)
            }
            roomlist.put("rids", rids)

            Send(agent, Msg(rids.size, roomlist, "viplist", "hall_room"))
            return
        }

    }


    //command=msg_maj
    //data=leave/ready/ chi/gang/peng/hu/pass
    fun HandleAll(agent: Agent, root: JSONObject) {

        if (!root.has("card")) return       //must
        if (!root.has("detail")) return     //must

        val doo = root["data"] as String
        val card = root["card"].toString()
        val detail = root["detail"].toString().toInt()

        val cards = card
                .removeSurrounding("[", "]")
                .split(",")
                .map { it.toInt() }
                .toIntArray()

        val uid = agent.UID
        val user = agent.user
        if (mapUserRoom[uid] == null) return
        val table = mapUserRoom[uid] as TableCdd
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

            "pass" -> table.DoPass(seat)
            "noSwitch" -> table.noSwitch(seat)
            "da" -> table.DoDa(seat, cards)
            "switch" -> table.DoSwitch(seat, cards)
        }
    }


    //get RoomId
    fun Rand(min: Int, max: Int) = rand.nextInt(max - min) + min

    fun RidSafeGet(): Int {

        val rid = Rand(100000, 999999)
        if (mapUserRoom[rid] == null) return rid
        else return RidSafeGet()
    }

    //get SendMsg
    fun Msg(result: Any?, detail: Any?, data: Any?, command: Any?): String {
        val msg = JSONObject()
        msg.put("command", command)
        msg.put("result", result)
        msg.put("data", data)
        msg.put("detail", detail)
        return msg.toString()
    }

    fun Send(agent: Agent, msg: String) = agent.Send(agent.CID, msg)
}