package com.shine.controller.poker.ddz


import com.shine.agent.Agent
import com.shine.amodel.ArmyRanking
import com.shine.amodel.Room
import com.shine.amodel.User
import com.shine.aservice.army.ArmyAdminService
import com.shine.aservice.army.ArmyRankingService
import com.shine.aservice.currency.BonusCalculationService
import com.shine.aservice.user.UserService
import com.shine.controller.aHall.Hall
import com.shine.controller.aHall.RoomController
import com.shine.controller.poker.Landlords.LogicDdz
import com.shine.controller.poker.Landlords.ManagerDdz
import com.shine.controller.poker.Landlords.cardTypeDdz
import com.shine.controller.poker.Landlords.tool.JSONTool
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 *  Create by Colin
 *  Date:2018/6/8.
 *  Time:16:16
 */
class TableDdz constructor(override val rid: Int, override val creator: Int, override val pwd: String,
                           override val di: Int, override val roundMax: Int, override val mTime: Int, override var armyBoo: Int) : Room {

    override val numMax = 3
    override val numMin = 3
    override val type = "ddz"
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
    val mapUserLeaveRom = Hall.mapUserLeaveRom
    override val mapHalfRoom = RoomController.mapHalfRoom_dzz

    override val rand = Random()
    override var switchAi = true
    override val switchLog = true
    override val serviceScheduled = Executors.newSingleThreadScheduledExecutor()

    override val agentRoom = RoomController.RoomAgent
    override val all = 255
    override val cmd = "msg_" + type
    var thisAgent: MutableMap<Int, Agent> = HashMap()
    var dizhuover = true
    var isMingCard: MutableMap<Int, Boolean> = HashMap()
    var jiabeiDone = false
    var jiabeiFirst = false
    val jiabeiMark = BooleanArray(numMax)
    var jiabeiPoint = 0
    var jiabeiSeat = 0
    var jiabeiMul = -1
    var allPermission = mutableMapOf<Int, List<Int>>()  //房间卡牌
    //叫地主
    var fist = 0
    var isDiz = mutableListOf<Int>()

    var haveMing = 5
    var beishu = 0
    var userMap = mutableMapOf<Int, User>()
    var isDizhuSeat: Int = 0
    var timeDo = 0
    var seatWin = -1
    var seatPoint = -1
    val cardArray = Array(numMax) { MutableList(0, { 0 }) }
    var card = Card()

    class Card() {
        var seat: Int = 5
        var card = mutableListOf<Int>()
    }

    //...
    fun saps(): Array<User> {
        val safeArr = Array(numMax, { RoomController.RoomUser })
        for (i in 0 until numMax)
            arrPlayers[i]?.let { safeArr[i] = it }
        return safeArr
    }

    fun thisAgent(agent: Agent, seat: Int) {
        thisAgent[seat] = agent
    }

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
                arrSeats[seat] = 2;
                mapHalfRoom.remove(arrPlayers[seat]?.uid)
                mapUserLeaveRom.remove(arrPlayers[seat]?.uid)
            } else {
                arrSeats[seat] = 1
            }
            user.rid = rid
            SendData(all, Msg(1, seat, saps(), "sit", cmd))
            if (user.uid != 1) mapUserRoom.put(user.uid, this)
            numCur++
            return 1
        }

        RoomSeatSync() //  初始化座位号

        if (isStart) return 2
        if (numCur == numMax) return 3

        for ((i, u) in arrPlayers.withIndex()) {
            if (u == null) {
                arrSeats[i] = 1
                arrPlayers[i] = user
                user.rid = rid
                SendData(all, Msg(1, i, saps(), "sit", cmd))
                OnUserReady(i)
                userMap.put(i, user)
                if (user.uid != 1) mapUserRoom.put(user.uid, this)
                numCur++
                return 1
            }
        }
        return 4
    }

    fun innerMap(agent: Agent, seat: Int) {
        SendData(all, Msg(1, seat, "levelRoom", "tuichu", "msg_ddz"))
        arrSeats[seat]=-1
        RoomController.mapUserLeaveRom[agent.user.uid]=this
    }

    override fun OnUserLeave(seat: Int) {
        if (isStart) {
            SendData(all, Msg(1, seat, 2, "leave", cmd))
            arrSeats[seat] = -1
            arrPlayers[seat]?.let {
                if (it.uid != 1) mapHalfRoom.put(it.uid, this)
                if (it.uid != 1) mapUserLeaveRom.put(it.uid, this)
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
        if (isStart) return      //just unstart can start
        arrSeats[seat] = 2
        SendData(all, Msg(1, seat, arrSeats, "ready", cmd))
        var allReady = true
        for (s in arrSeats) {
            if (s != 2) {
                allReady = false
            }
        }
        if (allReady) {
            RoundStart()
        }
    }


    //
    override fun RoomLeave() {
        var noLiveOne = true
        for (status in arrSeats)
            if (status > 0)
                noLiveOne = false

        println("##### in leave =${noLiveOne}")
        if (noLiveOne)
            RoomDelete()
    }

    override fun RoomDelete() {
        for (u in arrPlayers) {
            if (u == null) continue
            if (u.uid == 1) continue
            mapUserRoom.remove(u.uid)
            mapHalfRoom.remove(u.uid)
            mapUserLeaveRom.remove(u.uid)
            u.rid = 0
        }
        for (uid in arrLeavers) {
            if (uid == 0) continue
            if (uid == 1) continue
            mapHalfRoom.remove(uid)
            mapUserLeaveRom.remove(uid)
        }
        mapRoom.remove(rid)
        switchAi = false
        serviceScheduled.shutdown()
        serviceScheduled.shutdownNow()
        allPermission.clear()
        if (switchLog) println("#_________ Room is deleted")
    }

    /**
     * 打牌
     */
    fun playHand(root: JSONObject) {
//        println("出牌的环境 ：" + root["detail"])
        var seat = root["seat"] as Int
        var a = allPermission[seat]!!.toMutableList()                                      //取自己所有的牌
        var thisCard = JSONTool.toList(JSONTool.toJson(root["card"]).toString(), Int::class.java)!!  //转换出牌
//        println("出牌人<>" + seat + "<>下个人<>" + getNextUser(seat) + "<>上家id<>：" + card.seat + "<>上家出牌<>" + card.card + "<>当前玩家出牌<>" + thisCard)
        if (!thisCard.isEmpty() && !a.containsAll(thisCard)) return
        if (LogicDdz.jugdeType(thisCard) == cardTypeDdz.c4||LogicDdz.jugdeType(thisCard) == cardTypeDdz.c22) {
            beishu * 2
        }
        a.removeAll(thisCard)
        if (seat != seatPoint) {
            SendData(seat, Msg(0, seat, "wrong order", "da", cmd))
            return
        }
        if (thisCard.isEmpty()) {
//            println("打牌 pass 了")
            SendData(all, Msg(1, seat, "pass", "pass", cmd))
        } else {
//            println("打牌了")
            card.card = thisCard
            card.seat = seat
            SendData(all, Msg(1, seat, thisCard, "da", cmd)) //打牌 告诉所有人
        }
        allPermission.set(seat, a)
        if (a.size == 0) {
            seatWin = seat
            for (i in arrPlayers) {
                if (i!!.uid != 1) {
                    mapHalfRoom.remove(i!!.uid)
                    mapUserLeaveRom.remove(i!!.uid)
                }
            }
            RoundCount()
            RoundEnd()
            return
        }
        seatPoint = getNextUser(seat)
        TimeOut_Da(getNextUser(seat))
        getTime()  //统计执行时间
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
        println("#_________mapHalfRoom")
        println("#_________size=:${mapHalfRoom.size}")
        println("#_________content=:${mapHalfRoom}")
        println()
    }

    override fun SendData(seat: Int, msg: String) {
//        println("要发送给"+seat+"的信息"+"玩家有"+JSONTool.toJson(arrPlayers))
        if (seat > (numMax - 1) && seat < all) {
            val si = seat - numMax
            if (arrSeats[si] < 1) return
            arrPlayers[si]?.let {
                agentRoom.Send(it.cid, msg)
//                println("发给一个人 "+si+"发送用户的频道id：："+it.cid)
            }
            val json = JSONObject(msg)
            json.put("detail", "hide")
            for (i in 0 until numMax) {
                if (i == si) continue
                arrPlayers[i]?.let {
                    agentRoom.Send(it.cid, json.toString())
//                    println("发给个人的"+i+"发送用户的频道id：："+it.cid)
                }
            }
            return
        }
        if (seat == all) {
            for ((i, status) in arrSeats.withIndex()) {
//                println(i.toString()+"发给所有人的 0"+status)
                if (status < 1) continue
//                println(i.toString()+"发给所有人的 1"+status)
                if (arrPlayers[i] == null) continue
//                println(i.toString()+"发给所有人的 2 "+status)
                if (arrPlayers[i]!!.uid == 1) continue            //robot
//                println(i.toString()+"发给所有人的 3 "+status)
                arrPlayers[i]?.let {
                    agentRoom.Send(it.cid, msg)
//                    println("发给所有人的 "+i+"发送用户的频道id：："+it.cid)
                }
            }
            return
        }
        if (arrSeats[seat] < 1)
            return
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
    override var cardMount = LogicDzz.GetCardMount()

    override fun SeatNext(seat: Int) = if (seat == (numMax - 1)) 0 else (seat + 1)
    override fun SeatLast(seat: Int) = if (seat == 0) (numMax - 1) else (seat - 1)


    var isdi = 5
    var isFist = true
    fun getNextUser(seat: Int): Int {
        when (seat) {
            0 -> {
                return 1
            }
            1 -> {
                return 2
            }
            2 -> {
                return 0
            }
        }
        return seat
    }

    /**
     * 发牌了
     */
    override fun GameFapai() {
        if (!isStart) {
            return
        }
        allPermission.clear()
//        println("开始发牌了")
        for (i in 0 until numMax) {
            SendData(i, Msg(1, i, getPorkers(i), "fapai", cmd))
        }
        TimeOut_Start()
    }

    fun getPorkers(key: Int): List<Int>? {
        if (!allPermission.isEmpty()) {
            return allPermission.get(key)
        }
        var creadList = LogicDdz.disorganizePokers().toMutableList()
        for (j in 0..2) {
            var cards = mutableListOf<Int>()
            for (i in 0..16) {            //取出牌组存入临时集合
                cards.add(creadList.get(0))
                creadList.removeAt(0) //删除牌栈
            }
            var card = mutableListOf<Int>()
            LogicDdz.cardSort(card, cards, 1)         //递归排序卡牌
            allPermission.put(j, card)        //存到map中

        }
        allPermission.put(4, creadList)
        return allPermission.get(key)!!.toList()
    }

    fun jiaodizhu(root: JSONObject) {
        if (!dizhuover) return
        var seat = root["seat"] as Int
        var qiang = root["dizhu"] as Boolean
        if (qiang && !isDiz.contains(seat)) isDiz.add(seat)
        if (qiang && dizhuover) {
            SendData(all, Msg(1, seat, "qiang", "jiaodizhu", cmd))
        } else {
            SendData(all, Msg(1, seat, "buqiang", "jiaodizhu", cmd))
        }
//        println(seat.toString() + "抢地主了" + qiang + " 下一个人是：" + getNextUser(seat))
        if (fist == 0) {
            fist++
            if (qiang) beishu = di * 2
            tellAllOrOne(seat, qiang)
            return
        }
        if (fist == 1) {
            fist++
            if (qiang) beishu = di * 2
            tellAllOrOne(seat, qiang)
            return
        }
        if (fist == 2) {
            when {
                qiang && isDiz.size == 1 -> {
                    fist++
                    beishu = di * 2
                    seatPoint = isDiz[0]
                    GameGetDizhu(isDiz[0])
                    return
                }
                qiang && isDiz.size > 1 -> {
                    fist++
                    beishu = di * 2
                    SendData(all, Msg(1, isDiz[0], "start jiaodizhu", "jiaodizhu", cmd))
//                    tellAllOrOne(isDiz[0],qiang)
                    //超时 不要
                    seatPoint = isDiz[0]
                    TimeOut_Pa(isDiz[0])
                    getTime()
                    return
                }
            }
            when {
                !qiang && isDiz.size == 2 && !isDiz.contains(seat) -> {
                    fist++
                    SendData(all, Msg(1, isDiz[0], "start jiaodizhu", "jiaodizhu", cmd))
//                    tellAllOrOne(isDiz[0],qiang)
                    seatPoint = isDiz[0]
                    TimeOut_Pa(isDiz[0])
                    getTime()
                    return
                }
                !qiang && isDiz.size == 1 -> {
                    fist++
//                    println("恭喜 >" + isDiz[0] + "< 成为地主")
                    beishu = di * 2
                    seatPoint = isDiz[0]
                    GameGetDizhu(isDiz[0])
                    return
                }
                !qiang && isDiz.size == 0 -> {
                    isFist = false
                    GameFapai()
                    return
                }
            }
        }
        if (fist == 3 && qiang && isDiz.size > 1) {
            fist++
//            println("dizhu:" + isDiz[0])
            beishu = di * 2
            seatPoint = isDiz[0]
            GameGetDizhu(isDiz[0])
            return
        } else if (fist == 3 && !qiang && isDiz.size > 2) {
//            println("dizhu:" + isDiz[1])
            beishu = di * 2
            seatPoint = isDiz[1]
            GameGetDizhu(isDiz[1])
            return
        }
    }

    /**
     * 通知
     */
    fun tellAllOrOne(seat: Int, isDi: Boolean) {
        SendData(all, Msg(1, getNextUser(seat), "start jiaodizhu", "jiaodizhu", cmd))
        seatPoint = getNextUser(seat)
        TimeOut_Pa(getNextUser(seat))
        getTime()
    }

    /**
     * 地主牌
     */
    fun GameGetDizhu(seat: Int) {
        isDizhuSeat = seat
        if (dizhuover) {
            var dic = getPorkers(4)!!
            SendData(all, Msg(1, seat, dic, "dipai", cmd))
            var dizhu = allPermission[seat]!!.toMutableList()
            dizhu.addAll(dic)
            allPermission[seat] = dizhu
        }
        dizhuover = false
        seatPoint = seat
        TimeOut_Da(seat)
        getTime()
    }

    fun GameMing(seat: Int) {
//        println("明牌 ：" + seat)
        if (haveMing == 5) {
            haveMing = seat
            isdi=seat
//            SendData(all, Msg(1, haveMing, "start jiaodizhu", "jiaodizhu", cmd))
        }
        isMingCard[seat] = true
        beishu = di * 2
        var dizhu = allPermission[seat]!!.toMutableList()
        SendData(all, Msg(1, seat, dizhu, "mingpai", cmd))
    }
    fun Start_Jiaodizhu(seat: Int) {
//        println("开始叫地主 ：" + seat)
        if (haveMing == 5) {
            haveMing = seat
            isdi=seat
            SendData(all, Msg(1, haveMing, "start jiaodizhu", "jiaodizhu", cmd))
        }else{
            SendData(all, Msg(1, isdi, "start jiaodizhu", "jiaodizhu", cmd))
        }
    }
    fun DoJiabei(seat: Int, user: User) {
        //如果金币数量不足加倍要求  返回0 不允许加倍
        if (user.coin < 1000) {
            SendData(seat, Msg(0, seat, "jiabeiNo", "jiabei", cmd))
        } else {
            beishu = di * 2
            SendData(all, Msg(1, seat, "jiabeiOk", "jiabei", cmd))
        }
    }

    fun DoPass(seat: Int) {
        //叫地主 进行
//        println(seat.toString() + "pass")
        if (dizhuover) {
            SendData(all, Msg(1, seat, "pass", "pass", cmd))
            SendData(all, Msg(1, getNextUser(seat), "start jiaodizhu", "jiaodizhu", cmd))
            return
        } else {
            SendData(all, Msg(1, seat, "pass", "pass", cmd))
            seatPoint = getNextUser(seat)
            TimeOut_Da(getNextUser(seat))
            getTime()
        }
    }

    //AI打牌
    fun DoDa(seat: Int) {
        if (dizhuover) return
        var a = allPermission[seat]!!.toMutableList()
//        println("AI代打人所有牌面》》》" + a)
        var thisCard = mutableListOf<Int>()
        if (card.seat != seat && card.seat != 5 && !card.card.isEmpty()) {
//            println("AI帮忙打牌 座位号:" + seat)
            for ((i, v) in getAllCarMap(a.toIntArray())) {
                if (i > LogicDdz.getcardTypeDdz(card.card[0])) {
                    if (card.card.size == v.size && !v.isEmpty()) {
                        thisCard.addAll(v)
                        break
                    }
                    if (card.card.size < v.size && !v.isEmpty()) {
                        for (i in 0..card.card.size - 1) {
                            thisCard.add(v[i])
                        }
                        break
                    }
                } else if (LogicDdz.jugdeType(card.card.toMutableList()) != cardTypeDdz.c4
                        && v.size == 4 && !v.isEmpty()) {
                    thisCard.addAll(v)
                    break
                }
                if (thisCard.size == card.card.size) {
                    break
                }
            }
        } else {
            thisCard.add(a[0])
        }
//        println("判断完成后出的牌：" + JSONTool.toJson(thisCard))
        if (thisCard == null || !allPermission[seat]!!.toMutableList().containsAll(thisCard)|| (LogicDdz.jugdeType(card.card.toMutableList())==cardTypeDdz.c22&&card.seat != seat && card.seat != 5 && !card.card.isEmpty())) {
            DoPass(seat)
        } else {
            var root = JSONObject()
            root.put("seat", seat)
            root.put("card", thisCard)
            root.put("detail", 2)
            playHand(root)
            thisCard.clear()
        }
    }

    fun getAllCarMap(myCard: IntArray): MutableMap<Int, List<Int>> {
        var typeMap = mutableMapOf<Int, List<Int>>()
        for (i in 1..15) {
            var ca = mutableListOf<Int>()
            for (j in myCard) {
                if (LogicDdz.getcardTypeDdz(j) == i) {
                    ca.add(j)
                }
            }
            typeMap[i] = ca
        }
        return typeMap
    }

    override fun Status(seat: Int) {
        Log()
        val cardNums = IntArray(numMax)
        for (i in 0 until numMax)
            cardNums[i] = cardArray[i].size
//        println("SSS:seatPoint=$seatPoint")
        SendData(seat, Msg(cardArray[seat], seatPoint, cardNums, "status", cmd))
    }

    override fun RoundStart() {
        isStart = true
        jiabeiDone = false
        roundCur++
        GameFapai()

    }

    fun addCoin(user: User, coin: Int) {
        if (user.uid == 1) return
        BonusCalculationService.bonusCalculation(coin,3,user)
    }

    fun rmCoin(user: User, coin: Int) {
        if (user.uid == 1) return
       BonusCalculationService.bonusCalculation(-coin,-1,user)
    }


    /**
     * 结算
     */
    override fun RoundCount() {
        isStart = false
        dizhuover = true
        val detail = JSONObject()
        if (seatWin == isDizhuSeat) {
//            println("地主赢拉:" + seatWin)
            detail.put("cardHand" + seatWin, String.format("%s%s", "+", beishu * 2))
            detail.put("cardHand" + getNextUser(seatWin), String.format("%s%s", "-", beishu))
            detail.put("cardHand" + getNextUser(getNextUser(seatWin)), String.format("%s%s", "-", beishu))
            if (armyBoo == 1) {
                //通知玩家返回军团战场景
                SendData(all, Msg(1, 1, "", "armyWar", "hall_armyWar"))
                var intAray = IntArray(3)
                intAray[seatWin] = beishu * 2
                intAray[getNextUser(seatWin)] = -beishu
                intAray[getNextUser(getNextUser(seatWin))] = -beishu
                armyScore(intAray)
            } else {
                addCoin(arrPlayers[seatWin]!!, beishu * 2)
                rmCoin(arrPlayers[getNextUser(seatWin)]!!, beishu)
                rmCoin(arrPlayers[getNextUser(getNextUser(seatWin))]!!, beishu)
            }
        } else {
//            println("夭寿了  农民赢了" + seatWin)
            detail.put("cardHand" + isDizhuSeat, String.format("%s%s", "-", beishu * 2))
            detail.put("cardHand" + getNextUser(isDizhuSeat), String.format("%s%s", "+", beishu))
            detail.put("cardHand" + getNextUser(getNextUser(isDizhuSeat)), String.format("%s%s", "+", beishu))
            if (armyBoo == 1) {
                //通知玩家返回军团战场景
                SendData(all, Msg(1, 1, "", "armyWar", "hall_armyWar"))
                var intAray = IntArray(3)
                intAray[isDizhuSeat] = -(beishu * 2)
                intAray[getNextUser(isDizhuSeat)] = beishu
                intAray[getNextUser(getNextUser(isDizhuSeat))] = beishu
                armyScore(intAray)
            } else {
                rmCoin(arrPlayers[isDizhuSeat]!!, beishu * 2)
                addCoin(arrPlayers[getNextUser(isDizhuSeat)]!!, beishu)
                addCoin(arrPlayers[getNextUser(getNextUser(isDizhuSeat))]!!, beishu)
            }
        }
        detail.put("roundCur", roundCur)
//        println("地主是 :" + isDizhuSeat)
        detail.put("beishu", beishu)
        detail.put("di", di)
        SendData(all, Msg(1, seatWin, detail, "multiples", cmd))
        jpSettlement()
        allPermission.clear()
        for (i in arrSeats) {
            arrSeats[i] = 1
        }
        return
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

    //jp 结算
    fun jpSettlement() {
        var jpPeopleArray = Hall.jPRandom(3)
        SendData(all, Msg(1, 1, jpPeopleArray, "jp", cmd))
        if (jpPeopleArray != null) {
            for (i in jpPeopleArray) {
                var maps = JSONTool.toObj(i.toString(), Map::class.java)!!
                var i = maps["index"] as Int
                var v = maps["coin"] as Int
                var user = arrPlayers[i]!!
                if (user.uid == 1) continue
                var thisUser = UserService.getUserMsgByUID(user.uid)
                UserService.updateUserCoin(thisUser.coin + v.toLong(), user.uid)
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

        for (i in 0 until numMax) {

        }

    }

    override fun RoundRestart() {
        if (isStart) return
        timeWait = 10
//        println("!!!### will restart in 10 second")
        val callable = java.lang.Runnable {
            RoundReset()
            Ai()
        }
        serviceScheduled.schedule(callable, timeWait.toLong(), TimeUnit.SECONDS)
    }


    val waittime = mTime + 1
    //pass  是叫地主pass
    fun TimeOut_Pa() {
        val callable = java.lang.Runnable {
//            println("3秒后开始叫地主")
        }
        serviceScheduled.schedule(callable, 3L, TimeUnit.SECONDS)
    }
    fun TimeOut_Start(){
        val callable = java.lang.Runnable {
//            println("3秒后开始叫地主")
            var seat = Random().nextInt(3)
            Start_Jiaodizhu(seat)
            seatPoint = isdi
            TimeOut_Pa(isdi)
            getTime()
        }
        serviceScheduled.schedule(callable, 3L, TimeUnit.SECONDS)
    }
    //pass
    fun TimeOut_Pa(seat: Int) {
        if (!isStart) return
        val dotimebefore = timeDo
        val callable = java.lang.Runnable {
            if (timeDo != dotimebefore)
                return@Runnable
            if (seatPoint != seat) return@Runnable
            if (userMap[seat]!!.uid == 1) {
                var root = JSONObject()
                root.put("seat", seat)
                root.put("dizhu", true)
                jiaodizhu(root)
            } else {
                var root = JSONObject()
                root.put("seat", seat)
                root.put("dizhu", false)
                jiaodizhu(root)
            }
//            println(seat.toString() + "############超时# " + waittime + "秒 PASS" + userMap[seat]!!.uid)
        }
        serviceScheduled.schedule(callable, waittime.toLong(), TimeUnit.SECONDS)
    }

    fun TimeOut_Da(seat: Int) {
        if (!isStart) return
        val dotimebefore = timeDo
        val callable = java.lang.Runnable {
            if (timeDo != dotimebefore) return@Runnable
            if (seatPoint != seat) return@Runnable
            DoDa(seat)
//            println("############超时# " + waittime + "秒 da")
        }
        serviceScheduled.schedule(callable, waittime.toLong(), TimeUnit.SECONDS)
    }

    var thisTime = 0L
    //统计执行时间
    fun getTime() {
        var time = waittime.toLong()
        while (time > 0) {
            try {
                Thread.sleep(1000)
                time = time - 1
                thisTime = time
//                println(time)
            } catch (ex: Exception) {
//                println(ex.message)
            }
        }
        return
    }

    override fun Ai() {
        for (i in 0 until numMax) {
            if (arrSeats[i] == 0) {
                val user = User(
                        uid = 1,
                        avatar = Random().nextInt(9).toString(),
                        coin = Random().nextInt(99999).toLong(),
                        signCount = 0,
                        signTimes = 0,
                        expFashion = 0L,
                        medal0 = 0,
                        sex=1,
                        gem = 500,
                        nick = Hall.listRobotName[Random().nextInt(34270)])
                OnUserSit(user)
            }
            OnUserReady(i)
        }
    }

    override fun TimeOut_DoAi() {
        if (isStart) return
        if (!switchAi) return
        timeWait = Random().nextInt(10) + 5
//        println("timeWait=$timeWait")
        val callable = java.lang.Runnable {
            Ai()
        }
        serviceScheduled.schedule(callable, timeWait.toLong(), TimeUnit.SECONDS)
    }

    fun TimeOut_NewCreatRoom(agent: Agent, root: JSONObject, seat: Int) {
        if (isStart) return
//        timeWait= Random().nextInt(10)+5
//        println("timeWait=$timeWait")
        val callable = java.lang.Runnable {
            OnUserLeave(seat)
            ManagerDdz.OnCreate(agent, root)
        }
        serviceScheduled.schedule(callable, 30.toLong(), TimeUnit.SECONDS)
    }
}