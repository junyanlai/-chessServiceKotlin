package com.shine.controller.gamble.saima

import com.shine.agent.Agent
import com.shine.amodel.Manager
import com.shine.controller.aHall.Hall
import org.json.JSONObject
import java.util.*

/**
 *  Create by Colin
 *  Date:2018/6/27.
 *  Time:9:51
 *  14803121
 */
object ManagerSm : Manager {

    override val type = "sm"
    override val rand = Random()
    override val mapRoom = Hall.mapRoom
    override val mapUserRoom = Hall.mapUserRoom

    fun Create() {
        val rid1 = 14803121
        val rid2 = 14803122
        val room1 = TableSm(rid1, 10, 0)
        val room2 = TableSm(rid2, 100, 0)

        room1.RoundStart()
        room2.RoundStart()
        mapRoom[rid1] = room1
        mapRoom[rid2] = room2
    }

    override fun OnJoin(agent: Agent, root: JSONObject) {
        val rid = root["detail"].toString().toInt()
        val table = mapRoom[rid] as TableSm

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

    override fun HandleAll(agent: Agent, root: JSONObject) {
        if (!root.has("card")) return
        if (!root.has("detail")) return
        val doo = root["data"] as String
        val card = root["card"].toString()

        val cards = card
                .removeSurrounding("[", "]")
                .split(",")
                .map { it.toInt() }
                .toIntArray()

        val uid = agent.UID
        val user = agent.user
        if (mapUserRoom[uid] == null) return
        val table = mapUserRoom[uid] as TableSm

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
            "stake" -> table.Stake(seat, cards)
        }
    }
}