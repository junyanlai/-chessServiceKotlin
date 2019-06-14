package com.shine.controller.gamble.saima

import com.shine.amodel.Room
import com.shine.amodel.User
import com.shine.aservice.user.UserService
import com.shine.controller.aHall.Hall
import com.shine.controller.aHall.Hall.jPCoin
import com.shine.controller.aHall.RoomController
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 *  Create by Colin
 *  Date:2018/6/27.
 *  Time:9:55
 */
class TableSm constructor(override val rid: Int, override val di: Int, override var armyBoo: Int) : Room {

    override val pwd = ""
    override val mTime = 0
    override val creator = -1
    override val roundMax = 0

    override val numMax = 100
    override val numMin = 0
    override val type = "sm"
    override val timeCreate = System.currentTimeMillis()

    override var numCur = 0
    override var timeWait = 0
    override var roundCur = 0

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
    val service = Executors.newSingleThreadScheduledExecutor()
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

        val json = JSONObject()
        json.put("horseData", horseData)
        json.put("timelast", timelast)

        if (arrPlayers.contains(user)) {
            val seat = arrPlayers.indexOf(user)
            arrSeats[seat] = 1

            user.rid = rid


            SendData(all, Msg(1, numCur + 1, json, "sit", cmd))
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
                SendData(all, Msg(1, numCur + 1, json, "sit", cmd))

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
            for ((i, u) in arrPlayers.withIndex()) {
                if (arrSeats[i] < 1) continue
                u?.let {
                    if (u.uid == 1) return@let
                    agentRoom.Send(u.cid, msg)
                }
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
    override var cardMount = intArrayOf()

    fun Stake(seat: Int, cards: IntArray) {
        if (cards.size != 37) {
            SendData(seat, Msg(0, seat, "wrong stake", "stake", cmd))
            return
        }
        if (!canstake) {
            SendData(seat, Msg(0, seat, "wrong time", "stake", cmd))
            return
        }
        if (!stop) return
        val sum = cards.sum()

        arrPlayers[seat]?.dice = cards
    }

    val time0 = 30
    val time1 = 5
    val time2 = 20
    val time3 = 15
    val time = time0 + time1 + time2 + time3+8  //前端
    var timeNext = 0L

    var run = false
    var stop = false
    var canstake = false

    var m = -1
    var steps = 10
    val horse = IntArray(8)
    val horseData = IntArray(8)


    //val coinMap=IntArray(37)
    val multi0 = floatArrayOf(0f, 3.3f, 4.1f, 4.3f, 5.5f, 11f, 16f, 41f, 46f, 6f, 6.3f, 8.2f, 18f, 26f, 66f, 73f, 8.2f, 10f, 24f, 34f, 86f, 96f, 11f, 25f, 36f, 91f, 99f, 33f, 46f, 119f, 132f, 105f, 268f, 299f, 380f, 423f, 999f)
    val multi1 = floatArrayOf(0f, 3f, 3.6f, 5.7f, 7.2f, 10f, 17f, 24f, 33f, 4.6f, 7.8f, 9.9f, 15f, 24f, 34f, 48f, 9.4f, 11f, 18f, 29f, 41f, 57f, 20f, 31f, 50f, 70f, 98f, 39f, 64f, 90f, 125f, 99f, 138f, 192f, 225f, 313f, 438f)
    val multi2 = floatArrayOf(0f, 3.8f, 4.5f, 5f, 5.2f, 9.5f, 15f, 23f, 31f, 6.9f, 8.5f, 9.1f, 17f, 28f, 43f, 58f, 9.1f, 10f, 19f, 31f, 48f, 64f, 12f, 23f, 39f, 59f, 80f, 24f, 42f, 63f, 85f, 79f, 119f, 160f, 203f, 273f, 413f)
    val multi3 = floatArrayOf(0f, 3.1f, 3.9f, 5.2f, 6.2f, 9.9f, 14f, 27f, 33f, 5.7f, 7.7f, 9.3f, 15f, 22f, 44f, 52f, 9.6f, 11f, 19f, 25f, 54f, 65f, 15f, 26f, 37f, 74f, 89f, 31f, 45f, 89f, 107f, 75f, 148f, 178f, 214f, 258f, 510f)
    val multi4 = floatArrayOf(0f, 3.5f, 4.2f, 4.9f, 6.7f, 9.1f, 13f, 35f, 40f, 5.1f, 6.9f, 8.8f, 19f, 23f, 50f, 65f, 8.9f, 12f, 22f, 31f, 64f, 77f, 17f, 30f, 44f, 71f, 96f, 31f, 53f, 84f, 99f, 86f, 204f, 312f, 249f, 357f, 785f)
    val multMap = listOf<FloatArray>(multi0, multi1, multi2, multi3, multi4)

    var black = false
    val step = java.lang.Runnable {
        val flag = steps in 0..9
        if (!flag) return@Runnable

        for (i in 0..7) {

            horse[i] += random(-100, 250)
            for (i in 0..2) {
                if (horse[i] > 1150) horse[i] += random(-250, -150)
                if (horse[i] < -180) horse[i] += random(0, 600)
            }
        }
        steps++
        SendData(all, Msg(1, steps, horse.toList(), "run", cmd))

        if (steps == 3 && ra.nextInt(50) == 47) {
            //if (steps==3){
            black = true
            SendData(all, Msg(1, all, "black", "black", cmd))
        }

        if (steps == 10) {
            DataChange()
            RoundCount()
        }
    }


    override fun RoundStart() {
        val runnable = Runnable { start() }
        service.scheduleAtFixedRate(step, 0, 2, TimeUnit.SECONDS)
        serviceScheduled.scheduleAtFixedRate(runnable, 0, time.toLong(), TimeUnit.SECONDS);
    }

    fun start() {
        stop = false
        black = false
        for (i in 0..7) horse[i] = 0
        for (i in 0..7) horseData[i] = ra.nextInt(3)
        m = ra.nextInt(5)
        canstake = true
        SendData(all, Msg(1, m, horseData, "start", cmd))
        timeNext = System.currentTimeMillis() / 1000 + time.toLong()

        val callable = java.lang.Runnable { stop() }
        serviceScheduled.schedule(callable, time0.toLong(), TimeUnit.SECONDS)
    }

    fun stop() {
        stop = true
        SendData(all, Msg(1, all, "", "stop", cmd))

        val callable = java.lang.Runnable {

            canstake = false
            steps = 0
        }
        serviceScheduled.schedule(callable, time1.toLong(), TimeUnit.SECONDS)
    }

    val record = MutableList(8, { FloatArray(4) })
    val recordHorse = Array(80, { MutableList(5, { 0 }) })
    fun DataChange() {

        val result = Result(horse)
        val i1 = result[1]
        val i2 = result[2]
        val i22 = result[0]

        record.add(0, floatArrayOf(i1.toFloat(), i22.toFloat(), multMap[m][i1], multMap[m][i2]))
        record.removeAt(8)

        for (i in 0..7) {
            val index = i * 10 + horseData[i]
            if (i + 1 == i1) {
                recordHorse[index].add(0, 1)
                recordHorse[index].removeAt(5)
            } else {
                recordHorse[index].add(0, 0)
                recordHorse[index].removeAt(5)
            }
        }
    }

    override fun RoundCount() {
        val result = Result(horse)
        val i1 = result[1]
        val i2 = result[2]

        for ((i, u) in arrPlayers.withIndex()) {
            if (u == null) continue
            u.let {

                val dices = IntArray(37)
                it.dice?.let {
                    for (i in 1..36)
                        dices[i] = it[i]
                }

                val r1 = multMap[m][i1] * dices[i1]
                val r2 = multMap[m][i2] * dices[i2]
                val detail = JSONObject()

                detail.put("result", intArrayOf(0, i1, result[0]))
                detail.put("resultOne", floatArrayOf(r1, dices[i1].toFloat(), multMap[m][i1]))
                detail.put("resultAll", floatArrayOf(r2, dices[i2].toFloat(), multMap[m][i2]))
                detail.put("finalResult", floatArrayOf(r1 + r2, r1, r2))
                updateUserCoin(u, r1 + r2)
                SendData(i, Msg(1, i, detail, "multiples", cmd))
            }
        }
    }

    fun updateUserCoin(user: User, coin: Float) {
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
        Log()
        val detail = JSONObject()
        detail.put("m", m)
        detail.put("horse", horseData.toList())
        for (i in 0..7) {
            detail.put("record$i", record[i].toList())
            detail.put("recordhorse$i", recordHorse[i * 10 + horseData[i]])
        }
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

    val ra = Random()
    fun random(min: Int, max: Int) = ra.nextInt(max - min) + min
    fun Result(horse: IntArray): IntArray {

        val horses = horse.clone()
        var o1 = 0
        var o2 = 0

        var oo = -99999
        for (i in 0..7)
            if (horses[i] > oo) {
                oo = horses[i]
                o1 = i
            }

        oo = -99999
        horses[o1] = -99999
        for (i in 0..7)
            if (horses[i] > oo) {
                oo = horses[i]
                o2 = i
            }
        o1++
        o2++
        val o22 = (o1 * 15 + o2 * 2 - o1 * o1) / 2
        return intArrayOf(o2, o1, o22)
    }
}