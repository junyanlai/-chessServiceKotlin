package com.shine.controller.aHall

import ManagerCb
import ManagerMl
import com.shine.agent.Agent
import com.shine.agent.SSF
import com.shine.agent.WebSocketServer
import com.shine.amodel.*
import com.shine.aservice.achievement.AchievementTempService
import com.shine.aservice.achievement.AchievementTrigger
import com.shine.aservice.achievement.AchievementUserService
import com.shine.aservice.army.ArmyAdminService
import com.shine.aservice.army.ArmyRankingService
import com.shine.aservice.eamil.EmailService
import com.shine.aservice.user.LivenessService
import com.shine.aservice.user.UserService
import com.shine.aservice.util.DayGet.Get_Relay_DayNum
import com.shine.aservice.util.DayGet.Get_This_DayNum
import com.shine.aservice.util.DayGet.formatDay
import com.shine.aservice.util.MailService
import com.shine.controller.gamble.saima.ManagerSm
import com.shine.controller.gamble.toubao.ManagerTb
import com.shine.controller.poker.Landlords.ManagerDdz
import com.shine.controller.poker.cdd.ManagerCdd
import com.shine.controller.poker.dz.ManagerDz
import com.shine.controller.poker.maj.ManagerMaj
import com.shine.controller.poker.nn.ManagerNN
import com.shine.controller.poker.ssz.ManagerSsz
import com.shine.controller.poker.xcsl.ManagerLh
import com.shine.controller.poker.xcsl.ManagerSl
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.round

object Hall {

    val mapRoom: MutableMap<Int, Room> = HashMap()          //rid,room
    val mapUser: MutableMap<Int, User> = HashMap()          //uid,user
    val mapUserRoom: MutableMap<Int, Room> = HashMap()      //uid,rid
    val mapUserLeaveRom: MutableMap<Int, Room> = HashMap()   //离开的人的 id 和 房间
    val mapSession: MutableMap<Int, Int> = HashMap()        //uid and cid
    var ArmySequence = LinkedList<Int>()                    //军团战游戏开启序列
    var gameStatus = LinkedList<ArmyStatus>()               //军团战状态使用标志
    //    val userAgent: MutableMap<Int, Agent> = HashMap()          //uid,agent
    val gMapRoom: MutableMap<Int, Room> = HashMap()         //rid,room  用拉霸游戏，防止ID重复
    val gMapUserRoom: MutableMap<Int, Room> = HashMap()     //uid,rid   用拉霸游戏，防止ID重复

    var mapLoginCount: MutableMap<Int, List<Int>> = HashMap()   // key   当天日期  /   value   登陆 用户id 集合
    var loginId = mutableListOf<Int>()
    val listCQDY = File("./src/CQDY.csv").readLines(Charset.forName("UTF-8"))
    val listHQDY = File("./src/HQDY.csv").readLines(Charset.forName("UTF-8"))
    val listRobotName = File("./src/nickname.csv").readLines(Charset.forName("UTF-8"))
    val VERSION = File("./src/VERSION.csv").readLines(Charset.forName("UTF-8"))
    val listHandWare = listOf("android", "ios")

    val switchHallin = false   //log print hall in
    val serviceScheduled = Executors.newSingleThreadScheduledExecutor()
    val JP = 5      // JP中奖幸运数字
    fun isOnLine(uid: Int): Boolean = mapUser.containsKey(uid)
    fun getCid(uid: Int): Int = mapSession[uid] ?: 1


    @Synchronized
    fun HandleHallIn(agent: Agent, root: JSONObject): Boolean {
        println("输入："+root)
        val command = root["command"] as String
        when (command) {
            "" -> return false
            "hall_sign" -> {
                HandleHallSign(agent, root)
                //println("sign : " + agent.UID.toString() + "sign : " + agent.user.uid)
                if (agent.user.uid != 0 || agent.user.uid != 1) judgeAllInit(agent)
            }
            "hall_login" -> {
                HandleHallLogin(agent, root)
                // println("login : " + agent.UID.toString() + "login : " + agent.user.uid)
                if (agent.user.uid != 0 || agent.user.uid != 1) judgeAllInit(agent)
            }
            "hall_resign" -> HandleHallReSign(agent, root)
            "hall_version" -> HandleHallVersion(agent, root)

            else -> HandleHall(agent, root)
        }
        return false
    }

    //判断各种需要初始化的信息是否初始化
    fun judgeAllInit(agent: Agent) {
        getLoginCount(agent.UID)
        if (AwardController.isFirstDayOfMonth(Date())) {
            AwardController.clearAllSinMsg(agent)
        }
        clear_everyDay_task(agent)
        initAchievement(agent)
    }
    //每天清空军团商店信息
    fun  everyDayClearArmyShop(agent:Agent){
        println("初始化数据")
        //删除Map里的军团商店信息
        SSF.getJedisMethod(agent).set(agent.UID.toString(),"new")
        SSF.getJedisMethod(agent).set(String.format("%s_%s",agent.UID.toString(),"num"),"0")
    }
    //统计登陆活跃度
    fun getLoginCount(uid: Int) {
        // println(mapLoginCount.size.toString() + "<<>><<>>" + mapLoginCount.toString())
        when {
            mapLoginCount.isEmpty() -> {
                loginId.clear()
                if (uid != 0) {
                    loginId.add(uid)
                    mapLoginCount.put(Get_This_DayNum(), loginId)
                }
            }
            !mapLoginCount.containsKey(Get_This_DayNum()) -> {
                if (mapLoginCount.containsKey(Get_Relay_DayNum())) {
                    for ((k, v) in mapLoginCount) {
                        LivenessService.insertLiveness(Liveness(count = v.size, date = formatDay()))
                        break
                    }
                } else {
                    for ((k, v) in mapLoginCount) {
                        LivenessService.insertLiveness(Liveness(count = v.size, date = formatDay(k)))
                        break
                    }
                }
                loginId.clear()
                mapLoginCount.clear()
                if (uid != 0) {
                    loginId.add(uid)
                    mapLoginCount.put(Get_This_DayNum(), loginId)
                }
            }
            mapLoginCount.containsKey(Get_This_DayNum()) && !loginId.contains(uid) -> {
                loginId.add(uid)
                mapLoginCount.put(Get_This_DayNum(), loginId)
            }
        }
        //println(loginId.size.toString() + " <<>>> " + loginId.toString())
    }

    //初始化用户成就信息表
    fun initAchievement(agent: Agent) {
        var user = UserService.getUserMsgByUID(agent.UID)   //获取用户信息从数据库里
        if (user != null && user.medal1 != 1) {
            // println("每日登陆:初始化" + agent.user.medal1)
            var time = Get_This_DayNum()
            insertAchivementUser(agent, "achievement", time)
            UserService.updateUserMsgByUID(User(uid = agent.UID, medal1 = 1))
        }
    }

    //每次登陆的时候清除用户的每日成就
    fun clear_everyDay_task(agent: Agent) {
        if (agent.UID == 1 || agent.UID == 0) return
        var msg = AchievementUserService.selectAuserMsg(AchievementUser(uid = agent.UID, atype = "day"))
        var time = Get_This_DayNum()
        if (msg.size > 0 && msg[0].time != time) {
            // println("每日登陆，清空：日期" + time)
            AchievementUserService.deleteAchievementUser(AchievementUser(uid = agent.UID, atype = "day"))
            AchievementUserService.deleteAchievementUser(AchievementUser(uid = agent.UID, atype = "oneSign"))
            AchievementUserService.deleteAchievementUser(AchievementUser(uid = agent.UID, atype = "online"))
            insertAchivementUser(agent, "day", time)
            insertAchivementUser(agent, "online", time)
            everyDayClearArmyShop(agent)    //清空每日军团商店
            println("每日清空数据")
            return
        }
        if (msg.size == 0 && agent.UID != 0) {
            // println("每日登陆:发现没有录入每日信息注入每日信息")
            insertAchivementUser(agent, "day", time)
            insertAchivementUser(agent, "online", time)
            // println("注入结束")
        }
    }

    fun insertAchivementUser(agent: Agent, type: String, time: Int) {
        var msg = AchievementTempService.selectAllTempMsg(AchievementTemp(type = type))
        if (msg == null) return
        msg.forEach {
            var aUser = AchievementUser(uid = agent.UID, atid = it.atid, aname = it.name, teps = 0, atype = it.type
                    , triggerType = it.triggerType, isCreated = 0, isfulfill = 0, time = time, nextTeps = it.teps)
            if ("online".equals(type)) {
                aUser.isfulfill = 1
                aUser.teps = it.teps
            }
            AchievementUserService.insertAchievementUser(aUser)
        }
    }

    // Verify version number
    fun HandleHallVersion(agent: Agent, root: JSONObject) {
        val version = root["number"] as String

        val clientVersion = version.split(".")
        if (clientVersion.size < 3) {
            Send(agent, Msg(VERSION[0], 0, "request", "hall_version"))
            return
        }
        val serverVersion = VERSION[0].split(".")
        var boo = true

        for (i in 0 until 3) {
            if (clientVersion[i].toInt() != serverVersion[i].toInt()) {
                boo = false
            }
        }
        if (boo) {
            Send(agent, Msg(VERSION[0], 0, true, "hall_version"))
        } else {
            Send(agent, Msg(VERSION[0], 0, false, "hall_version"))
        }
    }

    fun HandleHallSign(agent: Agent, root: JSONObject) {
        if (!root.has("data")) return
        //if logined
        if (agent.logined) {
            agent.Send(agent.CID, Msg(0, 0, "relogin", "hall_relogin"))
            agent.Disconnect()
            return
        }

        val type = root["data"] as String
        if (type.length == 7) {
            OnSignE(agent, root)
            return
        }
        when (type) {
            "" -> return
            "email" -> OnSignE(agent, root)
            "google" -> return //OnLoginG(cid,root)
            "yahoo" -> return //OnLoginY(cid,root)
            "facebook" -> return //OnLoginF(cid,root)
            else -> return
        }
    }

    fun HandleHallReSign(agent: Agent, root: JSONObject) {
        val data = root["data"] as String
        val email = root["account"] as String
        val name = root["hardware"] as String

        //if logined
        if (agent.logined) {
            agent.Send(agent.CID, Msg(0, 0, "relogin", "hall_relogin"))
            agent.Disconnect()
            return
        }

        //into code compare round
        if (data.length == 7) {
            OnResignE(agent, root)
            return
        }

        val emailCount = UserService.getEmailCount(email)
        if (emailCount == 0) {
            agent.Send(agent.CID, Msg(0, 0, "no find this email account", "hall_resign"))
            return
        }

        val dbaname = UserService.userNameGetByE(email)
        //println("#######################$dbaname")
        if (dbaname.length == 0) {
            agent.Send(agent.CID, Msg(0, 7, "you have not fill realname", "hall_resign"))
            return
        }

        if (name != dbaname) {
            agent.Send(agent.CID, Msg(0, 8, "wrong name", "hall_resign"))
            return
        }

        val mailCodeSend = MailService.sendCodeEmail(email)
        val codeSecond = Date().time.toInt() + 150000

        //in cid
        agent.signcode = mailCodeSend
        agent.signtimeout = codeSecond
        agent.signmail = email

        agent.Send(agent.CID, Msg(2, 0, "get mail code success", "hall_resign"))
        return
    }

    fun HandleHallLogin(agent: Agent, root: JSONObject) {

        val type = root["data"] as String
        val device = root["device"] as String
        val hardware = root["hardware"] as String

        //if logined
        if (agent.logined) {
            agent.Send(agent.CID, Msg(0, 0, "relogin", "hall_relogin"))
            agent.Disconnect()
            return
        }

        if (device == "" || !listHandWare.contains(hardware)) {
            Send(agent, Msg(0, 4, "illegal request", "hall_sign_on"))
            return
        }

        when (type) {
            "" -> return
            "email" -> OnLoginE(agent, root)
            "fast" -> OnLoginF(agent, root)
            "google" -> return //OnLoginG(cid,root)
            "yahoo" -> return //OnLoginY(cid,root)
            "facebook" -> return //OnLoginF(cid,root)
            else -> return
        }
    }

    //email sign
    fun OnSignE(agent: Agent, root: JSONObject) {

        val type = root["data"] as String
        val email = root["account"] as String
        val nick = root["nick"] as String

        if (type == "email") {

            val formcorrect = Regex("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$").matches(email)

            //mail form check
            if (!formcorrect) {//email type correct

                val msg = Msg(0, 0, "wrong mail form", "hall_sign")
                agent.Send(agent.CID, msg)
                return
            }

            //check same email
            val sameMailCount = UserService.getEmailCount(email)
            if (sameMailCount == 1) {

                val msg = Msg(0, 2, "you have already signed", "hall_sign")
                agent.Send(agent.CID, msg)
                return
            }

            //check same nick
            val sameNickCount = UserService.getNickCount(email)
            if (sameNickCount == 1) {

                val msg = Msg(0, 2, "your nick has been used", "hall_sign")
                agent.Send(agent.CID, msg)
                return
            }

            //send mailCode
            val mailCodeSend = MailService.sendCodeEmail(email)
            val codeSecond = Date().time.toInt() + 150000

            //in cid
            agent.signnick = nick
            agent.signcode = mailCodeSend
            agent.signtimeout = codeSecond
            agent.signmail = email

            val msg = Msg(1, 1, "had send signcode to your emailAddress", "hall_sign")
            agent.Send(agent.CID, msg)
            return
        }
        if (type.length == 7) {

            //time check
            val time = Date().time.toInt()

            //println("curTime="+time+" agentTime:"+cid.signtimeout+" c-a="+(time-cid.signtimeout)+" c>a"+(time>cid.signtimeout))

            if (time > agent.signtimeout) {

                agentSignClear(agent)
                val msg = Msg(0, 3, " time out", "hall_sign")
                agent.Send(agent.CID, msg)
                return
            }

            //email check
            if (email != agent.signmail || nick != agent.signnick) {

                agentSignClear(agent)
                val msg = Msg(0, 4, "illegal request", "hall_sign")
                agent.Send(agent.CID, msg)
                return
            }

            //code check
            if (type != ("" + agent.signcode)) {

                agentSignClear(agent)
                agent.Send(agent.CID, Msg(0, 5, " wrong signcode", "hall_sign"))
                return
            }

            val hardware = root["hardware"] as String
            val hash = root["hash"] as String

            //hash check
            if (hash.length != 32) {

                agentSignClear(agent)
                agent.Send(agent.CID, Msg(0, 4, "illegal request", "hall_sign"))
                return
            }

            //hardware wrong
            if (!listHandWare.contains(hardware)) {

                agentSignClear(agent)
                agent.Send(agent.CID, Msg(0, 4, "illegal request", "hall_sign"))
                return
            }

            //add to dba
            val user = User(avatar = "" + Random().nextInt(9), nick = nick, hash = hash, email = email, device = hardware)
            val addresult = UserService.userAdd(user)

            //add dba success
            if (addresult == 0) {

                agentSignClear(agent)
                agent.Send(agent.CID, Msg(0, 6, " sign data msg wrong", "hall_sign"))
                return
            }

            //Sign success and do login
            agentSignClear(agent)
            root.put("data", "email")
            root.put("command", "hall_login")
            OnLoginE(agent, root)

        }
    }

    //onResign E
    fun OnResignE(agent: Agent, root: JSONObject) {

        val data = root["data"] as String
        val email = root["account"] as String

        //time check
        val time = Date().time.toInt()
        if (time > agent.signtimeout) {
            agentSignClear(agent)
            agent.Send(agent.CID, Msg(0, 3, "time out", "hall_resign"))
            return
        }

        //account check
        if (email != agent.signmail) {
            agentSignClear(agent)
            agent.Send(agent.CID, Msg(0, 4, "illegal request", "hall_resign"))
            return
        }

        //code check
        if (data != ("" + agent.signcode)) {
            agentSignClear(agent)
            agent.Send(agent.CID, Msg(0, 5, " wrong signcode", "hall_resign"))
            return
        }

        //hahs check
        val hash = root["hash"] as String
        //hash check
        if (hash.length != 32) {
            agentSignClear(agent)
            agent.Send(agent.CID, Msg(0, 4, "illegal request", "hall_resign"))
            return
        }

        //update do
        val updresult = UserService.userHashUpd(User(email = email, hash = hash))
        if (updresult == 0) {
            agentSignClear(agent)
            agent.Send(agent.CID, Msg(0, 9, "something wrong", "hall_resign"))
            return
        }

        agentSignClear(agent)

        agent.Send(agent.CID, Msg(1, 1, "sign success", "hall_resign"))

        return
        /*
        //resign success and do login
        root.put("data","email")
        root.put("command","hall_login")
        OnLoginE(cid,root)*/
    }

    //email login
    fun OnLoginE(agent: Agent, root: JSONObject) {

        val login = mapOf("email" to root["account"], "hash" to root["hash"])
        val result = UserService.userCheckE(login)

        //login failed
        if (result == 0)
            Send(agent, Msg(0, 0, "login failed and something wrong", "hall_sign_on"))
        else {

            agent.logined = true
            var user = UserService.userGetByEmail(root["account"] as String)
            val gameUser = OnloginBind(agent, user)//login bind and return only one User
            Send(agent, Msg(gameUser?.uid, JSONObject(gameUser), "login success", "hall_sign_on"))
            //println("#########Session size=${mapSession.size} , session=${mapSession}")
        }
    }

    //fast login
    fun OnLoginF(agent: Agent, root: JSONObject) {

        //nick,device,hardware
        val device = root["device"] as String
        val hardware = root["device"] as String

        val account = root["account"] as String
        val hash = root["hash"] as String
        if (account.length != 32 || hash.length != 32)
            Send(agent, Msg(0, 4, "illegal request", "hall_sign_on"))

        val deviceCount = UserService.getDeviceCount(device)

        if (deviceCount == 1) {

            agent.logined = true
            //get user
            val user = UserService.userGetByDevice(device)
            val gameUser = OnloginBind(agent, user)
            Send(agent, Msg(gameUser?.uid, JSONObject(gameUser), "login success", "hall_sign_on"))
        }
        if (deviceCount == 0) {

            agent.logined = true
            val user = addFastUser(device, hardware)
            val gameUser = OnloginBind(agent, user)
            Send(agent, Msg(gameUser?.uid, JSONObject(gameUser), "login success", "hall_sign_on"))
        }

        return
    }

    //addFastUser
    fun addFastUser(device: String, hardware: String): User {

        var type = ""
        if (hardware == "android") type = "a"
        else type = "i"

        val nick = listRobotName[Random().nextInt(34270)] + listRobotName[Random().nextInt(34270)]

        var user = User(
                device = device,
                hardware = type,
                avatar = "" + Random().nextInt(9),
                nick = nick,
                email = nick
        )

        val addfastResult = UserService.userAddF(user)
        user = UserService.userGetByDevice(device) //重新获取
        if (addfastResult == 0) addFastUser(device, hardware)

        return user
    }

    //cid sign clear
    fun agentSignClear(agent: Agent) {
        agent.signnick = ""
        agent.signmail = ""
        agent.signcode = 0
        agent.signtimeout = 0
    }

    //bind the login user and return the only gameUser
    fun OnloginBind(agent: Agent, user: User): User? {

        val uid = user.uid
        val cid = agent.CID

        //map check
        if (mapSession[uid] == null) {
            if (switchHallin) println("#########################登陆绑定—1：第一次登陆")

            user.cid = cid
            mapSession[uid] = cid

            //mapUser[uid]=user -> mapUser is null point user,else make the detail same and don't change point
            if (mapUser[uid] == null) mapUser[uid] = user
            else UClone(mapUser[uid], user)

            mapUser[uid]?.let { agent.user = it }
            agent.UID = uid
            return user
        } else if (mapSession[uid] != cid) {//ReLogin Operate
            if (switchHallin) println("#########################登陆绑定-2：登陆挤掉")

            //kill old
            val oldCid = mapSession[uid]
            val oldAgent = Agent.agents[oldCid]
            oldAgent?.relogined = true
            if (oldCid != null) {
                agent.Send(oldCid, Msg(0, 0, "relogin", "hall_relogin"))
                oldAgent?.let { it.Disconnect() }//cotained onClientClose
            }

            mapSession[uid] = cid
            mapUser[uid]?.let {
                it.cid = cid
                agent.user = it
            }
            agent.UID = uid

            return mapUser[uid]
        }

        if (switchHallin) println("#########################登陆绑定-3：登陆消息多发")
        return mapUser[uid]
    }

    //saps close [Handle the relogin]
    fun OnClientClose(agent: Agent) {
        //if(switchHallin)    println("#########################Hall.onClientClose：大厅安全关闭")

        val uid = agent.UID
        val table = mapUserRoom[uid]

        if (table != null) table.OnClientClose(agent.user)
        if (!agent.relogined) {       //一般都会移除，只有oldAgent 才不删除session [relogin情况]
            //Just when agent is not the relogined oldagent Can be remove session
            mapUser[uid]?.let { it.cid = 0 }
            mapSession.remove(uid)
        }

        //拉霸
        val gTable = gMapUserRoom[uid]
        if (gTable != null) HangUpController.deleteRoom(agent, JSONObject().put("rid", gTable.rid))
    }

    //When signed can receive msg
    fun HandleHall(agent: Agent, root: JSONObject) {
//        userAgent[agent.user.uid]=agent
        if (!agent.logined || !mapSession.containsValue(agent.CID)) {
            //重新登陆
            Send(agent, Msg(1, "restart", "restart", "hall_sign"))
            return
        }
        if (!root.has("data")) return
        val command = root["command"] as String
        when (command) {
            "" -> return
            "hall_user" -> UserController.HandleAll(agent, root)                //个人用户信息
            "hall_army" -> ArmyController.HandleAll(agent, root)                //军团信息管理
            "hall_armyWar" -> ArmyWarHall.HandleAll(agent, root)                //军团战
            "hall_friend" -> FriendController.HandleAll(agent, root)            //好友
            "hall_notice" -> NoticeController.HandleAll(agent, root)            //公告模块
            "hall_hangup" -> HangUpController.HandleAll(agent, root)            //离线挂机模块
            "hall_email" -> EmailController.HandleAll(agent, root)              //赠送礼物 邮件
            "hall_attendance" -> AchievementController.HandleAll(agent, root)   //每日签到
            "hall_recharge" -> AchievementTrigger.choseMethod(agent, root)       //充值 确认
            "hall_award" -> AwardController.HandleAll(agent, root)              //奖励系统  // 获取 奖励信息
            "hall_store" -> StoreController.HandleAll(agent, root)              //背包
            "hall_goods" -> GoodsShopController.HandleAll(agent, root)          //商城
            "hall_controller" -> ManagerController.HandleAll(agent, root)       //后台控制
            "hall_room" -> RoomController.HandleAll(agent, root)                //建房
            "hall_ranking" -> RankingController.HandleAll(agent, root)          //排行榜

            "msg_maj" -> ManagerMaj.HandleAll(agent, root)
            "msg_cdd" -> ManagerCdd.HandleAll(agent, root)
            "msg_ddz" -> ManagerDdz.HandleAll(agent, root)                      //斗地主
            "msg_ml" -> ManagerMl.HandleAll(agent, root)                        //玛莉
            "msg_cb" -> ManagerCb.HandleAll(agent, root)                        //超八
            "msg_nn" -> ManagerNN.HandleAll(agent, root)
            "msg_ssz" -> ManagerSsz.HandleAll(agent, root)
            "msg_dz" -> ManagerDz.HandleAll(agent, root)
            "msg_tb" -> ManagerTb.HandleAll(agent, root)
            "msg_sm" -> ManagerSm.HandleAll(agent, root)
            "msg_xcsl" -> ManagerSl.HandleAll(agent, root)
            "msg_lhj" -> ManagerLh.HandleAll(agent, root)
            "msg_nn" -> ManagerNN.HandleAll(agent, root)


            else -> return
        }
    }


    //给在线的全部玩家推送通知-谨慎调用
    fun sendingNotice(msg: String) {
        mapUser.values.forEach {
            var agent = Agent()
            agent.CID = it.cid
            Send(agent, msg)
        }
    }

    fun stratGameType(): String {
        return ArmyAdminService.querywarAction(ArmySequence.first).type
    }

    fun startAction(s: Long) {
        println("startAction")
        sendingNotice(Msg(1, "startAction", gameStatus.first.type, "hall_armyWar"))
        serviceScheduled.schedule(Runnable {
            gameStatus.addLast(gameStatus.poll())
            sendingNotice(Msg(1, "stopAction", stratGameType(), "hall_armyWar"))
            closeAction(15)
        }, s, TimeUnit.MINUTES)
    }

    fun closeAction(s: Long) {
        serviceScheduled.schedule(Runnable {
            gameStatus.addLast(gameStatus.poll())
            sendingNotice(Msg(1, "closeAction", stratGameType(), "hall_armyWar"))
            sendWinMail(1)
        }, s, TimeUnit.MINUTES)
    }

    fun sendWinMail(s: Long) {
        serviceScheduled.schedule(Runnable {
            sendingNotice(Msg(1, "getranking", stratGameType(), "hall_armyWar"))
            sendReward()
        }, s, TimeUnit.MINUTES)

    }

    fun sendReward() {
        val list = ArmyRankingService.queryAll()
        for ((i, v) in list.withIndex()) {
            if (i == 3) break
            val army = ArmyAdminService.getArmyInfo(v.id)
            ArmyAdminService.updateAarmyCoin(v.id, 100000) //award coin Of 10w
            ArmyAdminService.updateAarmyDiamonds(v.id, 5000) //award diamond Of 500
            ArmyAdminService.updategrain(1000, v.id) //award dategrain Of 1000
            var mail = Mail()
            mail.sendId = 1
            mail.sendName = " 系統郵件"
            mail.receiveId = army.adminid
            mail.receiveName = army.name
            mail.message = "您的軍團非常給力，恭喜妳的軍團進入三強，排名為：${i + 1},獲得獎勵 10w 金幣，5000鉆石，1000軍糧，感謝妳的軍團和軍團成員的付出"
            mail.mailType = 1
            EmailService.sendMail(mail)
        }
        var r = ArmyAdminService.deleteArmyRankingAll() //clear army_ranking by data
        ArmyWarHall.count = 0
        ArmyWarHall.mapUser.clear()             //clear uid and user
        gameStatus.clear()                      //清空游戏状态
        addQueue()                              //substitution of element
        WebSocketServer.checkWarArmyTime()      //检查军团活动状态，进入下一个周期
    }

    fun addQueue() {
        val sequence = ArmySequence.poll()
        ArmySequence.addLast(sequence)
    }

    //从淘宝&赛马里面 抽水
    fun jPCoin(coin: Long): Long {
        if (coin == 0L) return 0L
        val c = round((coin * 0.1)).toLong()
        val uid = 1000
        val user = UserService.getUserMsgByUID(uid)

        //如果jp系统金币过大，则不增加
        if (user.coin >= 9999999999) return 0

        if (user != null) {
            val r = UserService.updateUserCoin((user.coin + c).toLong(), user.uid)
            if (r > 0) return c
            else return 0
        }
        return 0L
    }

    //JP
    fun jPRandom(index: Int): JSONArray {
        var coin: Long = 0
        val uid = 1000
        var JpArray = JSONArray()
        for (i in 0 until index) {
            var JpJson = JSONObject()
            val r = Random().nextInt(100)
            if (r == JP) {
                val user = UserService.getUserMsgByUID(uid)
                //如果jp系统里面的金额过小，触发的JP就失效
                if (user.coin < 500) break
                coin = round(user.coin * 0.3).toLong()
                //更新JP
                UserService.updateUserCoin(user.coin - coin, user.uid)
                JpJson.put("index", i)
                JpJson.put("coin", coin)
                JpArray.put(JpJson)
            }
        }
        return JpArray
    }


    //get SendMsg
    fun Msg(result: Any?, data: Any?, detail: Any?, command: Any?): String {
        val msg = JSONObject()
        msg.put("command", command)
        msg.put("result", result)
        msg.put("data", data)
        msg.put("detail", detail)
        return msg.toString()
    }

    fun Send(agent: Agent, msg: String) = agent.Send(agent.CID, msg)
    fun UClone(user: User?, userSql: User) {
        if (user == null) return
        user.uid = userSql.uid
        user.cid = userSql.cid

        user.avatar = userSql.avatar
        user.hash = userSql.hash
        user.nick = userSql.nick
        user.name = userSql.name
        user.line = userSql.line

        user.email = userSql.email
        user.phone = userSql.phone
        user.accountType = userSql.accountType
        user.accountGG = userSql.accountGG
        user.accountFB = userSql.accountFB
        user.accountYH = userSql.accountYH
        user.device = userSql.device
        user.hardware = userSql.hardware

        user.sex = userSql.sex
        user.level = userSql.level
        user.vipLevel = userSql.vipLevel
        user.exp = userSql.exp
        user.expFashion = userSql.expFashion
        user.medal0 = userSql.medal0
        user.medal1 = userSql.medal1
        user.medal2 = userSql.medal2

        user.coin = userSql.coin
        user.gem = userSql.gem
        user.bank = userSql.bank

        user.aid = userSql.aid
        user.rid = userSql.rid

        user.registerTime = userSql.registerTime
        user.loginTime = userSql.loginTime
        user.birthday = userSql.birthday

        user.signTimes = userSql.signTimes
        user.lastSignDate = userSql.lastSignDate
    }


    fun jsonArrayTomap(array: JSONArray): HashMap<Int, Long> {
        var map = HashMap<Int, Long>()
        for (i in 0..array.length()) {
            val json = array.get(i) as JSONObject
            map.put(json["index"] as Int, json["coin"] as Long)
        }
        return map
    }


    //get Hall setting
    fun getStatus() {
    }

}
