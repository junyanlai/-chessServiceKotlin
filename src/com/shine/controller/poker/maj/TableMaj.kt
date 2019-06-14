package com.shine.controller.poker.maj

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
import com.shine.controller.aHall.Hall.jsonArrayTomap
import com.shine.controller.aHall.Hall.listRobotName
import com.shine.controller.aHall.RoomController
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class TableMaj constructor(override val rid: Int, override val creator: Int, override val pwd: String,
                           override val di: Int, val tai: Int, override val roundMax: Int,
                           override val mTime: Int, val mTing: Boolean, val mZimo: Boolean, val mBao: Boolean, val mMen: Boolean, override var armyBoo: Int) : Room {

    override val numMax = 4
    override val numMin = 4
    override val type = "maj"
    override val timeCreate = System.currentTimeMillis()

    override var numCur = 0
    override var timeWait = 0
    override var roundCur = 0

    //armyBoo: Int // 0:非军团战 1：军团战
    override var isStart = false
    override val arrSeats = IntArray(numMax)            //-1- leaved, 0-safeNull, 1-sit, 2-ready, 3-
    override val arrPlayers = arrayOfNulls<User>(numMax)
    override val arrLeavers = IntArray(numMax)

    override val mapRoom = Hall.mapRoom
    override val mapUserRoom = Hall.mapUserRoom
    override val mapHalfRoom = RoomController.mapHalfRoom_maj

    override val switchLog = false
    override val agentRoom = RoomController.RoomAgent
    override val all = 255
    override val cmd = "msg_" + type

    //...
    override fun HasUserSeat(user: User): Int {
        if (!arrPlayers.contains(user)) return -1
        else return arrPlayers.indexOf(user)
    }

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
                   // println("i=$i")
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

/*
        println("#_________cardMount=${cardMount.toList()}")
        println("#_________cardArr0=${cardArray[0].toList()}")
        println("#_________cardArr1=${cardArray[1].toList()}")
        println("#_________cardArr2=${cardArray[2].toList()}")
        println("#_________cardArr3=${cardArray[3].toList()}")
*/
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


    //=====LogicMaj Part
    override var cardMount = LogicMaj.GetCardMount()

    var seatPoint = -1
    var seatBanker = -1
    val passMap = mapOf("command" to "msg_maj", "data" to "pass", "card" to "0", "detail" to "0")

    var cardMo = 0x00
    var cardDa = 0x00
    var cardHu = 0x00
    var seatHu = -1
    var zimo = false

    var numDo = 0
    var numMount = 79
    val seatDo = IntArray(3)

    var timeDa = 0
    var timeDo = 0

    val doValue = IntArray(3)
    val doStatus = IntArray(3)
    val doJsons = Array(3, { JSONObject(passMap) })
    val tingStatus = BooleanArray(4)
    val cardStatus = Array(4) { BooleanArray(5) }

    val cardStart = Array(4) { IntArray(24) }
    val cardArray = Array(4) { IntArray(22) }
    val cardFlower = Array(4) { IntArray(8) }
    val cardBox = Array(4) { MutableList<Int>(0, { 0 }) }
    val cardAngang = Array(4) { MutableList<Int>(0, { 0 }) }

    override fun SeatNext(seat: Int) = if (seat == (numMax - 1)) 0 else (seat + 1)
    override fun SeatLast(seat: Int) = if (seat == 0) (numMax - 1) else (seat - 1)
    fun SeatBanker() = if (seatBanker == -1) 0 else SeatNext(seatBanker)

    override fun GameFapai() {
        LogicMaj.MountSlice(cardMount, cardStart, cardArray, cardFlower)
        for (i in 0..3) {
            val msgFapai = JSONObject()
            msgFapai.put("seatBnaker", seatBanker)
            msgFapai.put("cardStart", cardStart[i])
            msgFapai.put("cardArray", cardArray[i])
            msgFapai.put("cardFlower0", cardFlower[0])
            msgFapai.put("cardFlower1", cardFlower[1])
            msgFapai.put("cardFlower2", cardFlower[2])
            msgFapai.put("cardFlower3", cardFlower[3])
            SendData(i, Msg(1, i, msgFapai, "fapai", cmd))
        }
    }

    fun DoMo(seat: Int) {

        cardMo = cardMount[0]
        numMount = LogicMaj.ChangeMo(cardMount)
        if (switchLog) println("[cardMo$cardMo]")
        if (numMount == 14) {
            SendData(all, Msg(1, seat, "liu", "liu", cmd))
            if (armyBoo == 1) {
                //通知玩家返回军团战场景
                SendData(all, Msg(1, 1, "", "armyWar", "hall_armyWar"))
            }
            RoundEnd()
            return
        }

        if (cardMo > 0x40) {
            for (i in 0..7)
                if (cardFlower[seat][i] == 0x00) {
                    cardFlower[seat][i] = cardMo
                    break
                }
            SendData(all, Msg(numMount, seat, cardMo, "hua", cmd))
            if (switchLog) println("[hua$cardMo]")
            DoMo(seat)
            return
        }

        val il = LogicMaj.IndexLive(cardArray[seat])
        cardArray[seat][il + 1] = cardMo
        SendData(seat + 4, Msg(numMount, seat, cardMo, "mo", cmd))

        if (tingStatus[seat]) {
            if (!LogicMaj.HuLegal(cardArray[seat])) {
                DoDa(seat, cardMo)
                return
            }
            DoHu(seat)
            return
        }
        TimeOut_Da(seat, cardMo)
    }

    fun DoDa(seat: Int, card: Int) {

        if (seat != seatPoint) {
            //println("[wrong]此时seatpoint=$seatPoint")
            SendData(seat, Msg(0, seat, "wrong order", "da", cmd))
            return
        }
        if (!LogicMaj.LegalDa(card, cardArray[seat])) {
            SendData(seat, Msg(0, seat, "wrong card", "da", cmd))
            return
        }

        timeDa++
        LogicMaj.ChangeDa(card, cardArray[seat])
        SendData(all, Msg(1, seat, card, "da", cmd))

        cardDa = card

        seatPoint = SeatNext(seat)
        seatDo[0] = seatPoint
        seatDo[1] = SeatNext(seatDo[0])
        seatDo[2] = SeatNext(seatDo[1])

        numDo = StatusSync()
        if (numDo != 0 && switchLog) {
            println("有冲突，数量：$numDo ")
            println("status0:[${cardStatus[0][0]},${cardStatus[0][1]},${cardStatus[0][2]},${cardStatus[0][3]},${cardStatus[0][4]}]")
            println("status1:[${cardStatus[1][0]},${cardStatus[1][1]},${cardStatus[1][2]},${cardStatus[1][3]},${cardStatus[1][4]}]")
            println("status2:[${cardStatus[2][0]},${cardStatus[2][1]},${cardStatus[2][2]},${cardStatus[2][3]},${cardStatus[2][4]}]")
            println("status3:[${cardStatus[3][0]},${cardStatus[3][1]},${cardStatus[3][2]},${cardStatus[3][3]},${cardStatus[3][4]}]")
        }
        if (numDo == 0) {

            cardBox[seat].add(card)
            DoMo(seatPoint)
            //println("直接发牌给$seatPoint 号")
            StatusClear()
            return
        }
    }

    fun StatusSync(): Int {

        for (i in 0..3)
            for (j in 0..4)
                cardStatus[i][j] = false

        var numDo = 0
        for (i in 0..3) {

            if (i == SeatLast(seatPoint)) continue
            cardStatus[i][1] = LogicMaj.LegalHu(cardDa, cardArray[i])
            cardStatus[i][2] = LogicMaj.LegalPeng(cardDa, cardArray[i])
            cardStatus[i][3] = LogicMaj.LegalGang(cardDa, cardArray[i])
            cardStatus[i][0] = cardStatus[i][1] || cardStatus[i][2] || cardStatus[i][3]

            if (i == seatPoint) {
                cardStatus[i][4] = LogicMaj.LegalCh(cardDa, cardArray[i])
                cardStatus[i][0] = cardStatus[i][0] || cardStatus[i][4]
            }

            if (cardStatus[i][0]) numDo++
        }
        if (numDo > 0)    //overtime pass
            for (i in 0..3)
                if (cardStatus[i][0])
                    TimeOut_Pa(i)

        return numDo
    }

    fun StatusClear() {

        cardDa = 0x00
        for (i in 0..3)
            for (j in 0..4)
                cardStatus[i][j] = false

        for (i in 0..2) {
            doValue[i] = 0
            doStatus[i] = 0
            doJsons[i] = JSONObject(passMap)
        }
    }

    fun Deal(seat: Int, root: JSONObject) {

        val data = root["data"].toString()
        val card = root["card"].toString().toInt()
        val detail = root["detail"].toString().toInt()

        if (numDo == 1 && cardStatus[seatPoint][0]) {

            if (seat != seatPoint) {
                SendData(seat, Msg(0, seat, "wrong order", data, cmd))
                return
            }

            when (data) {
                "chi" -> DoChi(seat, detail)
                "peng" -> DoPeng(seat)
                "gang" -> DoGang(seat)
                "hu" -> DoHu(seat)
                "pass" -> {
                    cardBox[SeatLast(seat)].add(cardDa)
                    DoMo(seat)
                }
            }

            StatusClear()
            return
        }

        for (i in 0..2)
            if (seat == seatDo[i] && cardStatus[seat][0]) {
                doStatus[i] = 1
                doValue[i] = LogicMaj.commandTurn(data)
                doJsons[i] = root
            }

        val numDid = doStatus[0] + doStatus[1] + doStatus[2]
        if (switchLog) println("$seat 想$data 需等 $numDo 步  已有：$numDid 步")

        //haven't do enough
        if (numDid < numDo) {

            var seatMax = -1
            var doMax: JSONObject

            outter@
            for (j in 1..4)
                for (i in 0..2)
                    if (cardStatus[seatDo[i]][j]) {
                        seatMax = seatDo[i]
                        doMax = doJsons[i]
                        break@outter
                    }

            if (switchLog) println("haven't do enough,seat max=$seatMax")

            //not the max
            if (seat != seatMax) {
                if (data == "pass")
                    for (j in 1..4) cardStatus[seat][j] = false

                SendData(seat, Msg(1, seat, 0, "hold", cmd))
                return
            }

            //is the max
            when (data) {
                "chi" -> {
                    DoChi(seat, detail)
                    error("the max 居然吃")
                }
                "peng" -> DoPeng(seat)
                "gang" -> DoGang(seat)
                "hu" -> DoHu(seat)
                "pass" -> {
                    for (j in 1..4) cardStatus[seat][j] = false
                    SendData(seat, Msg(1, seat, 0, "hold", cmd))
                    return
                }
            }

            StatusClear()
            return
        }

        if (numDid == 0) return

        /*if (numDid==1 && numDo==1){
            if (switchLog) println("非吃一步_command=$data")
            when(data){
                "peng"      ->DoPeng(seat,card)
                "gang"      ->DoGang(seat,card)
                "hu"        ->DoHu(seat,card)
                "pass"      ->{
                    for (j in 0..4) cardStatus[seat][j] = false
                    SendData(seat,Msg(1,seat,0,"pass",cmd))
                }
            }
            StatusClear()
            return
        }*/

        val finalIndex = LogicMaj.getMaxSeat(doValue)
        val finalSeat = seatDo[finalIndex]
        val finalJson = doJsons[finalIndex]

        //println("final_seat=$finalSeat  json=$finalJson")

        val finalData = finalJson["data"].toString()
        val finalCard = finalJson["card"].toString().toInt()
        val finalDetail = finalJson["detail"].toString().toInt()

        when (finalData) {
            "chi" -> DoChi(finalSeat, finalDetail)
            "peng" -> DoPeng(finalSeat)
            "gang" -> DoGang(finalSeat)
            "hu" -> DoHu(finalSeat)
            "pass" -> {
                cardBox[SeatLast(finalSeat)].add(cardDa)
                DoMo(finalSeat)
            }
        }

        StatusClear()
    }

    fun DoHu(seat: Int) {

        if (mTing && !tingStatus[seat]) {
            SendData(seat, Msg(0, seat, "must ting", "hu", cmd))
            return
        }

        if (mZimo) {
            SendData(seat, Msg(0, seat, "must zimo", "hu", cmd))
            return
        }

        if (!cardStatus[seat][1]) {
            SendData(seat, Msg(0, seat, "wrong option", "hu", cmd))
            return
        }
        if (switchLog) println("HU：seat=$seat")

        cardHu = cardDa
        seatHu = seat
        // LogicMaj.ChangeHu(cardHu, intArrayOf(1, 1, 1, 2, 3, 4, 5, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9))
        LogicMaj.ChangeHu(cardHu, cardArray[seat])
        SendData(all, Msg(1, seat, cardHu, "hu", cmd))
        RoundCount()
        RoundEnd()
    }


    fun DoZimo(seat: Int) {

        if (mTing && !tingStatus[seat]) {
            SendData(seat, Msg(0, seat, "must ting", "zimo", cmd))
            return
        }

        if (seat != seatPoint) {
            SendData(seat, Msg(0, seat, "wrong order", "zimo", cmd))
            return
        }
        if (!LogicMaj.HuLegal(cardArray[seat])) {
            SendData(seat, Msg(0, seat, "wrong zimo", "zimo", cmd))
            return
        }

        zimo = true
        cardHu = cardMo
        seatHu = seat
        SendData(all, Msg(1, seat, cardMo, "zimo", cmd))
        if (switchLog) println("ZIMO：seat=$seat")
        RoundCount()
        RoundEnd()
    }

    fun DoChi(seat: Int, wei: Int) {

        if (!cardStatus[seat][4]) {
            SendData(seat, Msg(0, seat, "wrong option", "chi", cmd))
            return
        }
        if (seat != seatPoint) {
            SendData(seat, Msg(0, seat, "wrong order", "chi", cmd))
            return
        }

        timeDo++
        LogicMaj.ChangeChi(cardDa, cardArray[seat], wei)
        val detail = cardDa.toString() + "_" + wei
        SendData(all, Msg(1, seat, detail, "chi", cmd))
        TimeOut_Doa(seat)

        if (switchLog)
            println("DODODO_chi:$seat chi   $cardDa ___card= ${LogicMaj.pla(cardArray[seat])}")
    }

    fun DoPeng(seat: Int) {

        if (!cardStatus[seat][2]) {
            SendData(seat, Msg(0, seat, "wrong option", "peng", cmd))
            return
        }
        timeDo++
        LogicMaj.ChangePeng(cardDa, cardArray[seat])
        SendData(all, Msg(1, seat, cardDa, "peng", cmd))
        seatPoint = seat

        TimeOut_Doa(seat)

        if (switchLog)
            println("DODODO_peng:$seat peng  $cardDa ___card= ${LogicMaj.pla(cardArray[seat])}")
    }

    fun DoGang(seat: Int) {
        if (!cardStatus[seat][2]) {
            SendData(seat, Msg(0, seat, "wrong option", "gang", cmd))
            return
        }
        timeDo++
        LogicMaj.ChangeGang(cardDa, cardArray[seat])
        SendData(all, Msg(1, seat, cardDa, "gang", cmd))
        seatPoint = seat

        DoMo(seat)
        if (switchLog)
            println("DODODO_gang:$seat gang  $cardDa ___card= ${LogicMaj.pla(cardArray[seat])}")
    }

    fun DoAngang(seat: Int, card: Int) {
        if (seat != seatPoint) {
            SendData(seat, Msg(0, seat, "wrong order", "angang", cmd))
            return
        }
        if (!LogicMaj.LegalAngang(card, cardArray[seat])) {
            SendData(seat, Msg(0, seat, "wrong angang", "angang", cmd))
            return
        }
        LogicMaj.ChangeAngang(card, cardArray[seat])
        cardAngang[seat].add(card)
        SendData(seat + 4, Msg(1, seat, card, "angang", cmd))
        DoMo(seat)
        if (switchLog) println("ANGANG：seat=$seat")
    }

    fun DoJiagang(seat: Int, card: Int) {
        if (seat != seatPoint) {
            SendData(seat, Msg(0, seat, "wrong order", "jiagang", cmd))
            return
        }
        if (!LogicMaj.LegalJiagang(card, cardArray[seat])) {
            SendData(seat, Msg(0, seat, "wrong jiagang", "jiagang", cmd))
            return
        }
        LogicMaj.ChangeJiagang(card, cardArray[seat])
        SendData(all, Msg(1, seat, card, "jiagang", cmd))
        DoMo(seat)
        if (switchLog) println("JIAGANG：seat=$seat")
    }

    fun DoTing(seat: Int, card: Int) {
        if (seat != seatPoint) {
            SendData(seat, Msg(0, seat, "wrong order", "ting", cmd))
            return
        }
        if (!LogicMaj.LegalTing(card, cardArray[seat])) {
            SendData(seat, Msg(0, seat, "can't ting", "ting", cmd))
            return
        }
        tingStatus[seat] = true
        SendData(seat, Msg(1, seat, "success", "ting", cmd))
        DoDa(seat, card)
    }

    override fun Status(seat: Int) {
        val status = JSONObject()
        status.put("numMount", numMount)
        status.put("cardHand" + seat, cardArray[seat])
        status.put("cardAngang" + seat, cardAngang[seat])
        for (i in 0..3) {
            if (i == seat) continue
            val cardClone = cardArray[i].clone()
            val il = LogicMaj.IndexLive(cardClone)
            for (i in 0..il) {
                if (il == 22) break
                cardClone[i] = -1
            }
            status.put("cardArray" + i, cardClone[seat])
        }
        for (i in 0..3) {
            status.put("cardBox" + i, cardBox[i])
            status.put("cardFlower" + i, cardFlower[i])
        }

        SendData(seat, Msg(cardArray[seat], seatPoint, status, "status", cmd))
    }

    fun saps(): Array<User> {     //safe arr players

        val safeArr = Array<User>(numMax, { RoomController.RoomUser })
        for (i in 0 until numMax)
            arrPlayers[i]?.let { safeArr[i] = it }
        return safeArr
    }

    override fun RoundStart() {

        isStart = true
        roundCur++

        seatBanker = SeatBanker()
        seatPoint = seatBanker
        GameFapai()
        DoMo(seatPoint)
    }

    override fun RoundCount() {

        val m = Multiple()
        val taiTypes = m.main(cardArray[seatHu], cardFlower[seatHu])
        if (zimo) taiTypes.add("zimo")

        val numTai = LogicMaj.getTai(taiTypes)
        val meansChange = IntArray(4)
        val jpArray = jPRandom(meansChange.size)  //jp
        val detail = JSONObject()

        detail.put("roundCur", roundCur)
        detail.put("numTai", numTai)
        detail.put("taiTypes", taiTypes)
        detail.put("meansChange", meansChange)
        for (i in 0..3) {
            detail.put("cardFlower" + i, cardFlower[i])
            detail.put("cardHand" + i, cardArray[i])
        }

        //army by war gameOver count
        if (armyBoo == 1) {
            armyScore(meansChange)
            SendData(all, Msg(1, seatHu, detail, "multiples", cmd))
            //通知玩家返回军团战场景
            SendData(all, Msg(1, 1, "", "armyWar", "hall_armyWar"))
        } else {
            updateUserCoin(meansChange, jpArray)
            SendData(all, Msg(1, seatHu, detail, "multiples", cmd))
            SendData(all, Msg(0, 1, jpArray, "jp", cmd))
        }
        /*
        //独听，一发
        //抢杠/花杠
        //海底捞月，河底捞鱼
        //门清/自摸/门清自摸/不求人/全求人/
        //连庄/拉庄/宝牌
        */
    }

    fun updateUserCoin(meansChange: IntArray, jpArray: JSONArray) {
        var map = HashMap<Int, Long>()
        if (jpArray.length() > 0) {
            map = jsonArrayTomap(jpArray)
        }

        for ((i, v) in arrPlayers.withIndex()) {
            if (v != null) {
                if (v.uid == 1) continue

//                var agent = Agent()
//                agent.UID = v.uid
//                agent.CID = v.cid
//                agent.user = v

                //积分
                var num = mutableMapOf<String, Int>()
                num["exp"] = 0

                if (map.containsKey(i)) {
                    val jpCoin = map.get(i) as Long
                    val user = UserService.getUserMsgByUID(v.uid)
                    val r = UserService.updateUserCoin(user.coin + meansChange[i] + jpCoin, user.uid)
                    //jp公告
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

    override fun RoundReset() {

        timeWait = Random().nextInt(10) + 5
        RoomSeatSync()
        StatusClear()

        cardMo = 0x00
        cardDa = 0x00
        cardHu = 0x00
        seatHu = -1
        zimo = false

        numDo = 0
        numMount = 79

        for (i in 0..2) {
            seatDo[i] = 0
            doValue[i] = 0
            doStatus[i] = 0
            doJsons[i] = JSONObject(passMap)
        }

        for (i in 0 until numMax) {
            arrLeavers[i] = 0
            tingStatus[i] = false

            for (k in 0..4)
                cardStatus[i][k] = false
            for (k in 0..23)
                cardStart[i][k] = 0
            for (k in 0..21)
                cardArray[i][k] = 0
            for (k in 0..7)
                cardFlower[i][k] = 0

            cardBox[i].clear()
            cardAngang[i].clear()
        }

        for (i in 0..143)
            cardMount[i] = 0

        cardMount = LogicMaj.GetCardMount()

    }

    override fun RoundRestart() {

        if (isStart) return
        timeWait = 10
        println("!!!### will restart in 10 second")
        val callable = java.lang.Runnable {
            RoundReset()
            Ai()
        }
        serviceScheduled.schedule(callable, timeWait.toLong(), TimeUnit.SECONDS)
    }


    override var switchAi = true
    val waittime = mTime + 1
    override val rand = Random()
    override val serviceScheduled = Executors.newSingleThreadScheduledExecutor()
    override fun Ai() {
        for (i in 0 until numMax) {
            if (arrSeats[i] == 0) {
                val user = User(
                        uid = 1,
                        sex = 1,    //初始为1
                        avatar = Random().nextInt(9).toString(),
                        nick = listRobotName[Random().nextInt(34270)])
                OnUserSit(user)
            }
            OnUserReady(i)
        }
    }

    fun TimeOut_Da(seat: Int, moCard: Int) {
        if (!isStart) return
        if (!switchAi) return
        val datimebefore = timeDa
        val callable = java.lang.Runnable {
            if (timeDa == datimebefore) {
                DoDa(seat, moCard)
                println("$$$$$$$$$$$$ 超时# " + waittime + "秒 #代打一张：" + moCard)
            }
        }
        serviceScheduled.schedule(callable, waittime.toLong(), TimeUnit.SECONDS)
    }

    fun TimeOut_Pa(seat: Int) {
        if (!isStart) return
        if (!switchAi) return
        val dotimebefore = timeDo
        val callable = java.lang.Runnable {
            if (timeDo == dotimebefore) {
                Deal(seat, JSONObject(passMap))
                println("############超时# " + waittime + "秒 PASS")
            }
        }
        serviceScheduled.schedule(callable, waittime.toLong(), TimeUnit.SECONDS)
    }

    fun TimeOut_Doa(seat: Int) {
        if (!isStart) return
        if (!switchAi) return
        val datimebefore = timeDa
        val callable = java.lang.Runnable {
            if (timeDa == datimebefore) {
                val daCard = cardArray[seat][0]
                DoDa(seat, daCard)
                println("$$$$$$$$$$$$ 超时# " + waittime + "秒 #代打一张：" + daCard)
            }
        }
        serviceScheduled.schedule(callable, waittime.toLong(), TimeUnit.SECONDS)
    }

    override fun TimeOut_DoAi() {

        if (isStart) return
        if (!switchAi) return
        timeWait = Random().nextInt(15) + 5
        val callable = java.lang.Runnable {
            Ai()
        }
        serviceScheduled.schedule(callable, timeWait.toLong(), TimeUnit.SECONDS)
    }
}