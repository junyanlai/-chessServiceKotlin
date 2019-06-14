package com.shine.controller.aHall

import com.shine.agent.Agent
import com.shine.aservice.notice.NoticeService
import org.json.JSONObject


object NoticeController {

    fun HandleAll(agent: Agent, root: JSONObject) {//command,data,type,id
        if (!root.has("data")) return
        val data = root["data"] as String
        when (data) {
            "newest" -> newest(agent, root)
            else -> return
        }
    }

    fun newest(agent: Agent, root: JSONObject) {
        val list = NoticeService.initNoticePanel()
//        var array = JSONArray()
//        list.forEach {
//            var json = JSONObject()
//            json.put("name", it.name)
//            json.put("title", it.title)
//            json.put("news", it.news)
//            json.put("time", it.time)
//            array.put(json)
//        }
//
        var json = JSONObject()
        json.put("name", list[0].name)
        json.put("title", list[0].title)
        json.put("news", list[0].news)
        json.put("time", list[0].time)

        Send(agent, Msg(1, json, "newest", "hall_notice"))
    }

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