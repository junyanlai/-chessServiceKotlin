package com.shine.controller.poker.xcsl

import com.shine.agent.Agent
import com.shine.amodel.Manager
import com.shine.controller.aHall.Hall
import org.json.JSONObject
import java.util.*

object ManagerLh : Manager {

    override val type = "hall_hangup"
    override val rand = Random()
    override val mapRoom = Hall.gMapRoom
    override val mapUserRoom = Hall.gMapUserRoom

    override fun OnCreate(agent: Agent, root: JSONObject) {
        val uid = agent.UID
        val rid = root["rid"] as Int

        val table = TableLh(rid, uid, "", 0, 0, 0, 0)
        mapRoom[rid] = table
        mapUserRoom.put(uid, table)
        Send(agent, Msg(rid, "success", "create", type))
    }

    override fun HandleAll(agent: Agent, root: JSONObject) {
        val doo = root["data"] as String

        var coin = 0
        if (root.has("stake")) {
            coin = root["stake"] as Int
        }

        val uid = agent.UID
        if (ManagerSl.mapUserRoom[uid] == null) return
        val table = ManagerLh.mapUserRoom[uid] as TableLh    //获得table

        when (doo) {
            "startNew" -> table.statrNew(agent, coin.toLong())
        }


    }


}
