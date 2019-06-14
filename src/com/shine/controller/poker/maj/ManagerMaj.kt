package com.shine.controller.poker.maj

import com.shine.agent.Agent
import com.shine.amodel.Room
import com.shine.amodel.room0
import com.shine.controller.aHall.Hall
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

object ManagerMaj {
    //https://blog.csdn.net/icehaopan/article/details/50261623

    val type = "maj"
    val rand = Random()
    val mapRoom = Hall.mapRoom
    val mapUserRoom = Hall.mapUserRoom

    //data=create
    //command=hall_room
    fun OnCreate(agent: Agent, root: JSONObject) {//command=hall_room,data=create,type=maj

        val uid = agent.UID
        val rid = RidSafeGet()
        val detail = root["detail"] as JSONObject

        val di = detail["di"].toString().toInt()
        val tai = detail["tai"].toString().toInt()
        val round = detail["round"].toString().toInt()
        val pwd = detail["pwd"].toString()

        var time = detail["time"].toString().toInt()
        val ting = detail["ting"].toString().toBoolean()
        val zimo = detail["zimo"].toString().toBoolean()
        val bao = detail["bao"].toString().toBoolean()
        val men = detail["men"].toString().toBoolean()
        var armyBoo = 0;
        if (root.has("armyBoo")) {
            armyBoo = 1
        }
        if (time < 2) time = 10

        val table = TableMaj(rid, uid, pwd, di, tai, round, time, ting, zimo, bao, men, armyBoo)
        mapRoom[rid] = table
        Send(agent, Msg(rid, "create success", "create", "hall_room"))

        // println("#_________RoomCreate: $rid")

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
        val tables = MutableList(0, { room0() })
        val detail = root["detail"] as JSONObject

        //获取高级房间信息
        if (root.has("senior")) {
            listInfo(agent, root)
            return
        }

        val di = detail["di"].toString().toInt()
        val tai = detail["tai"].toString().toInt()
        val round = detail["round"].toString().toInt()

        for ((rid, table) in mapRoom) {
            if (table.type != "maj") continue
            if (table.isStart) continue
            if (table.di != di) continue
            if (table.numCur == 4) continue
            if (table.pwd != "") continue
            rids.add(rid)
            Send(agent, Msg(rids.size, rids, "list", "hall_room"))
            return
        }

        Send(agent, Msg(rids.size, rids, "list", "hall_room"))
    }

    //获得房间具体信息
    fun listInfo(agent: Agent, root: JSONObject) {

        val rids = JSONArray()
        val detail = root["detail"] as JSONObject

        val di = detail["di"].toString().toInt()

        for ((rid, table) in mapRoom) {
            if (table.type != "maj") continue
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
    //data=leave/ready/ chi/gang/peng/hu/pass
    fun HandleAll(agent: Agent, root: JSONObject) {

        if (!root.has("card")) return       //must
        if (!root.has("detail")) return     //must

        val doo = root["data"] as String
        val card = root["card"].toString().toInt()
        val detail = root["detail"].toString().toInt()

        val uid = agent.UID
        val user = agent.user

        if (mapUserRoom[uid] == null) return
        val table = mapUserRoom[uid] as TableMaj
        if (table == null) {
            Send(agent, Msg(0, "illegal request", doo, "msg_maj"))
            return
        }

        val seat = table.HasUserSeat(user)
        //println("是否有seat结果=$seat")
        if (seat == -1) {
            Send(agent, Msg(0, "not in room", doo, "msg_maj"))
            return
        }

        val seatStatus = table.arrSeats[seat]
        if (seatStatus < 1) {
            Send(agent, Msg(0, "no sit", doo, "msg_maj"))
            return
        }

        when (doo) {
            "status" -> table.Status(seat)
            "leave" -> table.OnUserLeave(seat)
            "ready" -> table.OnUserReady(seat)

            "da" -> table.DoDa(seat, card)
            "zimo" -> table.DoZimo(seat)
            "angang" -> table.DoAngang(seat, card)
            "jiagang" -> table.DoJiagang(seat, card)
            "qianggang" -> table.DoJiagang(seat, card)
            "ting" -> table.DoTing(seat, card)

            "chi" -> table.Deal(seat, root)
            "gang" -> table.Deal(seat, root)
            "peng" -> table.Deal(seat, root)
            "hu" -> table.Deal(seat, root)
            "pass" -> table.Deal(seat, root)
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