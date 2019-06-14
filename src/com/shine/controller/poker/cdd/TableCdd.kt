package com.shine.controller.poker.cdd

import com.shine.agent.Agent
import com.shine.amodel.ArmyRanking
import com.shine.amodel.Room
import com.shine.amodel.User
import com.shine.aservice.army.ArmyAdminService
import com.shine.aservice.army.ArmyRankingService
import com.shine.aservice.currency.BonusCalculationService
import com.shine.aservice.notice.NoticeService
import com.shine.aservice.user.UserService
import com.shine.controller.aHall.Hall
import com.shine.controller.aHall.Hall.jPRandom
import com.shine.controller.aHall.RoomController
import com.shine.controller.poker.cdd.LogicCdd.getHuaSe
import com.shine.controller.poker.cdd.LogicCdd.getcardTypeCdd
import com.shine.controller.poker.cdd.LogicCdd.jugdeType
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 *  Create by Colin
 *  Date:2018/6/8.
 *  Time:16:16
 */
class TableCdd constructor(override val rid: Int, override val creator: Int, override val pwd: String,
                           override val di: Int, override val roundMax: Int, override val mTime: Int, val mMen: Boolean, override var armyBoo: Int) : Room {

    override val numMax = 4
    override val numMin = 4
    override val type = "cdd"
    override val timeCreate = System.currentTimeMillis()

    override val rand = Random()
    override var numCur = 0
    override var timeWait = 0
    override var roundCur = 0

    override var isStart = false
    override val arrSeats = IntArray(numMax)
    override val arrPlayers = arrayOfNulls<User>(numMax)
    override val arrLeavers = IntArray(numMax)

    override val mapRoom = Hall.mapRoom
    override val mapUserRoom = Hall.mapUserRoom
    override val mapHalfRoom = RoomController.mapHalfRoom_cdd

    override var switchAi = true
    override val switchLog = true
    override val serviceScheduled = Executors.newSingleThreadScheduledExecutor()

    override val agentRoom = RoomController.RoomAgent
    override val all = 255
    override val cmd = "msg_" + type

    var switchDone = false
    val switchMark = BooleanArray(4)
    val switchCount = IntArray(4)
    val switchCards = MutableList(0, { 0 })

    val intarr = IntArray(3)
    val meansFinal = IntArray(4)
    val cardBox = MutableList(0, { intarr })
    val cardBox2 = MutableList(0, { intarr })
    var onedone = true
    var firstCall = false
    var timesPass = 0

    var timeDo = 0
    var cardMen = 0
    var seatWin = -1
    var seatPoint = -1
    val cardArray = Array(numMax) { MutableList(0, { 0 }) }
    val cardKinds = Array(numMax) { MutableList(0, { cardTypeCdd.ERROR }) }


    //...
    fun saps(): Array<User> {

        val safeArr = Array(numMax, { RoomController.RoomUser })
        for (i in 0 until numMax)
            arrPlayers[i]?.let { safeArr[i] = it }
        return safeArr
    }

    override fun HasUserSeat(user: User) = arrPlayers.indexOf(user)

    override fun OnClientClose(user: User) {
        val seat = HasUserSeat(user)
        OnUserLeave(seat)
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

            return 1
        }

        RoomSeatSync()

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


    //
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


    override fun Log() {
        if (!switchLog) return

        println()
        println("#_________Cdd_Log")
        println("#_________arrSeats={[${arrSeats[0]}],[${arrSeats[1]}],[${arrSeats[2]}],[${arrSeats[3]}]}")


        println("#_________arrPlayer0=${arrPlayers[0]}")
        println("#_________arrPlayer1=${arrPlayers[1]}")
        println("#_________arrPlayer2=${arrPlayers[2]}")
        println("#_________mapUserRoom")
    }

    override fun SendData(seat: Int, msg: String) {
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

    //=====LogicMaj Part
    override var cardMount = LogicCdd.GetCardMount()

    override fun SeatNext(seat: Int) = if (seat == (numMax - 1)) 0 else (seat + 1)
    override fun SeatLast(seat: Int) = if (seat == 0) (numMax - 1) else (seat - 1)

    override fun GameFapai() {

        LogicCdd.MountSlice(cardMount, cardArray)
        if (mMen) {
            val seatR = rand.nextInt(4)
            var cardR = rand.nextInt(12)
            if (cardArray[seatR][cardR] == 3) cardR++

            cardMen = cardArray[seatR][cardR]
//            cardArray[seatR][cardR] = 0x4E
        }

        for (i in 0..3) {
            SendData(i, Msg(cardMen, i, cardArray[i], "fapai", cmd))
            var card = mutableListOf<Int>()
            cardSort(card, cardArray[i], 1)
            cardArray[i].clear()
            cardArray[i].addAll(card)
            card.clear()
        }
    }


    fun DoSwitch(seat: Int, cards: IntArray) {

        if (switchDone) {
            SendData(seat, Msg(0, seat, "switch done", "switch", cmd))
            return
        }
        if (!cardArray[seat].containsAll(cards.asList())) {
            SendData(seat, Msg(0, seat, "wrong card", "switch", cmd))
            return
        }
        if (switchMark[seat]) {
            SendData(seat, Msg(0, seat, "switched", "switch", cmd))
            return
        }

        switchMark[seat] = true
        switchCount[seat] = cards.size
        for (c in cards) {
            switchCards.add(c)
            cardArray[seat].remove(c)
        }

        SendData(seat, Msg(1, seat, "success", "switch", cmd))

        if (switchMark[0] && switchMark[1] && switchMark[2] && switchMark[3])
            SwitchDone()
    }

    fun SwitchDone() {

        switchDone = true
        if (switchCards.size == 0) {
            seatPoint = LogicCdd.Seat3(cardArray)
            SendData(all, Msg(0, seatPoint, "[]", "switchdone", cmd))
            return
        }

        switchCards.shuffle()
        val cardsDone = Array(numMax) { MutableList(0, { 0 }) }
        for (i in 0..3) {

            if (switchCount[i] == 0) continue
            for (j in 0 until switchCount[i]) {
                cardsDone[i].add(switchCards.first())
                cardArray[i].add(switchCards.first())
                switchCards.removeAt(0)
            }
        }

        seatPoint = LogicCdd.Seat3(cardArray)
        for (i in 0..3)
            SendData(i, Msg(1, seatPoint, cardsDone[i], "switchdone", cmd))

    }

    fun DoSwitchPass(seat: Int) {

        if (switchMark[seat]) {
            SendData(seat, Msg(0, seat, "switched", "pass", cmd))
            return
        }

        switchMark[seat] = true
        switchCount[seat] = 0
        SendData(seat, Msg(1, seat, "switched", "pass", cmd))

        if (switchMark[0] && switchMark[1] && switchMark[2] && switchMark[3])
            SwitchDone()
    }

    fun DoPass(seat: Int) {

        // SendData(all, Msg(1, seat, "success", "pass", cmd))

        if (!switchDone) {
            DoSwitchPass(seat)
            return
        }
        if (seat != seatPoint) {
            SendData(seat, Msg(0, seat, "wrong order", "pass", cmd))
            return
        }

        if (onedone) {
            SendData(seat, Msg(0, seat, "round done", "pass", cmd))
            return
        }
        cardBox2.add(0, intArrayOf(0))
        timeDo++
        timesPass++
        seatPoint = SeatNext(seat)
        SendData(all, Msg(1, seat, "success", "pass", cmd))
        if (timesPass == 3) {
            //println(SeatNext(seat).toString() + "<-下家 三家不要 老大出牌 本家->" + seat)
            timesPass = 0
            onedone = true
            TimeOut_Da(SeatNext(seat))
        } else {
            //println("420 PASS ")
            for ((k, v) in pai) {
                TimeOut_Da(SeatNext(seat), v.toIntArray())
                //TimeOut_Pa(SeatNext(seat))
                //TimeOut_Pa()
            }
        }
    }

    fun noSwitch(seat: Int) {
        if (!switchDone) {
            DoSwitchPass(seat)
            return
        }
    }

    //排序
    fun cardSort(card: MutableList<Int>, cards: MutableList<Int>, count: Int) {
        var count = count
        if (count == 15) return
        for (i in cards) {
            if (getcardTypeCdd(i) == count) {
                card.add(i)
            }
        }
        count += 1
        cardSort(card, cards, count)
    }

    val pai: MutableMap<Int, MutableList<Int>> = HashMap()
    fun DoDa(seat: Int, cards: IntArray) {
        if (cards.size == 0 || cards == null) {
            DoPass(seat)
            return
        }
//        println(seat.toString() + " 当前玩家剩余牌面 " + cardArray[seat].toList())
//        println(seat.toString() + "当前玩家 出牌 " + cards.toList())
//        println(seat.toString() + "DoDa 1 上家牌" + pai.toString())
        if (!switchDone) {
            SendData(seat, Msg(0, seat, "unswitch", "da", cmd))
            return
        }
        if (seat != seatPoint) {
            SendData(seat, Msg(0, seat, "wrong order", "da", cmd))
            return
        }
        if (!cardArray[seat].containsAll(cards.asList())) {
            SendData(seat, Msg(0, seat, "wrong card", "da", cmd))
            return
        }
        if (LogicCdd.Type(cards).order == 0) {
            SendData(seat, Msg(0, seat, "wrong type", "da", cmd))
            return
        }
        if (onedone) {//call
            if (!firstCall && !cards.contains(3)) {
                SendData(seat, Msg(0, seat, "wrong call", "da", cmd))
                return
            }
            if (!firstCall && cards.contains(3)) firstCall = true
            onedone = false
        } else//follow
            if (!LogicCdd.Done(cardBox[0], cards)) {
                SendData(seat, Msg(0, seat, "less weight", "da", cmd))
                return
            }
        cardArray[seat].removeAll(cards.asList())
        cardBox.add(0, cards)
        cardBox2.add(0, cards)
        cardKinds[seat].add(LogicCdd.Type(cards))
        timeDo++
        timesPass = 0
        seatPoint = SeatNext(seat)
        SendData(all, Msg(1, seat, cards, "da", cmd))

        if (cardArray[seat].size == 0) {
           // println("最后一张结算了")
            isStart = false
            seatWin = seat
            seatPoint = -1
            RoundCount()
            RoundEnd()
            pai.clear()
            cardArray.toMutableList().clear()
            return
        }
       // println(seat.toString() + "DoDa 3 上家牌" + pai.toString())
        if (pai.size == 0) {
           // println("DoDa 1 行 更换上家牌" + cards.toList())
            pai.clear()
            pai.put(seat, cards.toMutableList())
        } else if (cardTypeCdd.ERROR != jugdeType(cards)) {
            for ((k, v) in pai) {
                if (jugdeType(cards) == jugdeType(v.toIntArray()) || k == seat) {
                   // println("DoDa  2 行 更换上家牌" + cards.toList())
                    pai.clear()
                    pai.put(seat, cards.toMutableList())
                } else {
                    //println("DoDa 590 行 pass")
                    DoPass(seat)
                }
            }
        }
        var card = mutableListOf<Int>()
        if (pai[seat] == null) {
           // println("如果pai 时空的 咋整呢 不出牌 就要商家的牌" + pai[getNextUser(seat)]!!.toMutableList())
            card = pai[getNextUser(seat)]!!.toMutableList()
        } else {
            card = pai[seat]!!.toMutableList()
        }
       // println("DoDa 595 出牌人：" + seat.toString() + " 出的牌 " + card)
        TimeOut_Da(SeatNext(seat), card.toIntArray())
//    TimeOut_Pa(SeatNext(seat))
    }

    fun getNextUser(seat: Int): Int {
        when (seat) {
            0 -> {
                return 3
            }
            1 -> {
                return 0
            }
            2 -> {
                return 1
            }
            3 -> {
                return 2
            }
        }
        return seat
    }

    //cards  是当前人出的牌 下个人打牌
    fun getcard(seat: Int, cards: IntArray): IntArray {
        var card = IntArray(cards.size)
        if (cardArray[seat].size >= cards.size) {
            //println("出牌人：" + seat.toString() + " getcard方法上家牌：" + cards.toList() + " 牌型 " + LogicCdd.jugdeType(cards))
            when (LogicCdd.jugdeType(cards)) {
                cardTypeCdd.SINGLE -> {
                    card = disposeSINGLE(cardArray[seat].toIntArray(), cards)
                }    //单
                cardTypeCdd.DOUNLE -> {
                    card = disposeDOUNLE(cardArray[seat].toIntArray(), cards)
                }    //双
                cardTypeCdd.THREE -> {
                    card = disposeTHREE(cardArray[seat].toIntArray(), cards)
                }     //三
                cardTypeCdd.FOUR -> {
                    card = disposeFOUR(cardArray[seat].toIntArray(), cards)
                }      //四
                cardTypeCdd.FIVESC -> {
                    card = disposeFIVESC(cardArray[seat].toIntArray(), cards)
                }    //同花顺
                cardTypeCdd.FOUR1 -> {
                    card = disposeFOUR1(cardArray[seat].toIntArray(), cards)
                }     //铁支
                cardTypeCdd.HULU -> {
                    card = disposeHULU(cardArray[seat].toIntArray(), cards)
                }      //葫芦
                cardTypeCdd.TONGHUA -> {
                    card = disposeTONGHUA(cardArray[seat].toIntArray(), cards)
                }   //同花
                cardTypeCdd.FIVE -> {
                    card = disposeFIVE(cardArray[seat].toIntArray(), cards)
                }      //杂顺子

            }
        } else {
           // println("getcard方法里面的PASS 1 : " + seat)
            DoPass(seat)
        }
        if (card.size != null && card.size == cards.size) {
          //  println("getcard方法里面的返回值 : " + card.toList())
            return card
        } else {
          //  println("getcard方法里面的PASS 2 : " + seat)
            DoPass(seat)
        }
       // println("getcard方法里面的返回参数 2 : " + card.toList())
        card.toMutableList().clear()
        return card
    }

    fun disposeSINGLE(myCard: IntArray, cards: IntArray): IntArray {
        var card = mutableListOf<Int>()
        for (i in myCard) {
            when {
                getcardTypeCdd(cards[0]) == getcardTypeCdd(i) && getHuaSe(i) > getHuaSe(cards[0]) -> {
                    card.add(i)
                    return card.toIntArray()
                }
                getcardTypeCdd(cards[0]) < getcardTypeCdd(i) -> {
                    card.add(i)
                    return card.toIntArray()
                }
            }
        }
        return card.toIntArray()
    }

    fun disposeDOUNLE(myCard: IntArray, cards: IntArray): IntArray {
        var car = myCard[0]
        var card = mutableListOf<Int>()
        for (i in myCard) {
            if (getcardTypeCdd(car) == getcardTypeCdd(i) && getcardTypeCdd(cards[0]) < getcardTypeCdd(i)) {
                card.add(i)
            } else {
                car = i
                card.clear()
                card.add(i)
            }
            if (card.size == 2) {
                return card.toIntArray()
            }
        }
        return card.toIntArray()
    }

    fun huoqusanzhang(card: MutableList<Int>, cards: MutableList<Int>, count: Int) {
        var count = count
        if (count == 13) return
        for (i in cards) {
            if (getcardTypeCdd(i) == count) {
                card.add(i)
            }
        }
        count += 1
        cardSort(card, cards, count)
    }

    fun disposeTHREE(myCard: IntArray, cards: IntArray): IntArray {
        var card = mutableListOf<Int>()
        var car = cards[0]
        var bi = 0
        var c = mutableListOf<Int>()
        for (i in myCard) {
            if (getcardTypeCdd(i) > getcardTypeCdd(car)) {
                for (j in myCard) {
                    if (getcardTypeCdd(i) == getcardTypeCdd(j)) {
                        card.add(j)
                    }
                }
            }
            if (card.size == 3) {
                return card.toIntArray()
            } else {
                card.clear()
            }
        }
        return card.toIntArray()
    }

    fun disposeFOUR(myCard: IntArray, cards: IntArray): IntArray {
        var card = mutableListOf<Int>()
        for (i in 0..myCard.size - 1) {
            if (i + 3 <= myCard.size - 1
                    && getcardTypeCdd(myCard[i]) == getcardTypeCdd(myCard[i + 3])
                    && getcardTypeCdd(myCard[i]) > getcardTypeCdd(cards[0])) {
                card.add(myCard[i])
                card.add(myCard[i + 1])
                card.add(myCard[i + 2])
                card.add(myCard[i + 3])
                return card.toIntArray()
            }
        }
        return card.toIntArray()
    }

    //同花顺
    fun disposeFIVESC(myCard: IntArray, cards: IntArray): IntArray {
        return getTongHuaShun(myCard, cards[0])
    }

    //铁支  金刚
    fun disposeFOUR1(myCard: IntArray, cards: IntArray): IntArray {
        var card = mutableListOf<Int>()
        var isYes = false
        for (i in 0..myCard.size - 1) {
            if (getcardTypeCdd(myCard[i]) > getcardTypeCdd(cards[0])) {
                for (j in 0..myCard.size - 1) {
                    if (getcardTypeCdd(myCard[i]) == getcardTypeCdd(myCard[j])) {
                        card.add(myCard[j])
                        if (card.size == 4) {
                            if (getMinCard(myCard) == card[0]) {
                                card.add(getOneCard(myCard, card[0]))
                                if (card.size == 5) {
                                    return card.toIntArray()
                                }
                            } else {
                                card.add(myCard[0])
                                if (card.size == 5) {
                                    return card.toIntArray()
                                }
                                isYes = true
                                break
                            }
                        }
                    } else {
                        card.clear()
                    }
                }
                if (isYes) {
                    break
                }
            }
        }
        if (getTongHuaShun(myCard, getMinCard(cards)).size != 0) {
            return getTongHuaShun(myCard, getMinCard(cards))
        }
        card.clear()
        return card.toIntArray()
    }

    //同花
    fun disposeTONGHUA(myCard: IntArray, cards: IntArray): IntArray {
        var card = mutableListOf<Int>()
        var maxCard = getMaxCard(cards)
        for (i in myCard) {
            if (i > maxCard && getHuaSe(i) > getHuaSe(maxCard)) {
                card.add(i)
            }
            if (card.size == cards.size) {
                return card.toIntArray()
            }
        }
        card.clear()
        return card.toIntArray()
    }

    //葫芦
    fun disposeHULU(myCard: IntArray, cards: IntArray): IntArray {
        return IntArray(0)
        var card = mutableListOf<Int>()
        var car = getcardTypeCdd(cards[0])
        for (i in 0..myCard.size - 1) {
            if (getcardTypeCdd(i) > car) {
                for (j in 0..myCard.size - 1) {
                    if (getcardTypeCdd(myCard[i]) == getcardTypeCdd(myCard[j])) {
                        card.add(myCard[j])
                        if (card.size == 3) {
                            card.addAll(getTwoCard(myCard, card[0]))
                            if (card.size == 5)
                                return card.toIntArray()
                        }
                    } else {
                        card.clear()
                    }
                }
            }
        }
        if (card.size != 5) {
            if (getTieZhi(myCard).size == 5) {
                return getTieZhi(myCard)
            }
            if (getTongHuaShun(myCard, card[0]).size == 5) {
                return getTongHuaShun(myCard, card[0])
            }
        }
        return IntArray(0)
    }

    //顺子
    fun disposeFIVE(myCard: IntArray, cards: IntArray): IntArray {
        var card = mutableListOf<Int>()
        var lis = 0
        for (i in myCard) {
            if (getcardTypeCdd(i) > getcardTypeCdd(getMinCard(cards))) {
                lis = getcardTypeCdd(i)
                break
            }
        }
        var count = 0
        for (i in 0..myCard.size - 1) {
            if ((lis + count) == getcardTypeCdd(myCard[i])) {
                card.add(myCard[i])
                count++
                if (count == 5) {
                    return card.toIntArray()
                }
            } else if (lis == getcardTypeCdd(myCard[i]) || card.size != 0) {
                count = 1
                card.clear()
                card.add(myCard[i])
               // println(card)
                lis = getcardTypeCdd(myCard[i])
            }
        }
        if (card.size != 5) {
            if (getTongHua(myCard).size == 5) {
                return getTongHua(myCard)
            }
            if (getHuLu(myCard).size == 5) {
                return getHuLu(myCard)
            }
            if (getTieZhi(myCard).size == 5) {
                return getTieZhi(myCard)
            }
            if (getTongHuaShun(myCard, card[0]).size == 5) {
                return getTongHuaShun(myCard, card[0])
            }
        }
        return IntArray(0)
    }

    //最小牌
    fun getMinCard(cards: IntArray): Int {
        if (cards.size == 1) return cards[0]
        var car = cards[0]
        var card = getcardTypeCdd(cards[0])
        for (i in cards) {
            if (getcardTypeCdd(i) < card) {
                card = getcardTypeCdd(i)
                car = i
            }
        }
        return car
    }

    fun getAllcard(cards: IntArray): IntArray {
        var ca = mutableListOf<Int>()
        var car = getMinCard(cards)
        for (i in cards) {
            if (getcardTypeCdd(i) == getcardTypeCdd(car)) {
                ca.add(i)
            }
        }
        return ca.toIntArray()
    }

    //最大牌
    fun getMaxCard(cards: IntArray): Int {
        var car = cards[0]
        var card = getcardTypeCdd(cards[0])
        for (i in cards) {
            if (getcardTypeCdd(i) > card) {
                card = getcardTypeCdd(i)
                car = i
            }
        }
        return car
    }

    fun getOneCard(myCard: IntArray, card: Int): Int {
        for (i in myCard) {
            if (getcardTypeCdd(i) != getcardTypeCdd(card)) {
                return i
            }
        }
        return myCard[0]
    }

    fun getTwoCard(myCard: IntArray, card: Int): MutableList<Int> {
        var cards = mutableListOf<Int>()
        for (i in 0..myCard.size - 1) {
            if (getcardTypeCdd(i) != getcardTypeCdd(card)) {
                for (j in 0..myCard.size - 1) {
                    if (getcardTypeCdd(myCard[i]) == getcardTypeCdd(myCard[j])
                            && getcardTypeCdd(card) != getcardTypeCdd(myCard[j])) {
                        cards.add(myCard[j])
                        if (cards.size == 2) {
                            return cards
                        }
                    } else {
                        cards.clear()
                    }
                }
            }
        }
        return cards
    }

    fun getTieZhi(myCard: IntArray): IntArray {
        var card = mutableListOf<Int>()
        for (i in 0..myCard.size - 1) {
            for (j in 0..myCard.size - 1) {
                if (getcardTypeCdd(myCard[i]) == getcardTypeCdd(myCard[j])) {
                    card.add(myCard[j])
                    if (card.size == 4) {
                        card.add(getOneCard(myCard, card[0]))
                        if (card.size == 5)
                            return card.toIntArray()
                    }
                } else {
                    card.clear()
                }
            }
        }
        return card.toIntArray()
    }

    fun getTongHua(myCard: IntArray): IntArray {
        var meihua = mutableListOf<Int>()
        var fangpian = mutableListOf<Int>()
        var hongtao = mutableListOf<Int>()
        var heitao = mutableListOf<Int>()
        for (i in myCard) {
            when (getHuaSe(i)) {
                0 -> {
                    meihua.add(i)
                    if (meihua.size == 5) {
                        return meihua.toIntArray()
                    }
                }
                1 -> {
                    fangpian.add(i)
                    if (fangpian.size == 5) {
                        return fangpian.toIntArray()
                    }
                }
                2 -> {
                    hongtao.add(i)
                    if (hongtao.size == 5) {
                        return hongtao.toIntArray()
                    }
                }
                3 -> {
                    heitao.add(i)
                    if (heitao.size == 5) {
                        return heitao.toIntArray()
                    }
                }
            }
        }
        return IntArray(0)
    }

    fun getTongHuaShun(myCard: IntArray, minCard: Int): IntArray {
        var card = mutableListOf<Int>()
        var meihua = mutableListOf<Int>()
        var fangpian = mutableListOf<Int>()
        var hongtao = mutableListOf<Int>()
        var heitao = mutableListOf<Int>()
        for (i in myCard) {
            when (getHuaSe(i)) {
                0 -> {
                    meihua.add(i)
                }
                1 -> {
                    fangpian.add(i)
                }
                2 -> {
                    hongtao.add(i)
                }
                3 -> {
                    heitao.add(i)
                }
            }
        }
        if (meihua.size >= 5 && getHuaSe(minCard) <= 0) {
            card.clear()
            var lis = 0
            var start = 0
            when {
                getcardTypeCdd(minCard) == 12 -> start = 1
                getcardTypeCdd(minCard) == 12 -> start = 2
                else -> start = getcardTypeCdd(minCard)
            }
            for (i in meihua) {
                if (getcardTypeCdd(i) > start) {
                    lis = getcardTypeCdd(i)
                    break
                }
            }
            var count = 0
            for (i in 0..meihua.size - 1) {
                if ((lis + count) == getcardTypeCdd(meihua[i])) {
                    card.add(meihua[i])
                    count++
                    if (count == 5) {
                        return card.toIntArray()
                    }
                } else if (lis == getcardTypeCdd(meihua[i]) || card.size != 0) {
                    count = 1
                    card.clear()
                    card.add(meihua[i])
                    //println(card)
                    lis = getcardTypeCdd(meihua[i])
                }
            }
            return card.toIntArray()
        }
        if (fangpian.size >= 5 && getHuaSe(minCard) <= 0) {
            card.clear()
            var lis = 0
            var start = 0
            when {
                getcardTypeCdd(minCard) == 12 -> start = 1
                getcardTypeCdd(minCard) == 12 -> start = 2
                else -> start = getcardTypeCdd(minCard)
            }
            for (i in fangpian) {
                if (getcardTypeCdd(i) > start) {
                    lis = getcardTypeCdd(i)
                    break
                }
            }
            var count = 0
            for (i in 0..fangpian.size - 1) {
                if ((lis + count) == (getcardTypeCdd(fangpian[i]))) {
                    card.add(fangpian[i])
                    count++
                    if (count == 5) {
                        return card.toIntArray()
                    }
                } else if (lis == getcardTypeCdd(fangpian[i])) {
                    card.clear()
                    card.add(fangpian[i])
                    lis = getcardTypeCdd(fangpian[i])
                }
            }
            return card.toIntArray()
        }
        if (hongtao.size >= 5 && getHuaSe(minCard) <= 0) {
            card.clear()
            var lis = 0
            var start = 0
            when {
                getcardTypeCdd(minCard) == 12 -> start = 1
                getcardTypeCdd(minCard) == 12 -> start = 2
                else -> start = getcardTypeCdd(minCard)
            }
            for (i in hongtao) {
                if (getcardTypeCdd(i) > start) {
                    lis = getcardTypeCdd(i)
                    break
                }
            }
            var count = 0
            for (i in 0..hongtao.size - 1) {
                if ((lis + count) == (getcardTypeCdd(hongtao[i]))) {
                    card.add(hongtao[i])
                    count++
                    if (count == 5) {
                        return card.toIntArray()
                    }
                } else if (lis == getcardTypeCdd(hongtao[i])) {
                    card.clear()
                    card.add(hongtao[i])
                    lis = getcardTypeCdd(hongtao[i])
                }
            }
            return card.toIntArray()
        }
        if (heitao.size >= 5 && getHuaSe(minCard) <= 0) {
            card.clear()
            var lis = 0
            var start = 0
            when {
                getcardTypeCdd(minCard) == 12 -> start = 1
                getcardTypeCdd(minCard) == 12 -> start = 2
                else -> start = getcardTypeCdd(minCard)
            }
            for (i in heitao) {
                if (getcardTypeCdd(i) > start) {
                    lis = getcardTypeCdd(i)
                    break
                }
            }
            var count = 0
            for (i in 0..heitao.size - 1) {
                if ((lis + count) == (getcardTypeCdd(heitao[i]))) {
                    card.add(heitao[i])
                    count++
                    if (count == 5) {
                        return card.toIntArray()
                    }
                } else if (lis == getcardTypeCdd(heitao[i])) {
                    card.clear()
                    card.add(heitao[i])
                    lis = getcardTypeCdd(heitao[i])
                }
            }
            return card.toIntArray()
        }
        return IntArray(0)
    }

    fun getHuLu(myCard: IntArray): IntArray {
        var card = mutableListOf<Int>()
        for (i in 0..myCard.size - 1) {
            for (j in 0..myCard.size - 1) {
                if (getcardTypeCdd(myCard[i]) == getcardTypeCdd(myCard[j])) {
                    card.add(myCard[j])
                    if (card.size == 3) {
                        card.addAll(getTwoCard(myCard, card[0]))
                        if (card.size == 5)
                            return card.toIntArray()
                    }
                } else {
                    card.clear()
                }
            }
        }
        card.clear()
        return card.toIntArray()
    }

    fun TimeOut_Da(seat: Int, cards: IntArray) {
        if (!isStart) return
//        if (!switchAi) return
        val callable = java.lang.Runnable {
            if (seatPoint != seat) return@Runnable
            var card = getcard(seat, cards)
            if (card == null || card.size <= 0 || jugdeType(card) != jugdeType(cards)) {
                DoPass(seat)
                return@Runnable
            }
            //println(seat.toString() + " TimeOut cards入参：" + cards.toList())
            DoDa(seat, card)
            //println("######## $seat 号####超时# " + waittime + "秒 da出${card[0]}")
        }
        serviceScheduled.schedule(callable, waittime.toLong(), TimeUnit.SECONDS)
    }

    override fun Status(seat: Int) {
        Log()
        val cardNums = IntArray(numMax)
        for (i in 0 until numMax)
            cardNums[i] = cardArray[i].size

        val status = JSONObject()
        if (cardBox.size == 0) status.put("cardLast", intArrayOf(0))
        else status.put("cardLast", cardBox[0])

        status.put("cardNums", cardNums)

        status.put("di", di)
        status.put("time", mTime)
        status.put("roundMax", roundMax)
        status.put("roundCur", roundCur)
        status.put("isStart", isStart)

        for (i in 0..2) {
            if (cardBox.size < 4)
                if (i == cardBox.size)
                    break
            status.put("cardHistory" + i, cardBox2[i])
        }
        //println("SSS:seatPoint=$seatPoint ")

        SendData(seat, Msg(cardArray[seat], seatPoint, status, "status", cmd))
    }

    override fun RoundStart() {
        isStart = true
        switchDone = false

        roundCur++
        GameFapai()
        TimeOut_Pa()
    }

    override fun RoundCount() {

        val m = LogicCdd.Multiple(cardArray)
        val meansChange = IntArray(numMax)
        val jpArray = jPRandom(meansChange.size)  //jp

        for (i in 0 until numMax) {
            meansChange[i] = di * m[i]
            meansFinal[i] += meansChange[i]
        }
        val detail = JSONObject()
        detail.put("roundCur", roundCur)
        detail.put("multiple", m)

        detail.put("meansChange", meansChange)
        for (i in 0 until numMax)
            detail.put("cardHand" + i, cardArray[i])

        if (roundCur == roundMax)
            detail.put("meansFinal", meansFinal)


        //army by war gameOver count
        if (armyBoo == 1) {

            //通知玩家返回军团战场景
            SendData(all, Msg(1, 1, "", "armyWar", "hall_armyWar"))

            armyScore(meansChange)
            SendData(all, Msg(1, seatWin, detail, "multiples", cmd))
        } else {
            updateUserCoin(meansChange, jpArray)
            SendData(all, Msg(1, seatWin, detail, "multiples", cmd))
            SendData(all, Msg(0, 1, jpArray, "jp", cmd))
        }
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
                    val r = UserService.updateUserCoin(user.coin + meansChange[i] + jpCoin, user.uid)
                    //发送全服JP公告
                    NoticeService.jpWinBroadcast(user, jpCoin.toString())

                    //
                    if (meansChange[i] > 0) {
                        num["exp"] = 3
                        num["coin"] = meansChange[i]
                    } else {
                        num["exp"] = -1
                    }
                    //积分
                    BonusCalculationService.bonusCalculation(num, v)
                } else {
                    val user = UserService.getUserMsgByUID(v.uid)
                    val r = UserService.updateUserCoin(user.coin + meansChange[i], user.uid)

                    //
                    if (meansChange[i] > 0) {
                        num["exp"] = 3
                        num["coin"] = meansChange[i]
                    } else {
                        num["exp"] = -1
                    }
                    //积分
                    BonusCalculationService.bonusCalculation(num, v)
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


    override fun RoundEnd() {

        isStart = false
        for ((i, user) in arrPlayers.withIndex()) {
            if (user == null) {
                arrSeats[i] = 0
                continue
            }

            /**
             * 单回合结束离场
             * if (user.uid==1){
            arrSeats[i]=0
            OnUserLeave(i)
            }*/
            if (arrSeats[i] > 0) arrSeats[i] = 2
            if (arrSeats[i] < 1) arrSeats[i] = 0
        }

        if (roundCur >= roundMax) RoomDelete()
        else RoundRestart()
    }

    override fun RoundReset() {

        timeWait = Random().nextInt(10) + 5
        RoomSeatSync()

        for (i in 0 until numMax) {
            arrLeavers[i] = 0
            switchMark[i] = false
            switchCount[i] = 0
            cardArray[i].clear()
            cardKinds[i].clear()
        }

        switchDone = true
        switchCards.clear()
        cardBox.clear()
        cardBox2.clear()

        onedone = true
        firstCall = false
        timesPass = 0

        cardMen = 0
        seatWin = -1
        seatPoint = -1

        cardMount = LogicCdd.GetCardMount()
    }

    override fun RoundRestart() {
        if (isStart) return
        timeWait = 10
       // println("!!!### will restart in 10 second")
        val callable = java.lang.Runnable {
            RoundReset()
            Ai()
        }
        serviceScheduled.schedule(callable, timeWait.toLong(), TimeUnit.SECONDS)
    }

    val waittime = mTime + 1
    fun TimeOut_Pa() {
        if (!isStart) return
        if (!switchAi) return
        val callable = java.lang.Runnable {

            if (switchDone) return@Runnable
            for (i in 0 until numMax)
                if (switchMark[i]) continue
                // else DoPass(i)
                else noSwitch(i)    //注释掉了DoPass()，如有疑问，请联系 176823
            //println("############超时# 13秒 SwitchPASS")
            TimeOut_Da(seatPoint)
        }
        serviceScheduled.schedule(callable, 13L, TimeUnit.SECONDS)
    }

    fun TimeOut_Pa(seat: Int) {
        if (!isStart) return
        if (!switchAi) return
        val dotimebefore = timeDo
        val callable = java.lang.Runnable {

            if (timeDo != dotimebefore)
                return@Runnable
            if (seatPoint != seat)
                return@Runnable
            val cards = IntArray(1)
            cards[0] = cardArray[seat][0]
            DoPass(seat)
            //println("############超时# $seat 号" + waittime + "秒 PASS")
        }
        serviceScheduled.schedule(callable, waittime.toLong(), TimeUnit.SECONDS)
    }

    fun TimeOut_Da(seat: Int) {
        if (!isStart) return
        if (!switchAi) return
        val dotimebefore = timeDo
        val callable = java.lang.Runnable {
            if (timeDo != dotimebefore) return@Runnable
            if (seatPoint != seat) return@Runnable

            val cards = mutableListOf<Int>()
            //println(seat.toString() + " 还有谁   剩余牌 " + cardArray[seat])
            try {
                //println(seat.toString() + " TimeOut_Da 1467行 " + getMinCard(cardArray[seat].toIntArray()))
                DoDa(seat, getAllcard(cardArray[seat].toIntArray()))
            } catch (ex: Exception) {
               // println(ex.message)
                throw Exception(ex.message)
            }
            //println("######## $seat 号####超时# " + waittime + "秒 da出${cards[0]}")
        }
        serviceScheduled.schedule(callable, waittime.toLong(), TimeUnit.SECONDS)
    }


    override fun Ai() {
        for (i in 0 until numMax) {
            if (arrSeats[i] == 0) {
                val user = User(
                        uid = 1,
                        sex = 1,    //初始为1
                        avatar = Random().nextInt(9).toString(),
                        nick = Hall.listRobotName[Random().nextInt(34270)],
                        coin = Random().nextInt(5000).toLong()
                )
                OnUserSit(user)
            }
            OnUserReady(i)
        }
    }

    override fun TimeOut_DoAi() {
        if (isStart) return
        if (!switchAi) return
        timeWait = Random().nextInt(10) + 5
       // println("timeWait=$timeWait")
        val callable = java.lang.Runnable {
            Ai()
        }
        serviceScheduled.schedule(callable, timeWait.toLong(), TimeUnit.SECONDS)
    }

}