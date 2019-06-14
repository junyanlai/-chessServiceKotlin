package com.shine.controller.aHall

import com.shine.agent.Agent
import com.shine.amodel.Room
import com.shine.amodel.User
import com.shine.controller.gamble.saima.ManagerSm
import com.shine.controller.poker.cdd.ManagerCdd
import com.shine.controller.poker.maj.ManagerMaj
import com.shine.controller.gamble.toubao.ManagerTb
import com.shine.controller.poker.Landlords.ManagerDdz
import com.shine.controller.poker.ddz.TableDdz
import com.shine.controller.poker.dz.ManagerDz
import com.shine.controller.poker.nn.ManagerNN
import com.shine.controller.poker.ssz.ManagerSsz
import com.shine.controller.poker.xcsl.ManagerLh
import com.shine.controller.poker.xcsl.ManagerSl
import org.json.JSONObject


object RoomController {

    val RoomAgent = Agent()
    val RoomUser = User()
    val mapRoom = Hall.mapRoom
    val mapUserRoom = Hall.mapUserRoom
    var mapUserLeaveRom=Hall.mapUserLeaveRom
    val mapHalfRoom_maj: MutableMap<Int, Room> = HashMap()       //half leave players and the room |<uid,Room>
    val mapHalfRoom_cdd: MutableMap<Int, Room> = HashMap()       //half leave players and the room |<uid,Room>
    val mapHalfRoom_dzz: MutableMap<Int, Room> = HashMap()       //half leave players and the room |<uid,Room>
    val mapHalfRoom_ssz: MutableMap<Int, Room> = HashMap()       //half leave players and the room |<uid,Room>
    val mapHalfRoom_nn: MutableMap<Int, Room> = HashMap()       //half leave players and the room |<uid,Room>
    val mapHalfRoom_dz: MutableMap<Int, Room> = HashMap()       //half leave players and the room |<uid,Room>
    //command=hall_room
    fun HandleAll(agent: Agent, root: JSONObject) {//command,data,type,id
        if (!root.has("detail")) return
        if (!root.has("type")) return
        val data = root["data"] as String
        when (data) {
            "create" -> Handle_OnCreate(agent, root)
            "inRoom"->ManagerDdz.returnRomeMsg(agent,root)
            "inner"  ->Handle_OnInner(agent, root)
            "join" -> Handle_OnJoin(agent, root)
            "list" -> Handle_OnList(agent, root)
            "viplist" -> Handle_OnVipList(agent, root)
            "current" -> Handle_OnCurrent(agent, root)
            "leave" -> return
            "dismiss" -> return
            "return" -> return
            else -> return
        }
    }
    fun  Handle_OnInner(agent: Agent, root: JSONObject){
        val type = root["type"].toString()
        if (!mapUserLeaveRom.containsKey(agent.user.uid)) {
//            println("没有进入过房间：")
            Send(agent, Msg(1, "noInnerRoom", "create", "hall_room"))
            return
        }else{
            var tab= mapUserLeaveRom[agent.user.uid] as TableDdz
            Send(agent, ManagerDdz.Msg(1, tab.rid, "haveRoom", "hall_room"))
        }
    }

    //data=create
    fun Handle_OnCreate(agent: Agent, root: JSONObject) {

        val uid = agent.UID
        val type = root["type"].toString()

        if ((mapUserRoom.containsKey(uid)) && (mapUserRoom[uid]?.type == type)) {
            Send(agent, Msg(0, "", "create", "hall_room"))
            return
        }
        when (type) {
            "maj" -> ManagerMaj.OnCreate(agent, root)
            "cdd" -> ManagerCdd.OnCreate(agent, root)
            "ddz" -> ManagerDdz.OnCreate(agent, root)
            "nn" -> ManagerNN.OnCreate(agent, root)
            "ssz" -> ManagerSsz.OnCreate(agent, root)
            "dz" -> ManagerDz.OnCreate(agent, root)
            "lhj" -> ManagerLh.OnCreate(agent, root)
            "xcsl" -> ManagerSl.OnCreate(agent, root)
            else -> return
        }
    }

    //data=join
    fun Handle_OnJoin(agent: Agent, root: JSONObject) {

        val rid = root["detail"].toString().toInt()

        if (mapRoom[rid] == null) {
            Send(agent, Msg(0, "no room", "join", "hall_room"))
            return
        }

        var roomType = ""
        mapRoom[rid]?.let {
            roomType = it.type
        }

        when (roomType) {
            "" -> return
            "maj" -> ManagerMaj.OnJoin(agent, root)
            "cdd" -> ManagerCdd.OnJoin(agent, root)
            "ddz" -> ManagerDdz.OnJoin(agent, root)
            "tb" -> ManagerTb.OnJoin(agent, root)
            "sm" -> ManagerSm.OnJoin(agent, root)
            "nn" -> ManagerNN.OnJoin(agent, root)
            "ssz" -> ManagerSsz.OnJoin(agent, root)
            "dz" -> ManagerDz.OnJoin(agent, root)
            "ml" -> ManagerMl.OnJoin(agent, root)
            "cb" -> ManagerCb.OnJoin(agent, root)
        }
    }

    //data=list
    fun Handle_OnList(agent: Agent, root: JSONObject) {

        val type = root["type"] as String
        when (type) {
            "maj" -> ManagerMaj.OnList(agent, root)
            "cdd" -> ManagerCdd.OnList(agent, root)
            "ddz" -> ManagerDdz.OnList(agent, root)
            "tb" -> ManagerTb.OnList(agent, root)
            "nn" -> ManagerNN.OnList(agent, root)
            "ssz" -> ManagerSsz.OnList(agent, root)
            "dz" -> ManagerDz.OnList(agent, root)
            "ml" -> ManagerMl.OnList(agent, root)
            "cb" -> ManagerCb.OnList(agent, root)
            "lhj" -> ManagerLh.OnList(agent, root)
            "xcsl" -> ManagerSl.OnList(agent, root)
            else -> return
        }
    }

    //data=viplist
    fun Handle_OnVipList(agent: Agent, root: JSONObject) {

        val type = root["type"] as String
        when (type) {
            "maj" -> return
            "cdd" -> ManagerCdd.OnVipList(agent, root)
            "ddz" -> return
            "nn" -> return
            "ssz" -> return
            else -> return
        }
    }


    //data=current
    fun Handle_OnCurrent(agent: Agent, root: JSONObject) {

        val uid = agent.UID
        val type = root["type"].toString()

        if (type.equals("")) {
            val detail = MutableList(0, { 0 })
            mapHalfRoom_maj[uid]?.let { detail.add(it.rid) }
            mapHalfRoom_cdd[uid]?.let { detail.add(it.rid) }
            mapHalfRoom_ssz[uid]?.let { detail.add(it.rid) }
            mapHalfRoom_nn[uid]?.let { detail.add(it.rid) }

            Send(agent, Msg(detail.size, detail, "current", "hall_room"))
        } else {
            var rid = 0
            when (type) {
                "maj" -> mapHalfRoom_maj[uid]?.let { rid = (it.rid) }
                "cdd" -> mapHalfRoom_cdd[uid]?.let { rid = (it.rid) }
                "dzz" -> mapHalfRoom_dzz[uid]?.let { rid = (it.rid) }
                "ssz" -> mapHalfRoom_ssz[uid]?.let { rid = (it.rid) }
            }
            Send(agent, Msg(1, rid, "current", "hall_room"))
        }
    }


    //get SendMsg
    fun Msg(result: Any?, detail: Any?, data: Any?, command: Any?): String {
        val msg = JSONObject()
        msg.put("command", command)
        msg.put("result", result)
        msg.put("data", data)
        msg.put("detail", detail)
//        val msg ="{WRhyz/xNGSIHVKylW1x4SsvtPbWrojJA9pSgSP3y+ahfi++17Bb8GVFBZTmEDwlCTj/54/sINHoKwAFUPEZ0w8aHrfhvVmQWVkajl56R3MaBvKZ2Z4l6gwqG625GC0cytRP7l/HszelWvNyAUUAQBJ8hLTBVDrLJM+Bth23YLtGZIo0D3QWCsEN/YiiMOa1z/PbCE4c/WKwiXU4Sm3+VBDFeInlmbBO+bzp/ItyObBqdEzYu8YWBWg9QNwBgYsLv2pclkIWxtICB3I65RiCIdzmQ+qUe1OvZx1TxftCZhzyGBzhuiGJbUYBdAZYNjhu1qlRYxLTs0bl4SRMPeUgVx4YHOG6IYltRVgmpId8EAJ6dzbnd0yAOXGSm5zxMzVA4q4f4cwE4P9ZysSlrEnaTSOwVEkEyjoZnhgc4bohiW1FFGjJfnf7h3xsy6xP/NuMgUMeoi0pZdITf8zGiYd8U8I8biDUL1ezEBDLH3COpIgfiAgEDwoNSGhIonrijjApIhgc4bohiW1ERTQVF/QzAUkcKHY0clwOAfV7koIUWoBBVReXl9a+kJhsy6xP/NuMgjwCbbc7aSHt7ekugSVuBK79TImOAHCCQnWWenutYYv/iAgEDwoNSGoU7HWhy8PDIe3pLoElbgSuGkqnR3WS3WUUrr3kvT3BI35hhcpIzxyRfjtdegnXCKEstjrrCCGw8KwJ7iPQBgFfkTvoV3dqtbG6qqBPTkM4mqhMbrsqNVmxuE+QRwKFzs0stjrrCCGw8NT1lH7tN8/IbMusT/zbjIFiGoy9ARuEAcrEpaxJ2k0hOQVkxCo00g3KxKWsSdpNIDqRNHU761iAud6+WWAM/P3KxKWsSdpNIUxp4boKDT3mGBzhuiGJbUTXGVhkSUYSIcrEpaxJ2k0hxGRTNBOywH3KxKWsSdpNIp1cgxkUOWrx57KEXJv2/qmltmugEbLPyuRa3fS3mF21OZofN2YmfsKhoUL+US0XVo2qjlNhDOQmI2D/E7RiPlcVUP+/E/dRw04nla0gJpzsIip8rsqgLUSGN6Yg1EzwY}"
        return msg.toString()
    }

    //get SendMsg
    fun Msg(result: Any?, detail: Any?, data: Any?, command: Any?, count: Any?): String {
        val msg = JSONObject()
        msg.put("command", command)
        msg.put("result", result)
        msg.put("data", data)
        msg.put("detail", detail)
        msg.put("count", count)
        return msg.toString()
    }

    fun Send(agent: Agent, msg: String) = agent.Send(agent.CID, msg)
}