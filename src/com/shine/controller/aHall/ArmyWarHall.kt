package com.shine.controller.aHall

import com.shine.agent.Agent
import com.shine.amodel.*
import com.shine.aservice.army.ArmyAdminService
import com.shine.aservice.army.ArmyRankingService
import com.shine.aservice.army.ArmyUserService
import com.shine.controller.aHall.Hall.gameStatus
import com.shine.controller.poker.cdd.ManagerCdd
import com.shine.controller.poker.dz.ManagerDz
import com.shine.controller.poker.maj.ManagerMaj
import com.shine.controller.poker.nn.ManagerNN
import com.shine.controller.poker.ssz.ManagerSsz
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object ArmyWarHall {

    val mapRoom: MutableMap<Int, Room> = Hall.mapRoom           //rid,room
    val mapUser: MutableMap<Int, User> = HashMap()              //uid,user

    val mapUserRoom: MutableMap<Int, Room> = HashMap()      //uid,rid

    val serviceScheduled = Executors.newSingleThreadScheduledExecutor()
    var count = 0

    fun isOnLine(uid: Int): Boolean = mapUser.containsKey(uid)


    fun HandleAll(agent: Agent, root: JSONObject) {

        if (!root.has("data")) return
        val data = root["data"] as String

        when (data) {
            "getdate" -> actionDate(agent, root)
            "check" -> checkUID(agent, root)
            "status" -> status(agent, root)
            else -> warHall(agent, root)
        }
    }

    fun warHall(agent: Agent, root: JSONObject) {
        val data = root["data"] as String

        when (data) {
            "signUp" -> signUpGame(agent, root)
            "allPeople" -> allPeople(agent, root)
            "ranking" -> meansFinal(agent, root)
            "maj" -> GameStartMaj(agent, root)
            "nn" -> GameStartNn(agent, root)
            "ssz" -> GameStartSsz(agent, root)
            "cdd" -> GameStartCdd(agent, root)
            "dz" -> GameStartDz(agent, root)

        }
    }

    fun status(agent: Agent, root: JSONObject) {
        var json = JSONObject()

        if (gameStatus.size == 0) {
            Send(agent, Msg(1, "status", "fail", "hall_armyWar"))
        } else {
            json.put("boo", gameStatus.first.boo)
            json.put("type", gameStatus.first.type)
            Send(agent, Msg(1, "status", json, "hall_armyWar"))
        }
    }


    fun checkUID(agent: Agent, root: JSONObject) {
        if (mapUser[agent.user.uid] != null) {
            Send(agent, Msg(1, "check", "true", "hall_armyWar"))
        } else {
            Send(agent, Msg(1, "check", "false", "hall_armyWar"))
        }
    }


    fun actionDate(agent: Agent, root: JSONObject) {
        Send(agent, Msg(1, "getdate", ":-)", "hall_armyWar"))
    }


    /**
     * 所有参加军团战报名的玩家
     */
    fun signUpGame(agent: Agent, root: JSONObject) {
        val uid = agent.user.uid
        if (mapUser[uid] == null) {
            mapUser.put(uid, agent.user)
            Send(agent, Msg(1, "signUp", "success", "hall_armyWar"))
        } else {
            Send(agent, Msg(1, "signUp", "repeat", "hall_armyWar"))
        }
    }


    fun allPeople(agent: Agent, root: JSONObject) {
        val array = JSONArray()
        mapUser.forEach { t, u ->
            val json = JSONObject()
            json.put("nick", u.nick)
            json.put("avatar", u.avatar)
            array.put(json)
        }

        Send(agent, Msg(1, "allPeople", array, "hall_armyWar"))
    }

    fun GameStartCdd(agent: Agent, root: JSONObject) {
        if (!isOnLine(agent.UID)) {
            Send(agent, Msg(1, "cdd", "no", "hall_armyWar"))
            return
        }

        var room = whetherStartGame(root)
        if (room.size != 0) {
            root.put("detail", room[0])
            root.put("type", "")
            ManagerCdd.OnJoin(agent, root)
        } else {
            root.put("armyBoo", 1)
            ManagerCdd.OnCreate(agent, root)
        }
    }

    fun GameStartDz(agent: Agent, root: JSONObject) {
        if (!isOnLine(agent.UID)) {
            Send(agent, Msg(1, "dz", "no", "hall_armyWar"))
            return
        }

        var room = whetherStartGame(root)
        if (room.size != 0) {
            root.put("detail", room[0])
            root.put("type", "")
            ManagerDz.OnJoin(agent, root)
        } else {
            root.put("armyBoo", 1)
            ManagerDz.OnCreate(agent, root)
        }
    }

    fun GameStartMaj(agent: Agent, root: JSONObject) {

        if (!isOnLine(agent.UID)) {
            Send(agent, Msg(1, "maj", "no", "hall_armyWar"))
            return
        }

        var room = whetherStartGame(root)
        if (room.size != 0) {
            root.put("detail", room[0])
            ManagerMaj.OnJoin(agent, root)
        } else {
            root.put("armyBoo", 1)      //军团战爆发了
            ManagerMaj.OnCreate(agent, root)
        }
    }

    fun GameStartNn(agent: Agent, root: JSONObject) {
        if (!isOnLine(agent.UID)) {
            Send(agent, Msg(1, "nn", "no", "hall_armyWar"))
            return
        }

        var room = whetherStartGame(root)

        if (room.size != 0) {
            root.put("detail", room[0])
            root.put("type", "")
            ManagerNN.OnJoin(agent, root)
        } else {
            root.put("armyBoo", 1)
            ManagerNN.OnCreate(agent, root)
        }
    }

    fun GameStartSsz(agent: Agent, root: JSONObject) {

        if (!isOnLine(agent.UID)) {
            Send(agent, Msg(1, "ssz", "no", "hall_armyWar"))
            return
        }

        //获得游戏房间
        var room = whetherStartGame(root)
        if (room.size > 0) {
            root.put("detail", room[0])
            ManagerSsz.OnJoin(agent, root)
        } else {
            root.put("armyBoo", 1)
            ManagerSsz.OnCreate(agent, root)
        }
    }


    //判断是否有空余的房间
    fun whetherStartGame(root: JSONObject): MutableList<Int> {
        val rids = MutableList(0, { 0 })

        for ((rid, table) in mapRoom) {
            if (table.type != root["data"] as String) continue
            if (table.isStart) continue
            if (table.numCur == 4) continue
            if (table.pwd != "") continue
            if (table.armyBoo != 1) continue
            rids.add(rid)
        }
        return rids
    }

    // 军团积分排行榜初始化
    fun armyRanking() {
        var armyIdList = ArrayList<Int>(0)
        mapUser.forEach { t, u ->
            var armyUser = ArmyUser()
            armyUser.uid = t
            armyUser = ArmyUserService.selectArmyUserOne(armyUser)
            armyIdList.add(armyUser.armyId)
        }

        var set = armyIdList.toSet()
        for ((i, v) in set.withIndex()) {
            val army = ArmyAdminService.getArmyInfo(v)
            var r = ArmyRanking(0, 0, "0", "0", "0")
            r.id = army.id
            r.icon = army.icon
            r.name = army.name
            r.armyTitle = army.armyTitle
            ArmyRankingService.insertRanking(r)
        }

    }


    //获取军团积分排行榜
    fun meansFinal(agent: Agent, root: JSONObject) {
        val list = ArmyRankingService.queryAll()
        val array = JSONArray()
        for ((i, v) in list.withIndex()) {
            var json = JSONObject()
            json.put("name", v.name)
            json.put("icon", v.icon)
            json.put("armyTitle", v.armyTitle)
            json.put("score", v.score)
            array.put(json)
        }

        Send(agent, Msg(1, "ranking", array, "hall_armyWar"))
    }


    // 调用军团 存入分数
    fun armyScore(user: User, score: Int) {
        if (score < 0) return
        if (user.uid == 1) return
        val armyUser = ArmyAdminService.userArmyInfo(user.uid)
        val s = ArmyRankingService.queryByArmyId(armyUser.armyId).score + score
        ArmyRankingService.updateRanking(armyUser.armyId, s.toLong())
    }


    fun Msg(result: Any?, data: Any?, detail: Any?, command: Any?): String {
        val msg = JSONObject()
        msg.put("command", command)
        msg.put("result", result)
        msg.put("data", data)
        msg.put("detail", detail)
        return msg.toString()
    }
    fun Send(agent: Agent, msg: String) = agent.Send(agent.CID, msg)
}
