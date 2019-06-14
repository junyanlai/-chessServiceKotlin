package com.shine.controller.poker.ssz

import com.shine.agent.Agent
import com.shine.amodel.ArmyRanking
import com.shine.amodel.Room
import com.shine.amodel.User
import com.shine.aservice.army.ArmyAdminService
import com.shine.aservice.army.ArmyRankingService
import com.shine.aservice.currency.BonusCalculationService
import com.shine.aservice.notice.NoticeService.jpWinBroadcast
import com.shine.aservice.user.UserService
import com.shine.controller.aHall.Hall
import com.shine.controller.aHall.Hall.listRobotName
import com.shine.controller.aHall.RoomController
import com.shine.controller.poker.ssz.tool.SszUtil
import com.shine.controller.poker.ssz.tool.autoPlayCards
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class  TableSsz constructor(override val rid: Int, override val creator: Int, override val pwd: String,
                           override val di: Int, override val roundMax: Int,
                           override val mTime: Int, override var armyBoo: Int) : Room {

    override val numMax = 4
    override val numMin = 4
    override val type = "ssz"
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
    override val mapHalfRoom = RoomController.mapHalfRoom_ssz

    override val switchLog = true
    override var switchAi = true
    override val agentRoom = RoomController.RoomAgent
    override val all = 255
    override val cmd = "msg_" + type
    var score = IntArray(4) { 0 }
    var glod = IntArray(4) { 0 }

    override val rand = Random()
    override var cardMount = IntArray(52)
    override val serviceScheduled = Executors.newSingleThreadScheduledExecutor()


    //...
    override fun HasUserSeat(user: User) = arrPlayers.indexOf(user)

    override fun OnClientClose(user: User) {
        val seat = HasUserSeat(user)
        OnUserLeave(seat)
    }

    override fun OnUserLeave(seat: Int) {
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
        var allReady = true
        for (s in arrSeats)
            if (s != 2)
                allReady = false

        if (allReady)
            RoundStart()
    }

    override fun OnUserSit(user: User): Int {
        if (arrPlayers.contains(user)) {
            val seat = arrPlayers.indexOf(user)
            if (isStart) {
                arrSeats[seat] = 2;mapHalfRoom.remove(arrPlayers[seat]?.uid)
            } else arrSeats[seat] = 1

            user.rid = rid
            SendData(all, Msg(1, seat, saps(), "sit", cmd))
            if (user.uid != 1) mapUserRoom.put(user.uid, this)
            numCur++

            //坐下就准备
            OnUserReady(seat)

            return 1
        }
        RoomSeatSync()//同步人数
        if (isStart) return 2
        if (numCur == numMax) return 3

        for ((i, u) in arrPlayers.withIndex()) {
            if (u == null) {
                arrSeats[i] = 1
                arrPlayers[i] = user
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
        println("#_________Maj_Log")
        println("#_________arrSeats={[${arrSeats[0]}],[${arrSeats[1]}],[${arrSeats[2]}],[${arrSeats[3]}]}")


        println("#_________arrPlayer0=${arrPlayers[0]}")
        println("#_________arrPlayer1=${arrPlayers[1]}")
        println("#_________arrPlayer2=${arrPlayers[2]}")
        println("#_________arrPlayer3=${arrPlayers[3]}")


        println("#_________mapRoom")
        println("#_________size=:${mapRoom.size}")
        println("#_________content=:${mapRoom}")
        println("#_________mapUserRoom")
        println("#_________size=:${mapUserRoom.size}")
        println("#_________content=:${mapUserRoom}")
        println("#_________mapHalfRoom")
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


    //=====Logic Part
    var seatPoint = -1
    var seatBanker = -1
    val cardCompare = HashMap<Int, IntArray>()//比较牌
    //val cardFlower = Array(4) { IntArray(13) }//分发牌
    val cardFlower = ArrayList<IntArray>()//分发牌
    var cardlist: MutableList<IntArray> = ArrayList()      //保存的是有序的牌组（位置）

    override fun SeatNext(seat: Int) = if (seat == (numMax - 1)) 0 else (seat + 1)
    override fun SeatLast(seat: Int) = if (seat == 0) (numMax - 1) else (seat - 1)

    override fun RoundStart() {
        isStart = true  //房间是否开始
        roundCur++      //房间数

        if (arrSeats.filter { it == 2 }.size == 4) {
            GameFapai()
        }
    }

    override fun Status(seat: Int) {
        if (!switchAi) return
    }

    fun SeatBanker() = if (seatBanker == -1) 0 else SeatNext(seatBanker)

    //发牌
    override fun GameFapai() {
        var index = 0;
        val list = SszUtil.GetCardMount();//乱序的牌

        for (i in 0..3) {
            var setArray = IntArray(13)
            for (j in 0..12) {
                setArray[j] = list.get(index)
                index++
            }
            cardFlower.add(setArray)
        }
        var arrayJosn_0 = JSONArray()
        var arrayJosn_1 = JSONArray()
        var arrayJosn_2 = JSONArray()
        var arrayJosn_3 = JSONArray()

        for ((i, v) in cardFlower[0].withIndex()) {
            arrayJosn_0.put(v)
        }

        for ((i, v) in cardFlower[1].withIndex()) {
            arrayJosn_1.put(v)
        }

        for ((i, v) in cardFlower[2].withIndex()) {
            arrayJosn_2.put(v)
        }

        for ((i, v) in cardFlower[3].withIndex()) {
            arrayJosn_3.put(v)
        }

        for (i in 0..3) {
            val msgFapai = JSONObject()
            if (i == 0) msgFapai.put("card", arrayJosn_0)
            if (i == 1) msgFapai.put("card", arrayJosn_1)
            if (i == 2) msgFapai.put("card", arrayJosn_2)
            if (i == 3) msgFapai.put("card", arrayJosn_3)
            SendData(i, Msg(1, i, msgFapai, "fapai", cmd))
        }

        //发完牌之后，如果玩家超时未打牌，则自动打牌
        TimeOut_DoAi()

    }


    //备注：传过来的牌是无序的（牌组是有序的）
    fun DoDa(seat: Int, daMap: HashMap<String, String>) {

        val card = daMap.get("card").toString()
        var data = daMap.get("data")
        var StrCard = card.split(",")
        var intCard = IntArray(13)
        for ((i, v) in StrCard.withIndex()) { //转成InterArray
            intCard[i] = v.toInt()
        }
        cardCompare.put(seat, intCard)
        //校验收到的牌
        if (checkCard(intCard, cardFlower[seat])) {
            //收到da的消息返回通知
            SendData(seat + numMax, Msg(1, seat, "", "da", cmd))
            cardCompare.put(seat, intCard)
        }

        try {
            if (cardCompare.size == 4) { //排成有序的
                for (i in 0..3) {
                    cardlist.add(cardCompare.get(i) as IntArray)
                }
                when (data) {
                    "da" -> Da(cardlist, di)
                }
            }
        } catch (e: Exception) {
        }
    }


    //发送打牌的结果
    fun Da(list: MutableList<IntArray>, dun: Int) {
        val cardJosn = JSONObject()
        for ((i, v) in cardCompare) {
            when (i) {
                0 -> cardJosn.put("card_0", v)
                1 -> cardJosn.put("card_1", v)
                2 -> cardJosn.put("card_2", v)
                3 -> cardJosn.put("card_3", v)
            }
        }
        //发送公开结果（牌组）
        SendData(all, Msg(0, 4, cardJosn, "cards", cmd))

        val arrayList = LogicSsz.CompareCard(list, dun)

        val jpArray = Hall.jPRandom(arrayList[4][1].size)  // 这一组都是分

        // 第五个list 是由两个数组组成，分数和金币
        for ((i, v) in arrayList.withIndex()) {
            val reJosn = JSONObject()
            val resultJson = JSONObject()   //相加的分数和金币
            var list = v as List<IntArray>
            for ((j, s) in list.withIndex()) {
                if (i <= 3) {
                    when (j) {
                        0 -> reJosn.put("front", s)
                        1 -> reJosn.put("centre", s)
                        2 -> reJosn.put("behind", s)
                    }
                } else if (i == 4) {        //第四个list保存的是分数和金币
                    when (j) {
                        0 -> {
                            reJosn.put("score", s)

                            for ((i, g) in s.withIndex()) {
                                score[i] = score[i] + g
                            }
                            resultJson.put("score", score)
                        }
                        1 -> {
                            reJosn.put("gold", s)
                            for ((j, g) in s.withIndex()) {
                                glod[j] = glod[j] + g
                            }
                            resultJson.put("gold", glod)
                        }
                    }
                }
            }
            if (i <= 3) {
                SendData(i, Msg(0, i, reJosn, "balance", cmd))
            }

            if (i == 4) {
                SendData(all, Msg(0, i, reJosn, "gold", cmd))

            }

        }
        //JP公共消息
        SendData(all, Msg(0, 1, jpArray, "jp", cmd))
        //正常结算和军团结算
        if (armyBoo == 1) {
            //通知玩家返回军团战场景
            SendData(all, Msg(1, 1, "", "armyWar", "hall_armyWar"))
            armyScore(arrayList[4][1])
        } else {
            updateUserCoin(arrayList[4][1], jpArray)
        }
        RoundEnd()//是否结束当前回合
    }


    fun updateUserCoin(meansChange: IntArray, jpArray: JSONArray) {
        var map = HashMap<Int, Long>()
        if (jpArray.length() > 0) {
            map = Hall.jsonArrayTomap(jpArray)
        }

        for ((i, v) in arrPlayers.withIndex()) {
            if (v != null) {

                if (v.uid == 1) continue
                //积分
                var num = mutableMapOf<String, Int>()
                num["exp"] = 0

                if (map.containsKey(i)) {
                    val jpCoin = map.get(i) as Long
                    val user = UserService.getUserMsgByUID(v.uid)
                    //全发送服JP公告
                    jpWinBroadcast(user, jpCoin.toString())

                    if (meansChange[i] > 0) {
                        num["exp"] = 3
                        num["coin"] = meansChange[i]
                    } else {
                        num["exp"] = -1
                    }
                    //积分
                    BonusCalculationService.bonusCalculation(num, v)

                    val r = UserService.updateUserCoin(user.coin + meansChange[i] + jpCoin, user.uid)
                } else {
                    if (meansChange[i] > 0) {
                        num["exp"] = 3
                        num["coin"] = meansChange[i]
                    } else {
                        num["exp"] = -1
                    }
                    //积分
                    BonusCalculationService.bonusCalculation(num, v)

                    val user = UserService.getUserMsgByUID(v.uid)
                    val r = UserService.updateUserCoin(user.coin + meansChange[i], user.uid)

                }
            }
        }

    }

    fun armyScore(meansChange: IntArray) {
        for ((i, v) in arrPlayers.withIndex()) {
            if (v != null) {
                if (v.uid == 1) continue
                val armyId = ArmyAdminService.userArmyInfo(v.uid).armyId
                val action = ArmyRankingService.queryByArmyId(armyId)
                if (action != null) {
                    val score = action.score + meansChange[i]
                    ArmyRankingService.updateRanking(armyId, score)
                } else {
                    if (v.uid == 1) continue
                    val army = ArmyAdminService.getArmyInfo(armyId)
                    val armyRanking = ArmyRanking(army.id, meansChange[i].toLong(), army.name, army.icon, army.armyTitle)
                    ArmyRankingService.insertRanking(armyRanking)
                }
            }
        }
    }


    //校验收到的牌是否正确
    fun checkCard(array_1: IntArray, array_2: IntArray): Boolean {
        var boo = true
        for ((i, e) in array_2.withIndex()) {
            if (!array_2.contains(array_1[i])) boo = false
        }
        return boo
    }

    //如果超时，则生成牌
    override fun Ai() {
        var list: MutableList<Int> = ArrayList()

        for ((i, v) in cardFlower.withIndex()) {
            list.add(i)
        }

        for ((i, v) in cardCompare) {
            list.remove(i)
        }
        for ((i, v) in list.withIndex()) {
            var json = JSONObject()
            var daMap = HashMap<String, String>()
            daMap.put("card", autoPlayCards.aiCard(cardFlower[v]).toString())
            daMap.put("data", "da")
            DoDa(v, daMap)
        }
    }


    override fun RoundCount() {
        //通知前端开启一下一轮倒计时
        SendData(all, Msg(1, 1, "", "next", cmd))
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
        if (roundCur >= roundMax) RoomDelete()
        else RoundRestart()

    }


    //重新启动一局游戏
    override fun RoundReset() {
        //在每轮发牌之前，确保接收容器里面为空（会影响Ai判断人数）
        cardFlower.clear()
        cardCompare.clear()
        cardlist.clear()            //情况座位号排序好的list
    }

    override fun RoundRestart() {
        //通知开启下一回合
        RoundCount()
        if (isStart) return
        timeWait = 30
        val callable = java.lang.Runnable {
            RoundReset()    //重置缓存值
            playCards_Ai()
        }
        serviceScheduled.schedule(callable, timeWait.toLong(), TimeUnit.SECONDS)
    }

    override fun TimeOut_DoAi() {
//        if (isStart) return
        if (!switchAi) return
        timeWait = 10
        val callable = java.lang.Runnable {
            Ai()
        }
        serviceScheduled.schedule(callable, timeWait.toLong(), TimeUnit.SECONDS)
    }

    fun saps(): Array<User> {     //safe arr players

        val safeArr = Array<User>(numMax, { RoomController.RoomUser })
        for (i in 0 until numMax)
            arrPlayers[i]?.let { safeArr[i] = it }
        return safeArr
    }


    fun playCards_Ai() {
        for (i in 0 until numMax) {
            if (arrSeats[i] == 0) {
                val user = User(
                        uid = 1,
                        sex = 1,    //初始为1
                        avatar = Random().nextInt(9).toString(),
                        nick = listRobotName[Random().nextInt(34270)])
                OnUserSit(user)
            }
        }
    }

    fun addPlayCards_Ai() {
        //8秒钟之后加入机器人
        if (isStart) return
        if (!switchAi) return
        timeWait = 8
        val callable = java.lang.Runnable {
            playCards_Ai()
        }
        serviceScheduled.schedule(callable, timeWait.toLong(), TimeUnit.SECONDS)
    }
}

