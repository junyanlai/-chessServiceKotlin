package com.shine.aservice.notice

import com.shine.agent.Agent
import com.shine.amodel.Notice
import com.shine.amodel.User

interface INoticeService {

    /**
     * 游戏加载面板公告
     */
    fun initNoticePanel(): List<Notice>

    /**
     * 游戏险胜获得大量金币公告
     */
    fun dangerousVictory(name: String, type: String, gold: Int)

    /**
     * 加入军团公告
     */
    fun joinArmy(armyId: Int, name: String, armyName: String)

    /**
     * jp系统公告-全服级
     */
    fun jpWinBroadcast(user: User, coin: String)

    /**
     * 礼物公告-全服级
     */
    fun giftBroadcast(user: User, receive: String, cid: Int)

    /**
     * 常规性日常通知（新好友申请通知，新邮件通知）
     */
    fun sendOftenMsg(agent: Agent, type: String)

    fun insertNotice(notice: Notice): Int

    fun updateNotice(notice: Notice)

    fun deleteNotice(notice: Notice)

    /**
     * 日志记录
     */
    fun recordLog(uid: Int, name: String, type: String, msg: String)


}