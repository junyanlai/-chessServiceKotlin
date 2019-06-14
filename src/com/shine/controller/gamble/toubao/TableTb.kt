package com.shine.controller.gamble.toubao

import com.shine.amodel.Room
import com.shine.amodel.User
import com.shine.aservice.user.UserService
import com.shine.controller.aHall.Hall
import com.shine.controller.aHall.RoomController
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 *  Create by Colin
 *  Date:2018/6/25.
 *  Time:10:15
 */
class TableTb constructor(override val rid: Int, override val di: Int, override var armyBoo: Int) : Room {

    override val pwd = ""
    override val mTime = 0
    override val creator = -1
    override val roundMax = 0

    override val numMax = 100
    override val numMin = 0
    override val type = "tb"
    override val timeCreate = System.currentTimeMillis()

    override var numCur = 0
    override var timeWait = 0     //baozi
    override var roundCur = 0     //huangjin

    override var isStart = false
    override val arrSeats = IntArray(numMax)
    override val arrPlayers = arrayOfNulls<User>(numMax)
    override val arrLeavers = IntArray(numMax)

    override val mapRoom = Hall.mapRoom
    override val mapUserRoom = Hall.mapUserRoom
    override val mapHalfRoom = HashMap<Int, Room>()

    override val rand = Random()
    override var switchAi = false
    override val switchLog = false
    override val serviceScheduled = Executors.newSingleThreadScheduledExecutor()

    override val agentRoom = RoomController.RoomAgent
    override val all = 255
    override val cmd = "msg_" + type


    override fun HasUserSeat(user: User): Int {
        if (!arrPlayers.contains(user)) return -1
        else return arrPlayers.indexOf(user)
    }

    override fun OnClientClose(user: User) {
        val seat = HasUserSeat(user)
        OnUserLeave(seat)
    }

    override fun OnUserSit(user: User): Int {
        val timecur = System.currentTimeMillis() / 1000
        val timelast = timeNext - timecur
        RoomSeatSync()

        if (arrPlayers.contains(user)) {
            val seat = arrPlayers.indexOf(user)
            arrSeats[seat] = 1

            user.rid = rid
            SendData(all, Msg(1, numCur + 1, timelast, "sit", cmd))

            if (user.uid != 1) mapUserRoom.put(user.uid, this)
            numCur++

            return 1
        }

        if (numCur >= numMax) return 3
        for ((i, u) in arrPlayers.withIndex())
            if (u == null) {
                arrSeats[i] = 1
                arrPlayers[i] = user

                user.rid = rid
                SendData(all, Msg(1, numCur + 1, timelast, "sit", cmd))

                if (user.uid != 1) mapUserRoom.put(user.uid, this)
                numCur++

                return 1
            }

        return 4
    }

    override fun OnUserLeave(seat: Int) {
        arrPlayers[seat]?.rid = 0
        SendData(all, Msg(1, numCur - 1, 1, "leave", cmd))
        arrSeats[seat] = 0
        arrPlayers[seat]?.let {
            if (it.uid != 1) mapUserRoom.remove(it.uid)
        }
        arrPlayers[seat] = null
        numCur--
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
        println("#_________arrPlayer3=${arrPlayers[3]}")

        println("#_________mapRoom")
        println("#_________size=:${mapRoom.size}")
        println("#_________content=:${mapRoom}")
        println("#_________mapUserRoom")
        println("#_________size=:${mapUserRoom.size}")
        println("#_________content=:${mapUserRoom}")
    }

    override fun SendData(seat: Int, msg: String) {

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
    override var cardMount = intArrayOf(
            34, 2, 2, 201, 61, 31, 18, 13, 9, 8, 7, 7, 8, 9, 13, 18, 31, 61, 201, 0, 0, //0-20	0-33 1-min 2-max
            201, 201, 201, 201, 0, 2, 2, 2, 2, 2, 2,                                    //21-31	24-200	26.31-single
            6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6               //32-52
    )

    val time0 = 30
    val time1 = 5
    val time2 = 15
    val time = time0 + time1 + time2
    var timeNext = 0L


    var tou = IntArray(3)
    var canstake = false
    val falls = IntArray(53)
    var result = IntArray(53)

    var timesGold = 0L
    var timesBaozi = 0L
    val tous = MutableList(100, { IntArray(3) })
    val mapMax = IntArray(7)
    val mapMin = IntArray(7)
    var timesMax = 0
    var timesMin = 0

    var stakes = intArrayOf(
            1, 3, 5, 10, 30, 50, 100, 300, 500, 501
    )

    var stakess = intArrayOf(
            501,
            500, 500,
            300, 300, 300,
            100, 100, 100, 100,
            50, 50, 50, 50, 50, 50,
            30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
            10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,
            5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
            10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,
            50, 50, 50, 50, 50, 50,
            100, 100, 100, 100,
            300, 300, 300,
            501,
            500
    )

    fun Fall() {
        for (i in 0..52) falls[i] = 0
        val m = stakess[ra.nextInt(stakess.size)]
        val num = ra.nextInt(53)
        for (i in 0 until num)
            falls[ra.nextInt(53)] = m
    }

    fun Stake(seat: Int, cards: IntArray) {
        if (cards.size != 53) {
            SendData(seat, Msg(0, seat, "wrong stake", "onstake", cmd))
            return
        }
        if (!canstake) {
            SendData(seat, Msg(0, seat, "wrong time", "stake", cmd))
            return
        }
        SendData(all, Msg(1, seat, cards, "stake", cmd))
    }

    fun OnStake(seat: Int, cards: IntArray) {
        if (cards.size != 53) {
            SendData(seat, Msg(0, seat, "wrong stake", "onstake", cmd))
            return
        }
        if (!canstake) {
            SendData(seat, Msg(0, seat, "wrong time", "onstake", cmd))
            return
        }
        val sum = cards.sum()

        arrPlayers[seat]?.dice = cards
    }


    override fun RoundStart() {
        val runnable = Runnable {
            start()
        }
        serviceScheduled.scheduleAtFixedRate(runnable, 0, time.toLong(), TimeUnit.SECONDS);
    }

    fun start() {
        canstake = true
        SendData(all, Msg(1, all, "start stake", "start", cmd))
        timeNext = System.currentTimeMillis() / 1000 + time.toLong()

        val callable = java.lang.Runnable {
            stars()
        }
        serviceScheduled.schedule(callable, time0.toLong(), TimeUnit.SECONDS)
    }

    fun stars() {
        Fall()
        SendData(all, Msg(1, all, falls, "stars", cmd))
        val callable = java.lang.Runnable {
            kai()
        }
        serviceScheduled.schedule(callable, time1.toLong(), TimeUnit.SECONDS)
    }

    fun kai() {

        val gold = ra.nextInt(200)
        if (gold in 25..27) timesGold = 0
        else timesGold++

        canstake = false
        tou = intArrayOf(rand(), rand(), rand())
        if (gold == 25) {
            tou[0] = 7
        }

        //2018-12-27:AM 应前端开发陈** 要求注释掉以下代码，每次最多只能出现一个7
        //        if (gold == 26) {
        //            tou[0] = 7;tou[1] = 7
        //        }
        //        if (gold == 27) {
        //            tou[0] = 7;tou[1] = 7;tou[2] = 7
        //        }

        SendData(all, Msg(1, all, tou, "kai", cmd))

        if (tou[0] == tou[1] && tou[1] == tou[2]) timesBaozi = 0
        else timesBaozi++

        timeWait = timesBaozi.toInt()
        roundCur = timesGold.toInt()

        DataChange()
        RoundCount()
    }

    fun DataChange() {

        tous.add(0, tou)
        tous.removeAt(100)

        timesMax = 0
        timesMin = 0
        for (i in 1..6) {
            mapMax[i] = 0
            mapMin[i] = 0
        }

        for (tou in tous)
            if (tou.sum() in 3..10) {
                timesMin++
                for (i in 0..2) mapMin[tou[i]]++
            } else if (tou.sum() in 11..18) {
                timesMax++
                for (i in 0..2) mapMax[tou[i]]++
            }
    }

    override fun RoundCount() {

        result = Result(tou)

        val multF = IntArray(53)
        for (i in 0..52)
            multF[i] = result[i] * cardMount[i] * falls[i]

        for (i in 0 until numMax)
            arrPlayers[i]?.let {

                val dices = IntArray(53)
                it.dice?.let {
                    for (i in 0..52)
                        dices[i] = it[i]
                }

                val get = Results(dices, multF)

                updateUserCoin(it, get.toLong())

                SendData(i, Msg(1, i, get, "multiples", cmd))
            }
    }

    fun updateUserCoin(user: User, coin: Long) {
        val c = Hall.jPCoin(coin.toLong())//JP of System
        if (c > 0) {
            val user = UserService.getUserMsgByUID(user.uid)
            val r = UserService.updateUserCoin((user.coin + (coin - c)).toLong(), user.uid)
        } else {
            val user = UserService.getUserMsgByUID(user.uid)
            val r = UserService.updateUserCoin((user.coin + coin).toLong(), user.uid)
        }
    }


    override fun Status(seat: Int) {
        if (switchLog) Log()

        val touss = MutableList(0, { "" })
        for (i in 0..29)
            touss.add("${tous[i][0]},${tous[i][1]},${tous[i][2]}")

        val detail = JSONObject()
        detail.put("tous", touss)
        detail.put("timesMax", timesMax)
        detail.put("timesMin", timesMin)
        detail.put("mapMax", mapMax)
        detail.put("mapMin", mapMin)

        detail.put("timesGold", timesGold)
        detail.put("timesBaozi", timesBaozi)

        SendData(seat, Msg(1, seat, detail, "status", cmd))
    }


    override fun SeatNext(seat: Int) = if (seat == (numMax - 1)) 0 else (seat + 1)
    override fun SeatLast(seat: Int) = if (seat == 0) (numMax - 1) else (seat - 1)
    override fun RoomLeave() {}
    override fun RoomDelete() {}

    override fun GameFapai() {}
    override fun RoundEnd() {}
    override fun RoundReset() {}
    override fun RoundRestart() {}
    override fun OnUserReady(seat: Int) {}

    override fun Ai() {}
    override fun TimeOut_DoAi() {}

    //Logic
    val ra = Random()

    fun rand() = ra.nextInt(6) + 1
    val tou2 = arrayOf(
            intArrayOf(1, 1), intArrayOf(2, 2), intArrayOf(3, 3),
            intArrayOf(4, 4), intArrayOf(5, 5), intArrayOf(6, 6),
            intArrayOf(1, 2), intArrayOf(1, 3), intArrayOf(1, 4), intArrayOf(1, 5), intArrayOf(1, 6),
            intArrayOf(2, 3), intArrayOf(2, 4), intArrayOf(2, 5), intArrayOf(2, 6),
            intArrayOf(3, 4), intArrayOf(3, 5), intArrayOf(3, 6),
            intArrayOf(4, 5), intArrayOf(4, 6),
            intArrayOf(5, 6))

    fun baoZiTou(kaiTou: IntArray) = if (kaiTou[0] == kaiTou[1] && kaiTou[1] == kaiTou[2]) kaiTou[0] else 0

    fun Sort(kaitou: IntArray) {
        var temp = 0
        for (i in 0..1)
            for (j in 0 until 2 - i)
                if (kaitou[j] > kaitou[j + 1]) {
                    temp = kaitou[j]
                    kaitou[j] = kaitou[j + 1]
                    kaitou[j + 1] = temp
                }
    }

    fun hasItem(m: IntArray, n: IntArray): Boolean {
        Sort(m)
        if (m[0] == n[0] && m[1] == n[1]) return true
        if (m[0] == n[0] && m[2] == n[1]) return true
        if (m[1] == n[0] && m[2] == n[1]) return true else return false
    }

    fun Result(tou: IntArray): IntArray {
        val result = IntArray(53)
        val baozihead = baoZiTou(tou)
        val value = tou.sum()

        result[value] = 1                           //3-18
        if (value in 4..10) result[1] = 1           //min
        if (value in 11..17) result[2] = 1          //max

        if (baozihead != 0) {                       //baozi
            result[0] = 1
            result[19 + baozihead] = 1
        }
        result[25 + tou[0]] = 1                     //tou1
        result[25 + tou[1]] = 1
        result[25 + tou[2]] = 1

        for (i in 0..20)                      //tou2
            if (hasItem(tou, tou2[i]))              //System.out.println("contains:"+(32+i));
                result[32 + i] = 1
        return result
    }

    fun Results(cards: IntArray, mul: IntArray): Int {
        val r = IntArray(53)
        for (i in 0..52)
            r[i] = cards[i] * mul[i]
        return r.sum()
    }
}