package com.shine.controller.poker.nn

import com.shine.amodel.ArmyRanking
import com.shine.amodel.Room
import com.shine.amodel.User
import com.shine.aservice.army.ArmyAdminService
import com.shine.aservice.army.ArmyRankingService
import com.shine.aservice.currency.BonusCalculationService
import com.shine.aservice.user.UserService
import com.shine.controller.aHall.Hall
import com.shine.controller.aHall.RoomController
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 *  Create by Colin
 *  Date:2018/7/4.
 *  Time:9:56
 */
class TableNN constructor(override val rid: Int, override val creator: Int, override val di: Int, override var armyBoo: Int) : Room {

    override val pwd = ""
    override val roundMax = 100
    override val mTime = 2

    override val numMax = 5
    override val numMin = 2
    override val type = "nn"
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
    override val mapHalfRoom = RoomController.mapHalfRoom_nn

    override var switchAi = true
    override val switchLog = false
    override val serviceScheduled = Executors.newSingleThreadScheduledExecutor()

    override val agentRoom = RoomController.RoomAgent
    override val all = 255
    override val cmd = "msg_" + type
    override var cardMount = intArrayOf(
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D,
            0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D,
            0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D,
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3C, 0x3D
    )

    var numReady = 0
    val funStart = java.lang.Runnable { if (!isStart) RoundStart() }
    val timeReady = 6
    val timeReset = 6
    fun HoldClear() {
        for (i in 0..4) if (arrSeats[i] != 2) OnUserLeave(i)
    }

    fun saps(): Array<User> {
        val safeArr = Array(numMax, { RoomController.RoomUser })
        for (i in 0 until numMax)
            arrPlayers[i]?.let { safeArr[i] = it }
        return safeArr
    }

    override fun HasUserSeat(user: User) = arrPlayers.indexOf(user)
    override fun OnClientClose(user: User) = OnUserLeave(HasUserSeat(user))

    override fun OnUserSit(user: User): Int {
        if (isStart) return 2
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

        if (numCur == numMax) return 3
        for ((i, u) in arrPlayers.withIndex()) {
            if (u == null) {
                arrSeats[i] = 1
                arrPlayers[i] = user

                user.rid = rid
                SendData(all, Msg(1, i, saps(), "sit", cmd))

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

        if (isStart) {
            SendData(seat, Msg(0, seat, "started", "ready", cmd))
            return
        }

        arrSeats[seat] = 2
        SendData(all, Msg(1, seat, arrSeats, "ready", cmd))

        if (arrSeats.filter { it == 2 }.size >= 2) {
            SendData(all, Msg(1, seat, arrSeats, "willsratrt", cmd))
            serviceScheduled.schedule(funStart, timeReady.toLong(), TimeUnit.SECONDS)
        }

    }

    override fun RoomLeave() {
        var noLiveOne = true
        for (status in arrSeats)
            if (status > 0)
                noLiveOne = false

        //println("##### in leave =${n
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

        println("#_________arrSeats={[${arrSeats[0]}],[${arrSeats[1]}],[${arrSeats[2]}],[${arrSeats[3]}]}")
        println("#_________arrPlayer0=${arrPlayers[0]}")
        println("#_________arrPlayer1=${arrPlayers[1]}")
        println("#_________arrPlayer2=${arrPlayers[2]}")
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

    override fun SeatNext(seat: Int) = if (seat == (numMax - 1)) 0 else (seat + 1)
    override fun SeatLast(seat: Int) = if (seat == 0) (numMax - 1) else (seat - 1)

    override fun Status(seat: Int) {
        Log()
    }

    override fun RoundStart() {
        isStart = true
        roundCur++

        GameFapai()
    }

    override fun RoundCount() {
        //get finalMult
        val cs = Array(5, { IntArray(5) })
        for (i in 0..4) for (j in 0..4) cs[i][j] = cardStart[i][j]

        for (i in 0..4)
            for (j in 0..4)
                cs[i][j] = VV(cs[i][j])

        for (i in 0 until arrSeats.size) {
            if (arrSeats[i] == 2) {
                if (cs[i].sum() % 10 == 0) finalMult[i] = 10
                else finalMult[i] = cs[i].sum() % 10

                var wuhua = true
                for (j in 0..4)
                    if (V(cardStart[i][j]) < 11) {
                        wuhua = false
                        break
                    }
                if (wuhua) finalMult[i] = 12
            }
        }
        //getFinalResult
        val listLive = MutableList(0, { 0 })
        for (i in 0..4) if (arrSeats[i] == 2) listLive.add(i)

        for (i in listLive) {
            var seatWin = -1
            var seatLose = -1
            if (i == zhuang) continue
            if (finalMult[i] > finalMult[zhuang]) {
                seatWin = i
                seatLose = zhuang
            } else {
                seatWin = zhuang
                seatLose = i
            }

            val coin = markBet[seatLose] * finalMult[seatWin]

            finalResult[seatWin] += coin
            finalResult[seatLose] -= coin
        }
    }

    override fun RoundEnd() {

        if (!isStart) return
        canBet = false
        isStart = false

        for (i in 0..4)
            if (arrSeats[i] == 2 && markBet[i] == 0)
                markBet[i] = di / 100

        RoundCount()
        val detail = JSONObject()
        detail.put("result", finalResult)
        for (i in 0..4) {
            if (arrSeats[i] == 2) {
                detail.put("cards$i", cardStart[i])
            }
        }
        //army by war gameOver count
        if (armyBoo == 1) {
            //通知玩家返回军团战场景
            SendData(all, Msg(1, 1, "", "armyWar", "hall_armyWar"))

            armyScore(finalResult)
            SendData(all, Msg(1, all, detail, "end", cmd))
        } else {
            updateUserCoin(finalResult)
            SendData(all, Msg(1, all, detail, "end", cmd))
        }

        TOT(funReset, timeReset)
    }


    fun updateUserCoin(meansChange: IntArray) {
        for ((i, v) in arrPlayers.withIndex()) {
            if (v != null) {
                if (v.uid == 1) continue
//                var agent = Agent()
//                agent.UID = v.uid
                //积分
                var num = mutableMapOf<String, Int>()
                num["exp"] = 0

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


    override fun RoundReset() {

        RoomSeatSync()
        zhuanged = false
        zhuang = -1
        numBet = 0
        numQiang = 0
        numReady = 0

        for (i in 0..4) {
            markBet[i] = 0
            markQiang[i] = false
            finalMult[i] == 0
            finalResult[i] == 0
            if (arrSeats[i] == 2)
                arrSeats[i] = 1
        }

        //clear robot and sync seats
        for ((i, user) in arrPlayers.withIndex()) {
            if (user?.uid == 1) OnUserLeave(i)
            if (user == null) {
                arrSeats[i] = 0
                continue
            }
        }
        TOT(funRestart, 1)
    }


    override fun RoundRestart() {
        //自动准备
        arrPlayers.forEach {
            if (it != null) {
                arrSeats[arrPlayers.indexOf(it)] = 2
            }
        }
        //初始化结算
        for (i in 0..4) {
            finalResult[i] = 0
            finalMult[i] = 0
        }

        if (arrSeats.filter { it == 2 }.count() >= 2) {
            serviceScheduled.schedule(funStart, timeReady.toLong(), TimeUnit.SECONDS)
        } else {
            TimeOut_DoAi()
        }
    }


    override fun GameFapai() {
        RoomSeatSync()
        MountSlice()
        val cardStart2 = Array(5, { IntArray(5) })
        for (i in 0..4) for (j in 0..4) cardStart2[i][j] = cardStart[i][j]

        for (i in 0..4) cardStart2[i][4] = 0
        for (i in 0 until arrSeats.size) {
            if (arrSeats[i] == 2) {
                SendData(i, Msg(1, i, cardStart2[i], "fapai", cmd))
            }
        }
        canQiang = true
        TOT(funZhuang, 8)
    }

    //====
    var canQiang = false
    var canBet = false

    var zhuanged = false
    var zhuang = -1
    val markBet = IntArray(5)
    val markQiang = BooleanArray(5)
    var numBet = 0
    var numQiang = 0

    val finalMult = IntArray(5)
    val finalResult = IntArray(5)

    fun OnQiang(seat: Int) {
        if (!canQiang) {
            SendData(seat, Msg(0, seat, "wrong time", "qiang", cmd))
            return
        }
        if (markQiang[seat]) {
            SendData(seat, Msg(0, seat, "qianged", "qiang", cmd))
            return
        }

        markQiang[seat] = true
        numQiang++
        SendData(all, Msg(1, seat, "success", "qiang", cmd))
        if (numQiang == numCur) TOT(funZhuang, 0)
    }

    val funZhuang = java.lang.Runnable {
        if (zhuanged) return@Runnable
        canBet = true
        zhuanged = true
        canQiang = false
        val listSrc = MutableList(0, { -1 })
        val listQiang = MutableList(0, { -1 })
        for (i in 0..4) if (markQiang[i]) listQiang.add(i)
        for (i in 0..4) if (arrSeats[i] == 2) listSrc.add(i)


        if (listQiang.size == 1) {
            zhuang = listQiang[0]
        } else if (listQiang.size > 1) {
            zhuang = listQiang[rand.nextInt(listQiang.size)]
        } else if (listQiang.size == 0) {
            zhuang = listSrc[rand.nextInt(listSrc.size)]
        }

        SendData(all, Msg(1, all, zhuang, "zhuang", cmd))

        for (i in 0..4) {
            if (arrSeats[i] == 2) {
                SendData(i, Msg(1, all, cardStart[i][4], "fapai2", cmd))
            }
        }

        TOT(funEnd, 8)
    }

    fun OnBet(seat: Int, beat: Int) {
        if (!canBet) {
            SendData(seat, Msg(0, seat, "wrong time", "bet", cmd))
            return
        }
        if (markBet[seat] != 0) {
            SendData(seat, Msg(0, seat, "beted", "bet", cmd))
            return
        }
        markBet[seat] = beat
        numBet++
        SendData(seat, Msg(1, seat, beat, "bet", cmd))
        if (numBet == numCur) TOT(funEnd, 0)
    }

    val funEnd = java.lang.Runnable { RoundEnd() }

    val funReset = java.lang.Runnable { RoundReset() }
    val funRestart = java.lang.Runnable { RoundRestart() }


    fun TOT(function: Runnable, time: Int) = serviceScheduled.schedule(function, time.toLong(), TimeUnit.SECONDS)

    override fun Ai() {
        if (arrSeats.filter { it == 1 }.count() == 1 || arrSeats.filter { it == 2 }.count() == 1) {
            val listSrc = MutableList(0, { -1 })

            for (i in 0 until 2) {
                if (arrSeats[i] == 0) {
                    val user = User(
                            uid = 1,
                            sex = 1,    //初始为1
                            avatar = Random().nextInt(9).toString(),
                            nick = Hall.listRobotName[Random().nextInt(34270)],
                            coin = Random().nextInt(5000).toLong()
                    )
                    listSrc.add(i)
                    OnUserSit(user)
                    OnUserReady(i)
                }
            }
        }
    }

    override fun TimeOut_DoAi() {
        if (isStart) return
        if (!switchAi) return
        timeWait = Random().nextInt(8) + 5
        val callable = java.lang.Runnable { Ai() }
        serviceScheduled.schedule(callable, timeWait.toLong(), TimeUnit.SECONDS)
    }

    //Logic Part
    val cardStart = Array<IntArray>(5, { IntArray(5) })

    fun MountSlice() {
        val cm = cardMount.toMutableList()
        cm.shuffle()
        for (i in 0..4)
            for (j in 0..4)
                cardStart[i][j] = cm[i * 5 + j]
    }

    fun V(card: Int) = card and 0x0F
    fun VV(card: Int) = if (V(card) > 10) 10 else V(card)
}