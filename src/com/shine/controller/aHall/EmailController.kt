package com.shine.controller.aHall

import com.shine.agent.Agent
import com.shine.amodel.*
import com.shine.aservice.eamil.EmailService
import com.shine.aservice.eamil.EmailService.checkOneMail
import com.shine.aservice.eamil.EmailService.rechargeCount
import com.shine.aservice.notice.NoticeService
import com.shine.aservice.notice.NoticeService.recordLog
import com.shine.aservice.shop.GoodsService.goodsSelectOne
import com.shine.aservice.shop.RechargeService.insertRecharge
import com.shine.aservice.shop.StoreService
import com.shine.aservice.user.UserService
import com.shine.aservice.util.FeedbackMail
import com.shine.controller.aHall.Hall.getCid
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.LocalDateTime


object EmailController {

    fun HandleAll(agent: Agent, root: JSONObject) {//command,data,type,id
        if (!root.has("data")) return

        val data = root["data"] as String
        when (data) {
            "send" -> Handle_OnSendMail(agent, root)
            "del" -> Handle_OndelMail(agent, root)
            "update" -> Handle_OnUpdateMail(agent, root)
            "getAtt" -> Handle_OnGetAttachment(agent, root)
            "allOwn" -> Handle_allOwnMail(agent, root)
            "feedback" -> Handle_feedback(agent, root)
            else -> return
        }
    }

    fun Handle_OnSendMail(agent: Agent, root: JSONObject) {
        if (!root.has("sendId")) return
        var mail = Mail()
        //发送者的uid和昵称从agent中取
        var sendId = agent.user.uid
        var sendName = agent.user.nick

        var receiveId = root["receiveId"] as Int
        var receiveName = root["receiveName"] as String
        var message = ""

        if (root.has("detail")) {
            if ((root["detail"] as String).isNotEmpty()) {
                message = root["detail"] as String
            }
        } else {
            message = "玩家 ${sendName} 發送給妳的禮物"
        }

        mail.sendId = sendId
        mail.sendName = sendName
        mail.receiveId = receiveId
        mail.receiveName = receiveName
        mail.message = message

        var ex = LocalDate.now().plusDays(30)
        mail.expireDate = ex.toString()
        val user: User = UserService.getUserMsgByUID(receiveId)

        if (root["commodityId"] as Int == 0) {
            if (sendChatMail(mail) > 0) {
                //通知玩家查收礼物
                if (user != null) {
                    //在线发送提醒信息
                    if (Hall.isOnLine(agent.user.uid)) {
                        var noticeAgent = Agent()
                        noticeAgent.user = user
                        noticeAgent.CID = getCid(user.uid)
                        NoticeService.sendOftenMsg(noticeAgent, " email")
                    }
                }
                Send(agent, Msg(1, "success", "send", "hall_email"))
            } else {
                Send(agent, Msg(1, "fail", "send", "hall_email"))
            }
        } else {
            val emailId = sendChatMail(mail)

            if (emailId > 0) {
                var info = Attachmentinfo()
                var good = Goods()

                info.mailId = emailId

                val commodityId = root["commodityId"] as Int
                info.gId = commodityId
                info.count = root["count"] as Int
                good = goodsSelectOne(commodityId)      //查询商品

                //赠送扣除用户的金币或钻石，扣除金额失败则直接返回
                if (UserService.reduceCoinOrGem(agent.user.uid, good.currency ?: "coin", good.price.toLong()
                                ?: 0L) == 1) {
                    Send(agent, Msg(1, "fail", "send", "hall_email"))
                    return
                }

                EmailService.addAttachmentinfo(info)
                //判断礼物是否进行全服广播
                NoticeService.giftBroadcast(agent.user, mail.receiveName, good.commodityId)

                //通知玩家查收礼物
                if (user != null) {
                    //在线发送提醒信息
                    if (Hall.isOnLine(agent.user.uid)) {
                        var noticeAgent = Agent()
                        noticeAgent.user = user
                        noticeAgent.CID = getCid(user.uid)
                        NoticeService.sendOftenMsg(noticeAgent, " email")
                    }
                }
                //邮件礼物日志记录
                recordLog(sendId, sendName + " 發送給 " + receiveName, "mail", good.name)

                Send(agent, Msg(1, "success", "send", "hall_email"))
            } else {
                Send(agent, Msg(1, "fail", "send", "hall_email"))
            }


        }

    }

    fun sendChatMail(mail: Mail): Int {
        val emailId = EmailService.sendMail(mail)
        if (emailId > 0) {
            return emailId
        } else {
            //发送失败
            return 0
        }
    }


    fun Handle_OndelMail(agent: Agent, root: JSONObject) {
        if (!root.has("mid")) return
        if (EmailService.delMail(root["mid"] as Int) > 0) {
            Send(agent, Msg(1, "success", "del", "hall_email"))
        } else {
            Send(agent, Msg(1, "fail", "del", "hall_email"))
        }
    }

    fun Handle_OnUpdateMail(agent: Agent, root: JSONObject) {
        if (!root.has("mid")) return
        var mail = Mail()
        var mid = root["mid"] as Int
        mail.id = mid
        mail.expireDate = LocalDate.now().toString()
        mail.status = 1
        if (EmailService.updataMail(mail) > 0) {
            Send(agent, Msg(1, "success", "update", "hall_email"))
        } else {
            Send(agent, Msg(1, "fail", "update", "hall_email"))
        }
    }


    fun Handle_OnGetAttachment(agent: Agent, root: JSONObject) {
        if (!root.has("uid")) return
        if (!root.has("mid")) return
        var mid = root["mid"] as Int

        var att = EmailService.queryAttachmentinfo(mid)
        root.put("commodityId", att.gId)
        root.put("count", att.count)
        StoreService.Buy_Into_Backpacker(agent, root)



        if (EmailService.updateAttachmentinfo(mid) > 0) {
            Send(agent, Msg(1, "success", "get", "hall_email"))
            //转存充值记录
            var recharge = Recharge()
            if (att.gId == 110002) {
                val mail = checkOneMail(mid)
                recharge.customerId = mail.sendId.toString()
                recharge.money = att.count
                recharge.uid = mail.receiveId
                recharge.payMentType = "mail"
                recharge.time = LocalDateTime.now().toString()
                recharge.accruingAmounts = rechargeCount(mail.receiveId).sumBy { it.money }
                recharge.currency = "coin"
                insertRecharge(recharge)
            } else if (att.gId == 120002) {
                val mail = checkOneMail(mid)
                recharge.customerId = mail.sendId.toString()
                recharge.money = att.count
                recharge.uid = mail.receiveId
                recharge.payMentType = "mail"
                recharge.time = LocalDateTime.now().toString()
                recharge.accruingAmounts = rechargeCount(mail.receiveId).sumBy { it.money }
                recharge.currency = "gem"
                insertRecharge(recharge)
            }

            EmailService.delAttachmentinfo(att.id)
        } else {
            Send(agent, Msg(1, "fail", "get", "hall_email"))
        }
    }


    fun Handle_allOwnMail(agent: Agent, root: JSONObject) {
        if (!root.has("uid")) return

        var list = EmailService.getAllOwnMail(root["uid"] as Int)
        var array = JSONArray()
        list.forEach {
            var json = JSONObject()
            var att = EmailService.queryAttachmentinfo(it.id)
            var notGoods = Goods()
            notGoods.otherType = "0"
            var goods = EmailService.getGoodsInfo(att.gId) ?: notGoods
            json.put("id", it.id)
            json.put("sendId", it.sendId)
            json.put("sendName", it.sendName)
            json.put("receiveId", it.receiveId)
            json.put("receiveName", it.receiveName)
            json.put("sendDate", it.sendDate)
            json.put("receiveDate", it.receiveDate)
            json.put("expireDate", it.expireDate)
            json.put("message", it.message)
            json.put("status", it.status)
            json.put("attachmentinfo", it.attachmentinfo)
            json.put("gId", att.gId)
            json.put("count", att.count)
            json.put("goodType", goods.otherType)
            array.put(json)
        }
        if (list.size > 0) {
            Send(agent, Msg(1, array, "allOwn", "hall_email"))
        } else {
            Send(agent, Msg(1, "fail", "allOwn", "hall_email"))
        }

    }

    fun Handle_feedback(agent: Agent, root: JSONObject) {
        Send(agent, Msg(1, "success", "feedback", "hall_email"))

        //读取配置的电子邮件
        val sendMail = File("./src/Email.csv").readLines(Charset.forName("UTF-8"))[0]

        val content = root["content"] as String
        val Fmail = root["mail"] as String
        FeedbackMail.feedbackMail(sendMail, Fmail, agent.user.uid, agent.user.nick, content)
    }

    fun Handle_sysEmail(json: JSONObject, receiveUid: Int) {
        var mail = Mail()
        try {

            var user: User = UserService.getUserMsgByUID(receiveUid)

            var noticeAgent = Agent()
            if (user != null) {
                noticeAgent.CID = getCid(user.uid) ?: 1
                noticeAgent.UID = user.uid
                noticeAgent.user = user

                mail.receiveId = user.uid
                mail.receiveName = user.nick
                mail.message = json["msg"] as String

                var ex = LocalDate.now().plusDays(30)
                mail.expireDate = ex.toString()

                //发送邮件
                var emailId = EmailService.sendMail(mail)
                if (emailId != null) {
                    NoticeService.sendOftenMsg(noticeAgent, " email")
                    //邮件礼物日志记录
                    recordLog(mail.sendId, mail.sendName, "gift", "系統發送的獎勵郵件")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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