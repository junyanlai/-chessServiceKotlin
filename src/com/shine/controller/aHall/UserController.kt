package com.shine.controller.aHall


import com.shine.agent.Agent
import com.shine.amodel.AchievementTemp
import com.shine.amodel.Store
import com.shine.aservice.achievement.AchievementTempService
import com.shine.aservice.shop.StoreService
import com.shine.aservice.user.UserService
import com.shine.aservice.user.UserService.getUserMsgByUID
import com.shine.aservice.user.UserService.updateUserCoin
import com.shine.controller.aHall.EmailController.Handle_sysEmail
import com.shine.controller.aHall.Hall.OnClientClose
import com.shine.controller.aHall.Hall.mapUser
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

object UserController {

    //command=hall_room
    fun HandleAll(agent: Agent, root: JSONObject) {//command,data,type,id
        val data = root["data"] as String
        when (data) {
            "gold" -> Handle_OnGold(agent, root)
            "use" -> useGood(agent, root)           //使用的物品
            "info" -> queryInfo(agent, root)
            "OnLine" -> OnLine(agent, root)
            "updatAvatar" -> updatAvatar(agent, root)
            "jp" -> getJP(agent, root)
            "exchangeCoin" -> exchangeCoin(agent, root)     //兑退游戏币
            "exit" -> exit(agent, root)
            "title" -> getTitle(agent, root)
            "useTitle" -> userTitle(agent, root)   //获取称号
            "sex" -> sex(agent, root)           //修改性别
            "authentication" -> authentication(agent, root) //实名认证
            "transferAccounts" -> transferAccounts(agent, root)//转账
            else -> return
        }
    }

    fun userTitle(agent: Agent, root: JSONObject) {
        val atid = root["atid"] as Int
        if (atid == 0) return
        var allTitle = AchievementTempService.selectAllTitleByAtid(agent.user.vipLevel, agent.user.level) //充值等级称号
        allTitle.forEach {
            if (it.atid == atid) {
                var res = UserService.updateUserTittle(atid.toString(), agent.user.uid)
                Send(agent, Msg(res, mutableListOf(AchievementTempService.selectAchievementTempOne(AchievementTemp(atid = atid))), "useTitle", "hall_user"))
                return
            }
        }
    }

    fun getTitle(agent: Agent, root: JSONObject) {
        //查询用户称号id 获取称号数据
        var user = UserService.getUserMsgByUID(agent.user.uid)
        var allTitle = AchievementTempService.selectAllTitleByAtid(user.vipLevel, user.level) //充值等级称号
        var tittle = AchievementTemp() //当前称号
        if (user.tittle == "") {
            tittle = allTitle[0]
        } else {
            allTitle.forEach {
                if (it.atid == user.tittle.toInt()) {
                    tittle = it
                }
            }
        }
        Send(agent, Msg("1", allTitle, "allTittle", "hall_user"))
        Send(agent, Msg("1", mutableListOf(tittle), "thisTittle", "hall_user"))
    }

    fun exchangeCoin(agent: Agent, root: JSONObject) {
        val boo = root["type"] as Boolean
        val gameCurrency = root["money"].toString().toLong()
        val coin = gameCurrency
        val user = getUserMsgByUID(agent.user.uid)
        if (user.coin < coin) {
            Send(agent, Msg(gameCurrency, "insufficient", "insufficient", "hall_user"))
            return
        }
        if (boo) {
            if (updateUserCoin(user.coin - coin, agent.user.uid) > 0) {
                Send(agent, Msg(gameCurrency, "success", "exchange", "hall_user"))
            } else {
                Send(agent, Msg(gameCurrency, "fail", "exchange", "hall_user"))
            }
        } else {
            if (updateUserCoin(user.coin + coin, agent.user.uid) > 0) {
                Send(agent, Msg(gameCurrency, "success", "coinReturn", "hall_user"))
            } else {
                Send(agent, Msg(gameCurrency, "fail", "coinReturn", "hall_user"))
            }
        }
    }


    fun propertySafety(uid: Int, count: Long, type: Int): Int {
        val user = getUserMsgByUID(uid)

        when (type) {
            1 -> {                                  //金币
                if (user.coin < count) {
                    return 1
                }
                return 2
            }

            2 -> {                                  //钻石
                if (user.gem < count) {
                    return 1
                }
                return 2
            }
        }
        return 0
    }

    fun getJP(agent: Agent, root: JSONObject) {
        val user = UserService.getUserMsgByUID(1000)
        Send(agent, Msg(1, user.coin, "jp", "hall_user"))
    }

    fun updatAvatar(agent: Agent, root: JSONObject) {
        if (!root.has("uid")) return
        val uid = root["uid"] as Int
        val avatar = root["avatar"] as String
        if (UserService.updatAvatar(uid, avatar) > 0) {
            Send(agent, Msg(avatar.toInt(), "success", "updatAvatar", "hall_user"))
        } else {
            Send(agent, Msg(0, "fail", "updatAvatar", "hall_user"))
        }

    }

    fun OnLine(agent: Agent, root: JSONObject) {

        Send(agent, Msg(1, mapUser.size + Random().nextInt(1000) + 5000, "OnLine", "hall_user"))
    }

    fun sex(agent: Agent, root: JSONObject) {
        if (!root.has("sex")) return
        if (UserService.updatSex(agent.user.uid, root["sex"] as Int) > 0) {
            //更新缓存数据
            agent.user.sex = root["sex"] as Int
            Send(agent, Msg(1, "success", "sex", "hall_user"))
        } else {
            Send(agent, Msg(1, "fail", "sex", "hall_user"))
        }

    }

    fun authentication(agent: Agent, root: JSONObject) {
        if (!agent.logined) {
            Send(agent, Msg(1, "not", "authentication", "hall_user"))
        } else {
            val user = UserService.getUserMsgByUID(agent.user.uid)
            if (user.name.length > 0) {
                Send(agent, Msg(2, "repeat", "authentication", "hall_user"))
            } else {
                if (UserService.updateUserName(root["name"] as String, agent.user.uid) == 1) {
                    Send(agent, Msg(3, "success", "authentication", "hall_user"))
                } else {
                    Send(agent, Msg(4, "fail", "authentication", "hall_user"))
                }
            }
        }
    }

    fun transferAccounts(agent: Agent, root: JSONObject) {
        if (!root.has("rid")) return

        val rid = root["rid"] as Int
        val coin = root["coin"].toString().toLong()
        //余额校验
        when (propertySafety(agent.user.uid, coin, 1)) {
            1 -> {
                Send(agent, Msg(1, "insufficient", "transferAccounts", "hall_user"))
            }
            0 -> {
                Send(agent, Msg(0, "inside", "transferAccounts", "hall_user"))
            }
            2 -> {
                //减去用户转账金币
                val sUser = getUserMsgByUID(agent.user.uid)
                updateUserCoin(sUser.coin - coin, sUser.uid)

                //增加被转账的用户的金币
                val rUser = getUserMsgByUID(rid)
                updateUserCoin(rUser.coin + coin, rUser.uid)

                Send(agent, Msg(2, "success", "transferAccounts", "hall_user"))
                //发送系统通知邮件
                val json = JSONObject()
                json.put("msg", "您的好友:${sUser.nick}給您轉了${coin}金幣，已經添加到您的賬戶")
                Handle_sysEmail(json, rid)
            }
        }

    }

    /**
     *
     * 获得金币和钻石
     */
    fun Handle_OnGold(agent: Agent, root: JSONObject) {
        if (!root.has("uid")) return
        var user = UserService.getUserMsgByUID(root["uid"].toString().toInt())
        var reJson = JSONObject()
        reJson.put("gold", user.coin)
        reJson.put("gem", user.gem)
        if (user != null) {
            Send(agent, Msg(1, reJson, "success", "hall_user"))
        } else {
            Send(agent, Msg(1, "no the people", "fail", "hall_user"))
        }
    }

    /**
     * >>>
     */
    fun useGood(agent: Agent, root: JSONObject) {
        if (!root.has("uid")) return
        var store = Store()
        store.uid = root["uid"] as Int
        store.goodsState = 1
        val list = StoreService.storeGetByState(store)
        var array = JSONArray()
        list.forEach {
            var json = JSONObject()
            json.put("commodityId", it.commodityId)
            json.put("goodsType", it.goodsType)
        }
        if (list.size > 0) {
            Send(agent, Msg(1, array, "use", "hall_user"))
        } else {
            Send(agent, Msg(1, "fail", "use", "hall_user"))
        }
    }

    fun queryInfo(agent: Agent, root: JSONObject) {
        if (!root.has("uid")) return
        val user = UserService.getUserMsgByUID(root["uid"] as Int)
        val json = JSONObject()

        var atid = ""
        val t = AchievementTempService.selectAllTitleByAtid(user.vipLevel, user.level)

        if (t.size > 0) {
            atid = t[0].atid.toString()
        }

        user.let {
            json.put("uid", user.uid)
            json.put("avatar", user.avatar)
            json.put("sex", user.sex)
            json.put("vipLevel", user.vipLevel)
            json.put("nick", user.nick)
            json.put("atid", atid)
            Send(agent, Msg(1, json, "use", "hall_user"))
        }
        Send(agent, Msg(1, json, "info", "hall_user"))
    }

    fun exit(agent: Agent, root: JSONObject) {
        OnClientClose(agent)
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

