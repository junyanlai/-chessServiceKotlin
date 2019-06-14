package com.shine.controller.poker.xcsl

import com.shine.agent.Agent
import com.shine.amodel.Manager
import com.shine.controller.aHall.Hall
import org.json.JSONObject
import java.util.*

object ManagerSl : Manager {

    override val type = "hall_hangup"
    override val rand = Random()
    override val mapRoom = Hall.gMapRoom
    override val mapUserRoom = Hall.gMapUserRoom

    override fun OnCreate(agent: Agent, root: JSONObject) {
        val uid = agent.UID
        val rid = root["rid"] as Int

        val table = TableSl(rid, uid, "", 0, 0, 0, 0)

        mapRoom[rid] = table
        mapUserRoom.put(uid, table)
        Send(agent, Msg(rid, "success", "create", type))
    }

    override fun HandleAll(agent: Agent, root: JSONObject) {
        val doo = root["data"] as String
        val uid = agent.UID

        if (ManagerSl.mapUserRoom[uid] == null) return

        val table = ManagerSl.mapUserRoom[uid] as TableSl   //获得table
        if (table == null) {
            ManagerSl.Send(agent, ManagerSl.Msg(0, "illegal request", doo, "msg_xcsl"))
            return
        }

        when (doo) {
            "start" -> table.statrCard(agent,root)
        }
    }
}
