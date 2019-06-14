package com.shine.aservice.notice

import com.shine.amodel.Notice
import org.json.JSONObject
import com.shine.agent.Agent
import com.shine.amodel.RecordLog
import com.shine.amodel.User
import com.shine.aservice.army.ArmyUserService
import com.shine.controller.aHall.Hall
import com.shine.controller.aHall.Hall.isOnLine
import com.shine.controller.aHall.Hall.mapSession
import com.shine.dao.NoticeDao
import java.util.concurrent.Executors


object NoticeService : INoticeService {
    val serviceScheduled = Executors.newCachedThreadPool()

    override fun initNoticePanel(): List<Notice> {
        return NoticeDao().initNoticePanel()
    }

    override fun dangerousVictory(name: String, type: String, gold: Int) {
        TODO("not implemented")
    }

    override fun insertNotice(notice: Notice): Int {
        NoticeDao().insertNotice(notice)
        return 1
    }

    override fun updateNotice(notice: Notice) {
        NoticeDao().updateNotice(notice)
    }

    override fun deleteNotice(notice: Notice) {
        NoticeDao().deleteNotice(notice)
    }

    //加入军团公告
    override fun joinArmy(armyId: Int, name: String, armyName: String) {
        ArmyUserService.selectArmyUser(armyId).forEach {
            if (isOnLine(it.uid)) {
                var agent = Agent()
                agent.CID = mapSession[it.uid] ?: 1
                Send(agent, Msg(1, "欢迎 ${name} 加入${armyName} 军团", "army", "auto"))
            }
        }
    }

    override fun jpWinBroadcast(user: User, coin: String) {
        val msg = """恭喜玩家：${user.nick}  在游戏中获得幸运金币: ${coin} 枚"""

        val callable = java.lang.Runnable {
            Hall.sendingNotice(Msg(1, msg, "JP", "auto"))
        }

        //写入日志
        recordLog(user.uid, user.name, "jp", coin)
        serviceScheduled.execute(callable)
    }

    override fun giftBroadcast(user: User, receive: String, cid: Int) {
        val map = HashMap<Int, String>()
        map.put(400009, "玛莎拉蒂")
        map.put(400010, "海景別墅")
        map.put(400008, "藍寶基尼")


        if (map[cid] != null) {

            val msg = "${user.nick} 给 ${receive} 送 ${map[cid]} 礼物 "

            val callable = java.lang.Runnable {
                Hall.sendingNotice(Msg(1, msg, "JP", "auto"))
            }

            //写入日志
            recordLog(user.uid, user.name, "gift", msg)
            serviceScheduled.execute(callable)
        }
    }

    override fun recordLog(uid: Int, name: String, type: String, msg: String) {
        var recordLog = RecordLog()
        recordLog.uid = uid
        recordLog.name = name
        recordLog.type = type
        recordLog.msg = msg
        NoticeDao().recordLog(recordLog)
    }

    //type:friend b n
    override fun sendOftenMsg(agent: Agent, type: String) {
        if (isOnLine(agent.user.uid)) {
            Send(agent, Msg(1, "", "new", "hall_${type}"))
        }
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