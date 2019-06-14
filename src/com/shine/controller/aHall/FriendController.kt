package com.shine.controller.aHall

import com.shine.agent.Agent
import com.shine.amodel.Friend
import com.shine.amodel.User
import com.shine.aservice.friend.FriendService
import com.shine.aservice.notice.NoticeService
import com.shine.aservice.user.UserService
import com.shine.controller.aHall.Hall.getCid
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import  com.shine.controller.aHall.Hall.isOnLine


fun noticeYou(uid: Int) {
    var user: User = UserService.getUserMsgByUID(uid)
    var noticeAgent = Agent()
    noticeAgent.UID = user.uid
    noticeAgent.CID = getCid(user.uid) ?: 1
    noticeAgent.user = user
    NoticeService.sendOftenMsg(noticeAgent, "friend")
}

object FriendController {

    fun HandleAll(agent: Agent, root: JSONObject) {//command,data,type,id
        if (!root.has("data")) return
        val data = root["data"] as String
        when (data) {
            "query" -> Handle_OnQuery(agent, root)
            "apply" -> Handle_OnApply(agent, root)
            "add" -> Handle_OnAdd(agent, root)
            "delete" -> Handle_OnDelete(agent, root)
            "list" -> Handle_OnList(agent, root)
            "recommendation" -> Handle_OnRecommendation(agent, root)
            else -> return
        }
    }


    fun Handle_OnQuery(agent: Agent, root: JSONObject) {
        if (!root.has("nick")) return
        var list: List<User> = FriendService.lookUpFriend(root["nick"] as String)
        var msg = JSONArray()
        for ((i, v) in list.withIndex()) {
            var value = JSONObject()
            value.put("uid", v.uid)
            value.put("nick", v.nick)
            value.put("sex", v.sex)
            value.put("avatar", v.avatar)
            value.put("status", isOnLine(v.uid))
            msg.put(value)
        }
        Send(agent, Msg(1, msg, "query", "hall_friend"))
    }

    fun Handle_OnApply(agent: Agent, root: JSONObject) {
        var friend = Friend()
        var fid = root["fid"] as Int
        friend.uid = agent.UID
        friend.fid = fid

        if (isAddRepeatFriend(agent.UID, fid)) {
            Send(agent, Msg(1, "repeat", "apply", "hall_friend"))
            return
        }

        friend.status = 0
        friend.aid = agent.UID
        if (FriendService.insertFreined(friend) == 1) {
            Send(agent, Msg(1, "success", "apply", "hall_friend"))
            noticeYou(fid)
        } else {
            Send(agent, Msg(1, "fail", "apply", "hall_friend"))
        }
    }

    fun Handle_OnAdd(agent: Agent, root: JSONObject) {
        if (!root.has("uid") or !root.has("fid")) return
        if (FriendService.updateFriend(root["uid"] as Int, root["fid"] as Int) == 1) {
            Send(agent, Msg(1, "success", "add", "hall_friend"))
            noticeYou(root["fid"] as Int)
        } else Send(agent, Msg(1, "fail", "add", "hall_friend"))
    }

    fun Handle_OnDelete(agent: Agent, root: JSONObject) {
        if (!root.has("uid") or !root.has("fid")) return
        if (FriendService.delFriend(root["uid"] as Int, root["fid"] as Int) == 1) {
            Handle_OnList(agent, root)
        } else Send(agent, Msg(1, "fail", "delete", "hall_friend"))

    }

    fun Handle_OnList(agent: Agent, root: JSONObject) {
        if (!root.has("uid")) return
        var uid: String
        if (root["uid"] is Int) {
            uid = root["uid"].toString()
        } else {
            uid = root["uid"] as String
        }
        var set = FriendService.queryOwnFriend(uid.toInt())
        var listFid = FriendService.queryNotApplyFriend(uid.toInt())

        var msg = JSONArray()
        for ((i, v) in listFid.withIndex()) {
            var value = JSONObject()
            var user = UserService.userIdGetUser(v.toInt())
            value.put("applyId", 0)
            value.put("uid", user.uid ?: 0)
            value.put("avatar", user.avatar ?: 0)
            value.put("nick", user.nick ?: 0)
            value.put("sex", user.sex ?: 0)
            value.put("authentication", if (user.name.isBlank()) 0 else 1)
            value.put("expFashion", user.expFashion)
            value.put("tittle", user.tittle)
            value.put("vipLevel", user.vipLevel)
            value.put("status", isOnLine(user.uid ?: 1000))
            msg.put(value)
        }

        for ((i, v) in set.withIndex()) {
            var value = JSONObject()
            var user = UserService.userIdGetUser(v)
            value.put("applyId", 1)
            value.put("uid", user.uid)
            value.put("avatar", user.avatar)
            value.put("nick", user.nick)
            value.put("sex", user.sex)
            value.put("authentication", if (user.name.isBlank()) 0 else 1)
            value.put("expFashion", user.expFashion)
            value.put("tittle", user.tittle)
            value.put("vipLevel", user.vipLevel)
            value.put("status", isOnLine(user.uid))
            msg.put(value)
        }
        Send(agent, Msg(1, msg, "list", "hall_friend"))
    }

    fun Handle_OnRecommendation(agent: Agent, root: JSONObject) {
        var c = UserService.countUser()
        var random = Random().nextInt(c - 3)
        var list = UserService.queryRandomUser(random, 3)
        var msg = JSONArray()
        for ((i, v) in list.withIndex()) {
            var value = JSONObject()
            value.put("avatar", v.avatar)
            value.put("nick", v.nick)
            value.put("sex", v.sex)
            value.put("status", isOnLine(v.uid))
            msg.put(value)
        }
        Send(agent, Msg(1, msg, "Recommendation", "hall_friend"))
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
    fun isAddRepeatFriend(uid: Int, fid: Int): Boolean {
        var count = FriendService.repeatCheck(uid, fid)
        if (count > 0) {
            return true
        }
        return false
    }


}

