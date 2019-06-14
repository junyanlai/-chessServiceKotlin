package com.shine.controller.aHall

import com.shine.agent.Agent
import com.shine.aservice.ranking.RankingService
import org.json.JSONObject

object RankingController {


    fun HandleAll(agent: Agent, root: JSONObject) {
        if (!root.has("data")) return
        val data = root["data"] as String
        when (data) {
            "" -> return
            "glamour" -> glamour(agent, root)
            "gold" -> gold(agent, root)
            "army" -> army(agent, root)
            else -> return
        }
    }

    fun glamour(agent: Agent, root: JSONObject) {
        val json = RankingService.glamour()
        json.let {
            Send(agent, Msg(1, "glamour", json, "hall_ranking"))
        }
    }

    fun gold(agent: Agent, root: JSONObject) {
        val json = RankingService.gold()
        json.let {
            Send(agent, Msg(1, "gold", json, "hall_ranking"))
        }
    }

    fun army(agent: Agent, root: JSONObject) {
        val json = RankingService.armyGroup()
        json.let {
            Send(agent, Msg(1, "army", json, "hall_ranking"))
        }
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