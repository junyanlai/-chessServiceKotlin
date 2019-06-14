package com.shine.controller.poker.dz

import com.mysql.cj.xdevapi.JsonArray
import com.shine.agent.Agent
import com.shine.amodel.ArmyRanking
import com.shine.amodel.Room
import com.shine.amodel.Texas
import com.shine.amodel.User
import com.shine.aservice.army.ArmyAdminService
import com.shine.aservice.army.ArmyRankingService
import com.shine.aservice.currency.BonusCalculationService
import com.shine.aservice.notice.NoticeService
import com.shine.aservice.user.UserService
import com.shine.controller.aHall.Hall
import com.shine.controller.aHall.RoomController
import com.shine.controller.poker.dz.LogicDz.CompareCard
import com.shine.controller.poker.ssz.tool.DzUtil.getTexasJson
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class TableDz constructor(override val rid: Int, override val creator: Int, override val pwd: String,
                          override val di: Int, override val roundMax: Int,
                          override val mTime: Int, override var armyBoo: Int) : Room {

    override val numMax = 9
    override val numMin = 2
    override val type = "dz"
    override val timeCreate = System.currentTimeMillis()

    override var numCur = 0
    override var timeWait = 0
    override var roundCur = 0

    override var isStart = false
    override val arrSeats = IntArray(numMax)            //-1- leaved, 0-safeNull, 1-sit, 2-ready, 3-
    override val arrPlayers = arrayOfNulls<User>(numMax)
    override val arrLeavers = IntArray(numMax)

    override val mapRoom = Hall.mapRoom
    override val mapUserRoom = Hall.mapUserRoom
    override val mapHalfRoom = RoomController.mapHalfRoom_dz


    override val switchLog = true
    override var switchAi = true
    override val agentRoom = RoomController.RoomAgent
    override val all = 255
    override val cmd = "msg_" + type
    var score = IntArray(4) { 0 }
    var stake = HashMap<Int, Long>()
    override val rand = Random()
    override var cardMount = IntArray(52)
    override val serviceScheduled = Executors.newSingleThreadScheduledExecutor()
    var cachedThreadPool = Executors.newCachedThreadPool()

    var cardlist = ArrayList<IntArray>()      //保存的是有序的牌组（位置）
    val cardCompare = hashMapOf<Int, IntArray>()

    var village: Int = -1                        //庄家位置

    var lock = java.lang.Object()
    var survivalPeople = ArrayList<User>()      // 存活的玩家
    var sortSurvival = ArrayList<User>()        //正序玩家
    var messageQueue = LinkedList<Texas>()      //玩家消息队列
    var allInList = ArrayList<Int>()            //allin玩家的座位号
    var renounceList = ArrayList<Int>()         //弃牌的玩家的号
    var surrender = ArrayList<Int>()            //投降玩家的座位号
    var choose = ArrayList<Int>()
    var aHand = HashMap<Int, JSONArray>()       //玩家的手牌存储
    var singOut = 0                             //退出标志

    //...
    override fun HasUserSeat(user: User) = arrPlayers.indexOf(user)

    override fun OnClientClose(user: User) {
        val seat = HasUserSeat(user)
        OnUserLeave(seat)
    }

    override fun OnUserLeave(seat: Int) {
        //println("德州离开测试>>>")
        if (isStart) {
            SendData(all, Msg(1, seat, 2, "leave", cmd))
            arrSeats[seat] = -1
            arrPlayers[seat]?.let {
                if (it.uid != 1) mapHalfRoom.put(it.uid, this)
                if (it.uid != 1) mapUserRoom.remove(it.uid)
            }
            numCur--
            for (i in 0 until numMax)//for roomleave easy
                if (arrPlayers[i]?.uid == 1) {
                    arrSeats[i] = -1
                }

            RoomLeave()
            return
        }

        arrPlayers[seat]?.rid = 0
        SendData(all, Msg(1, seat, 1, "leave", cmd))//send before truly remove(or it cannot receive msg)

        arrSeats[seat] = 0
        arrPlayers[seat]?.let {
            if (it.uid != 1) mapUserRoom.remove(it.uid)
            arrLeavers[seat] = it.uid
        }
        arrPlayers[seat] = null
        numCur--
        RoomLeave()
    }

    override fun OnUserReady(seat: Int) {

        arrSeats[seat] = 2
        SendData(all, Msg(1, seat, arrSeats, "ready", cmd))

        if (isStart) return      //just unstart can start

        var allReady = false

        if (arrSeats.count { it == 2 } >= 2) allReady = true

        if (allReady)
            RoundStart()
    }

    override fun OnUserSit(user: User): Int {
        if (arrPlayers.contains(user)) { //是否包含玩家，
            val seat = arrPlayers.indexOf(user)
            if (isStart) {
                arrSeats[seat] = 2;mapHalfRoom.remove(arrPlayers[seat]?.uid)
            } else arrSeats[seat] = 1
            user.rid = rid
            SendData(all, Msg(1, seat, saps(), "sit", cmd))
            if (user.uid != 1) mapUserRoom.put(user.uid, this)
            numCur++
            return 1
        }

        RoomSeatSync()//同步人数

        if (isStart) return 2
        if (numCur == numMax) return 3

        for ((i, u) in arrPlayers.withIndex()) {
            if (u == null) {
                arrSeats[i] = 1
                arrPlayers[i] = user
                user.rid = rid
                SendData(all, Msg(1, i, saps(), "sit", cmd))
                OnUserReady(i)
                if (user.uid != 1) mapUserRoom.put(user.uid, this)
                numCur++
                return 1
            }
        }
        return 4
    }

    //...
    override fun RoomLeave() {

        var noLiveOne = true
        for (status in arrSeats)
            if (status > 0)
                noLiveOne = false

        if (noLiveOne)
            RoomDelete()
    }

    override fun RoomDelete() {

        for (u in arrPlayers) {
            if (u == null) continue
            if (u.uid == 1) continue
            mapUserRoom.remove(u.uid)
            mapHalfRoom.remove(u.uid)
            u.rid = 0
        }
        for (uid in arrLeavers) {
            if (uid == 0) continue
            if (uid == 1) continue
            mapHalfRoom.remove(uid)
        }
        mapRoom.remove(rid)
        switchAi = false
        serviceScheduled.shutdown()
        serviceScheduled.shutdownNow()
        if (switchLog) println("#_________ Room is deleted")
    }

    override fun RoomSeatSync() {

        var numNull = 0
        for ((i, u) in arrPlayers.withIndex()) {
            if (u == null) {
                numNull++
            } else {
                if (arrSeats[i] == 0)
                    arrSeats[i] = 1
            }
        }
        numCur = numMax - numNull
    }

    //...
    override fun Log() {
        if (!switchLog) return
        println()
        println("#_________arrSeats={[${arrSeats[0]}],[${arrSeats[1]}],[${arrSeats[2]}],[${arrSeats[3]}]}")
        println("#_________arrPlayer0=${arrPlayers[0]}")
        println("#_________arrPlayer1=${arrPlayers[1]}")
        println("#_________arrPlayer2=${arrPlayers[2]}")
        println("#_________arrPlayer3=${arrPlayers[3]}")
        println("#_________size=:${mapRoom.size}")
        println("#_________content=:${mapRoom}")
        println("#_________size=:${mapUserRoom.size}")
        println("#_________size=:${mapHalfRoom.size}")
        println("#_________content=:${mapHalfRoom}")
        println()
    }

    override fun SendData(seat: Int, msg: String) {

        //4,5,6,7-> hide msg
        if (seat > (numMax - 1) && seat < all) {
            val si = seat - numMax
            if (arrSeats[si] < 1) return
            arrPlayers[si]?.let { agentRoom.Send(it.cid, msg) }
            val json = JSONObject(msg)
            json.put("detail", "hide")

            for (i in 0 until numMax) {
                if (i == si) continue
                arrPlayers[i]?.let { agentRoom.Send(it.cid, json.toString()) }
            }
            return
        }

        if (seat == all) {
            for ((i, status) in arrSeats.withIndex()) {
                if (status < 1) continue
                if (arrPlayers[i] == null) continue
                if (arrPlayers[i]!!.uid == 1) continue            //robot

                arrPlayers[i]?.let { agentRoom.Send(it.cid, msg) }
            }
            return
        }

        if (arrSeats[seat] < 1) return
        arrPlayers[seat]?.let { agentRoom.Send(it.cid, msg) }
    }

    override fun Msg(result: Any?, seat: Any?, detail: Any?, data: Any?, command: Any?): String {
        val msg = JSONObject()

        msg.put("result", result)
        msg.put("seat", seat)
        msg.put("detail", detail)
        msg.put("data", data)
        msg.put("command", command)
        return msg.toString()
    }


    override fun SeatNext(seat: Int) = if (seat == (numMax - 1)) 0 else (seat + 1)
    override fun SeatLast(seat: Int) = if (seat == 0) (numMax - 1) else (seat - 1)

    override fun RoundStart() {
        isStart = true      //房间是否开始
        roundCur++          //回合数
        GameFapai()     //发牌
    }

    override fun Status(seat: Int) {
        if (!switchAi) return
    }


    override fun GameFapai() {
        //初始化选牌值
        for (i in 0 until 52) {
            choose.add(i)
        }
        choose.shuffle()//洗牌

        //初始化生存玩家
        for (i in 0 until arrSeats.size) {
            if (arrSeats[i] == 2) {
                val user = arrPlayers[i]
                if (user != null) {
                    survivalPeople.add(user)
                }
            }
        }
        getVillage()    //更新庄家坐标
        SendData(all, Msg(1, village, "", "village", cmd))

        sortPlayer()    //玩家排序

        //初始化消息队列
        if (sortSurvival.size == 2) {
            //只有两个人的时候
            for (i in 0 until sortSurvival.size) {
                if (i == 0) {
                    stake.put(0, (di * 2).toLong())
                    messageQueue.add(Texas(0, di * 2, 2, 2, 2, di))
                } else {
                    stake.put(0, (di).toLong())
                    messageQueue.add(Texas(0, di, 2, 2, 2, di))
                }
            }
        } else {
            //发牌前，庄家左侧玩家压大小盲注
            for (i in 0 until sortSurvival.size) {
                if (i == 0) {
                    stake.put(0, (di / 2).toLong())
                    messageQueue.add(Texas(0, di / 2, 2, 2, 2, di))
                } else if (i == 1) {
                    stake.put(1, (di * 2).toLong())
                    messageQueue.add(Texas(0, di * 2, 2, 2, 2, di))
                } else {
                    messageQueue.add(Texas(0, di, 2, 2, 2, di))
                }
            }
        }

        // 发送小盲坐标和大盲坐标
        if (sortSurvival.size == 2) {
            SendData(all, Msg(1, HasUserSeat(sortSurvival[0]), 100, "max", cmd))
        } else {
            SendData(all, Msg(1, HasUserSeat(sortSurvival[0]), 100, "small", cmd))
            SendData(all, Msg(1, HasUserSeat(sortSurvival[1]), 200, "max", cmd))
        }

        cachedThreadPool.execute(Runnable {
            run {
                synchronized(lock) {
                    for (i in 0 until 4) {
                        println("第几轮>>>${i}")
                        if (i == 0) {
                            //只有两个人
                            if (sortSurvival.size == 2) {
                                for (j in 1 until messageQueue.size) {
                                    cardAhand()    //发送底牌

                                    val seat = HasUserSeat(sortSurvival[j])
                                    messageQueue[j].bets = messageQueue[j - 1].bets     //更新上一家的筹码

                                    //结束游戏
                                    if (signOut()) break

                                    SendData(seat, Msg(1, seat, getTexasJson(messageQueue[j]), "bets", cmd))
                                    lock.wait()

                                }
                            } else {
                                for (j in 2 until messageQueue.size) {
                                    cardAhand()    //发送底牌

                                    val seat = HasUserSeat(sortSurvival[j])
                                    messageQueue[j].bets = messageQueue[j - 1].bets     //更新上一家的筹码

                                    //结束游戏
                                    if (signOut()) break

                                    SendData(seat, Msg(1, seat, getTexasJson(messageQueue[j]), "bets", cmd))
                                    lock.wait()
                                }
                            }
                            println("第一圈发送公共牌>>>>")
                            publicCard()//公共牌 翻墙

                        } else if (i == 1) {
                            for ((j, u) in messageQueue.withIndex()) {
                                val seat = HasUserSeat(sortSurvival[j])
                                messageQueue[j].bets = getBets(j)   //更新上一家的筹码

                                //结束游戏
                                if (signOut()) break

                                SendData(seat, Msg(1, seat, getTexasJson(messageQueue[j]), "bets", cmd))
                                lock.wait()
                            }
                            // println("开始发送转牌>>>>")
                            SendData(all, Msg(1, 4, getCard(), "turnCard", cmd)) //发送一张公共牌，也叫转牌
                        } else if (i == 2) {
                            for ((j, u) in messageQueue.withIndex()) {
                                val seat = HasUserSeat(sortSurvival[j])

                                messageQueue[j].bets = getBets(j)    //更新上一家的筹码

                                //结束游戏
                                if (signOut()) break

                                SendData(seat, Msg(1, seat, getTexasJson(messageQueue[j]), "bets", cmd))
                                lock.wait()

                            }
                            println("开始发送和牌>>>>")
                            SendData(all, Msg(1, 4, getCard(), "riverCard", cmd)) //发送一张公共牌，也叫和牌
                        } else if (i == 3) {
                            for ((j, u) in messageQueue.withIndex()) {
                                val seat = HasUserSeat(sortSurvival[j])

                                messageQueue[j].bets = getBets(j)    //更新上一家下的筹码


                                //结束游戏
                                if (signOut()) break

                                SendData(seat, Msg(1, seat, getTexasJson(messageQueue[j]), "bets", cmd))
                                lock.wait()

                            }
                            println("开始进入结算程序>>>>")
                            //第四圈发送结算指令
                            balance()
                        }
                    }
                }
            }
        })

    }

    fun getVillage() {
        println("更新庄家坐标")
        village = village + 1
        val max = survivalPeople.size
        if (village == max) {
            village = 0
        }
        println("v>>${village}>>${max}")
    }

    fun sortPlayer() {
        survivalPeople.forEach {
            sortSurvival.add(it)
        }

        val user = sortSurvival.get(village)
        sortSurvival.remove(user)
        sortSurvival.add(sortSurvival.size, user)

    }

    //公共牌
    fun publicCard() {
        var card = JSONArray()
        for (i in 0 until 3) {
            card.put(getCard())
        }
        SendData(all, Msg(1, 4, card, "publiCard", cmd))
    }

    fun balance() {
        SendData(all, Msg(1, 4, "", "balance", cmd))
    }


    //底牌 每人两张
    fun cardAhand() {
        for (i in 0 until sortSurvival.size) {
            var card = JSONArray()
            for (j in 0 until 2) {
                card.put(getCard())

            }

            val seat = HasUserSeat(sortSurvival[i])
            //存储底牌
            aHand.put(seat, card)
            SendData(seat, Msg(1, seat, card, "card", cmd))
        }
    }


    fun stake(seat: Int, root: JSONObject) {
        cachedThreadPool.execute(Runnable {
            run {
                synchronized(lock) {
                    val coin = root["coin"].toString().toLong()
                    if (stake.get(seat) != null) {
                        stake.put(seat, stake.getValue(seat).plus(coin))
                    } else {
                        stake.put(seat, coin)
                    }
                    println("获得玩家的序列号：" + sortSurvival.indexOf(arrPlayers[seat]) + ">>" + coin)

                    messageQueue[sortSurvival.indexOf(arrPlayers[seat])].bets = coin.toInt()

                    SendData(all, Msg(1, seat, coin, "stake", cmd))
                    lock.notify()
                }
            }
        })
    }

    //获得上一家的赌注
    fun getBets(index: Int): Int {
        val max = messageQueue.size

        if (index == 0) {
            messageQueue.reversed().forEach {
                if (it.count != 2) return it.bets
            }
        } else if (index == 7) {
            messageQueue.subList(0, index).reversed().forEach {
                if (it.count != 2) return it.bets
            }
        } else {
            messageQueue.subList(0, index).reversed().forEach {
                if (it.count != 2) return it.bets
            }

            messageQueue.subList(index + 1, max).reversed().forEach {
                if (it.count != 2) return it.bets
            }
        }
        return di
    }


    fun allIn(seat: Int, root: JSONObject) {
        val index = sortSurvival.indexOf(arrPlayers[seat])
        val coin = root["coin"] as Int
        //all in 金币
        if (stake.get(seat) != null) {
            stake.put(seat, stake.getValue(seat).plus(coin))
        } else {
            stake.put(seat, coin.toLong())
        }

        //是否第二圈allIn
        if (allInList.contains(seat)) {
            messageQueue[index].allIn = 1
            messageQueue[index].count = 2
        } else {
            messageQueue[index].bets = coin
            messageQueue[index].allIn = 1
            allInList.add(seat)
        }
        //记录失败玩家
        if (!surrender.contains(seat)) surrender.add(seat)

        synchronized(lock) {
            println("获得玩家的序列号：" + sortSurvival.indexOf(arrPlayers[seat]) + ">>" + coin)

            lock.notify()
            SendData(all, Msg(1, seat, root["coin"] as Int, "allIn", cmd))
        }


        // signOut()   //结束游戏
    }

    fun renounce(seat: Int) {
        //记录弃牌座位号
        if (!renounceList.contains(seat)) renounceList.add(seat)

        val index = sortSurvival.indexOf(arrPlayers[seat])
        messageQueue[index].discard = 1

        synchronized(lock) {
            lock.notify()
            //记录失败玩家
            if (!surrender.contains(seat)) surrender.add(seat)
            SendData(all, Msg(1, seat, "", "renounce", cmd))

            // 弃牌玩家结算
            if (survivalPeople.size - surrender.size == 1) {
                balance() //结算
            }
        }
    }

    //判断是否是最后一个人 发送结算指令，提前结束游戏
    fun signOut(): Boolean {
        if (singOut >= 1) {
            balance() //结算
        }
        println("判断是否是最后一个人 发送结算指令，提前结束游戏>>" + singOut)
        if (survivalPeople.size - surrender.size == 1) {
            singOut++
        }
        return false
    }


    fun DoDa(seat: Int, root: JSONObject) {
        val card = root["card"].toString()
        val strCard = card.substring(1, card.length - 1).split(",")
        var arrayCard = IntArray(strCard.size)

//        // 丢弃弃牌
//        var card0 = 0;
//        if (arrayCard.contains(0)) {
//            card0++
//            return
//        }

        for (i in 0 until strCard.size) {
            arrayCard[i] = strCard.get(i).toInt()
        }
        cardCompare.put(seat, arrayCard)

        if ((survivalPeople.size - surrender.size) == cardCompare.size) {
            RoundCount()
        }

    }

    override fun Ai() {}


    override fun RoundCount() {
        var integral = HashMap<Int, Long>()

        if (cardCompare.size >= 2) {
            var winCard = CompareCard(cardCompare)
            val winPeople = ArrayList<Int>()    //赢的人

            //获取赢得座位号
            winCard.forEach {
                winPeople.add(it.card.first)
            }

            val winJson = JSONObject()
            val totalCoin: Long = stake.values.sum()        //总金币
            var everyBets: Long = 0

            if (winPeople.size >= 2) {
                val s: Double = 0.0
                everyBets = totalCoin / winPeople.size
            } else {
                everyBets = totalCoin
            }

            val win = JSONArray()

            winPeople.forEach {
                var obj = JSONObject()
                obj.put("seat", it)
                obj.put("coin", everyBets)
                obj.put("card", aHand[it])         //手牌
                integral.put(it, everyBets)       //结算
                win.put(obj)
                stake.remove(it)                  //移除赢的人
            }
            val fail = JSONArray()
            stake.forEach { t, u ->
                integral.put(t, u.inv() + 1)       // 输的玩家
            }

            winJson.put("winPeople", win)
            winJson.put("failPeople", fail)

            SendData(all, Msg(1, 4, winJson, "win", cmd))

        } else {
            val winJson = JSONObject()
            val totalCoin: Long = stake.values.sum()        //总金币
            val seat = cardCompare.keys.iterator().next()
            val win = JSONArray()
            var obj = JSONObject()
            obj.put("seat", seat)
            obj.put("coin", totalCoin)
            obj.put("card", aHand[seat])         //手牌
            win.put(obj)
            integral.put(cardCompare.keys.iterator().next(), totalCoin)     //赢的人
            stake.remove(seat)      //移除的人

            val fail = JSONArray()
            stake.forEach { t, u ->
                integral.put(t, u.inv() + 1)
            }

            winJson.put("winPeople", win)
            winJson.put("failPeople", fail)
            SendData(all, Msg(1, 4, winJson, "win", cmd))
        }


        //正常结算和军团结算
        if (armyBoo == 1) {
            //通知玩家返回军团战场景
            SendData(all, Msg(1, 1, "", "armyWar", "hall_armyWar"))
            armyScore(integral)
        } else {
            updateUserCoin(integral)
        }

        RoundEnd()      //开始下一轮
    }

    fun updateUserCoin(meansChange: HashMap<Int, Long>) {
        meansChange.forEach { t, u ->
            val user = arrPlayers[t] as User
            println(">>>" + user.toString())
            user.let {
                //积分
                var num = mutableMapOf<String, Int>()
                num["exp"] = 0

                if (u > 0) {
                    num["exp"] = 3
                    num["coin"] = u.toInt()
                } else {
                    num["exp"] = -1
                }
                //积分
                BonusCalculationService.bonusCalculation(num, user)
                val user = UserService.getUserMsgByUID(user.uid)
                val r = UserService.updateUserCoin(user.coin + u, user.uid)
            }
        }
    }

    fun armyScore(meansChange: HashMap<Int, Long>) {
        meansChange.forEach { t, u ->
            val user = arrPlayers[t] as User
            user.let {
                val armyId = ArmyAdminService.userArmyInfo(user.uid).armyId
                val action = ArmyRankingService.queryByArmyId(armyId)
                if (action != null) {
                    val score = action.score + u
                    ArmyRankingService.updateRanking(armyId, score)
                } else {
                    val army = ArmyAdminService.getArmyInfo(armyId)
                    val armyRanking = ArmyRanking(army.id, u, army.name, army.icon, army.armyTitle)
                    ArmyRankingService.insertRanking(armyRanking)
                }
            }
        }
    }


    override fun RoundEnd() {
        isStart = false
        for ((i, user) in arrPlayers.withIndex()) {
            if (user == null) {
                arrSeats[i] = 0
                continue
            }
            if (user.uid == 1) {
                arrSeats[i] = 0
                OnUserLeave(i)
            }
            if (arrSeats[i] > 0) arrSeats[i] = 2
            if (arrSeats[i] < 1) arrSeats[i] = 0
        }
        println("重新开始游戏>>>")
        RoundRestart()
    }


    //重置一些值
    override fun RoundReset() {
        println("开始清理缓存>>>>")
        val max = arrPlayers.size
//        if (village == (max - 1)) {
//            village = 0
//        } else {
//            village = village + 1
//        }
        //清理缓存

        choose.clear()
        stake.clear()
        survivalPeople.clear()
        sortSurvival.clear()
        messageQueue.clear()
        allInList.clear()
        renounceList.clear()
        surrender.clear()
        cardCompare.clear()
        aHand.clear()
        singOut = 0     //重复触发结算标志

    }

    override fun RoundRestart() {
        if (isStart) return
        if (numCur == 1) {
            return
        }

        println("等待8秒 进入下一轮游戏")

        val ce = java.lang.Runnable {
            RoundReset()    //重置缓存值
            RoundStart()    //重新开始
        }
        serviceScheduled.schedule(ce, 8, TimeUnit.SECONDS)
    }

    override fun TimeOut_DoAi() {}

    fun saps(): Array<User> {     //safe arr players
        val safeArr = Array<User>(numMax, { RoomController.RoomUser })
        for (i in 0 until numMax)
            arrPlayers[i]?.let { safeArr[i] = it }
        return safeArr
    }

    fun getCard(): Int {
        val index = Random().nextInt(choose.size)
        choose.remove(index)
        return LogicDz.cardList[index]
    }
}

