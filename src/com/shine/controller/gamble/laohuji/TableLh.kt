package com.shine.controller.poker.xcsl


import com.shine.agent.Agent
import com.shine.amodel.Room
import com.shine.amodel.User
import com.shine.aservice.gamble.GambleService
import com.shine.aservice.user.UserService.getUserMsgByUID
import com.shine.aservice.user.UserService.updateUserCoin
import com.shine.controller.aHall.Hall
import com.shine.controller.aHall.RoomController
import com.shine.controller.gamble.laohuji.util.MultipleRate.getRate
import com.shine.controller.gamble.laohuji.util.MultipleRate.lineOFour
import com.shine.controller.gamble.laohuji.util.MultipleRate.lineOfone
import com.shine.controller.gamble.laohuji.util.MultipleRate.lineOfthree
import com.shine.controller.gamble.laohuji.util.MultipleRate.lineOftwo

import org.json.JSONObject

import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class TableLh constructor(override val rid: Int, override val creator: Int, override val pwd: String,
                          override val di: Int, override val roundMax: Int,
                          override val mTime: Int, override var armyBoo: Int) : Room {

    override val numMax = 4
    override val numMin = 4
    override val type = "lhj"
    override val timeCreate = System.currentTimeMillis()

    override var numCur = 0
    override var timeWait = 0
    override var roundCur = 0       //用来记录免费的次数

    override var isStart = false
    override val arrSeats = IntArray(numMax)            //-1- leaved, 0-safeNull, 1-sit, 2-ready, 3-
    override val arrPlayers = arrayOfNulls<User>(numMax)
    override val arrLeavers = IntArray(numMax)

    override val mapRoom = Hall.mapRoom
    override val mapUserRoom = Hall.mapUserRoom
    override val mapHalfRoom = RoomController.mapHalfRoom_ssz        //拉霸游戏不需要这个属性


    override val switchLog = true
    override var switchAi = true
    override val agentRoom = RoomController.RoomAgent
    override val all = 255
    override val cmd = "msg_" + type

    override val rand = Random()
    override var cardMount = IntArray(52)
    override val serviceScheduled = Executors.newSingleThreadScheduledExecutor()
    val RP = 25
    var array = Array(4) { IntArray(5) }

    fun statrNew(agent: Agent, stake: Long) {
        var freeGame = ArrayList<Int>()

        //X2 和 x3 出现的次数
        var x2 = 0.0
        var x3 = 0.0

        if (roundCur > 0) {             //免费游戏模式赋值
            for (i in 0 until 4) {
                for (j in 0 until 5) {
                    if (j == 0 || j == 4) { //招财进宝 不能出现在第一列
                        array[i][j] = probabilityRP(Random().nextInt(11), 11)
                    } else {
                        array[i][j] = probabilityRP(Random().nextInt(14), 14)
                    }
                    freeGame.add(array[i][j])
                }
            }
        } else {                     //正常游戏模式赋值
            for (i in 0 until 4) {
                for (j in 0 until 5) {
                    if (j == 0 || j == 4) {  //招财进宝 不能出现在第一列
                        array[i][j] = probabilityRP(Random().nextInt(11), 11)
                    } else {
                        array[i][j] = probabilityRP(Random().nextInt(12), 12)
                    }
                    freeGame.add(array[i][j])
                }
            }
        }

        //计算免费游戏次数
        when (freeGame.count { it == 11 }) {
            2 -> {
                if (roundCur != 0) {
                    roundCur = roundCur + 5
                }
            }
            3 -> {
                if (roundCur == 0) {
                    roundCur = 8
                } else {
                    roundCur = roundCur + 8
                }
            }
            4 -> {
                if (roundCur == 0) {
                    roundCur = 15
                } else {
                    roundCur = roundCur + 15
                }
            }
            5 -> {
                if (roundCur == 0) {
                    roundCur = 25
                } else {
                    roundCur = roundCur + 20
                }
            }
        }

        //计算金币
        var winCoin: Double = 0.0
        //线
        var lineStr = StringBuffer()

        for (i in 0 until 4) {
            var str = ArrayList<String>()

            //坐标集合
            var list = judge(array[i][0])

            //计算x2和x3出现的次数
            list.forEach {
                it.forEach {
                    if (array[it.first][it.second] == 12) x2 = x2 + 1
                    if (array[it.first][it.second] == 13) x3 = x3 + 1
                }
            }


            var value = list.size + 1

            when (list.size) {
                1 -> {
                    str = lineOfone(list, i)
                    winCoin = winCoin + (getRate(array[i][0], value) * stake)
                }
                2 -> {
                    str = lineOftwo(list, i)
                    winCoin = winCoin + (getRate(array[i][0], value) * stake)
                }
                3 -> {
                    str = lineOfthree(list, i)
                    winCoin = winCoin + (getRate(array[i][0], value) * stake)
                }
                4 -> {
                    str = lineOFour(list, i)
                    winCoin = winCoin + (getRate(array[i][0], value) * stake)
                }
            }
            str.forEach {
                lineStr.append("${it}#")
            }
        }


        val coin = getUserMsgByUID(agent.user.uid).coin

        //计算X2 or X3倍数-免费游戏模式
        if (roundCur > 0) {
            x2 = Math.pow(2.0, x2)
            x3 = Math.pow(3.0, x3)
            if (x2 != 0.0) winCoin = winCoin * x2
            if (x3 != 0.0) winCoin = winCoin * x3
        }

        //更新筹码
        if (winCoin > stake) {
            var r = updateUserCoin(coin + (winCoin.toLong() - stake), agent.user.uid)
        } else {
            var r = updateUserCoin(coin + (stake - winCoin.toLong()), agent.user.uid)
        }

        var arrayToFormat = StringBuilder()
        for (i in 0 until 4) {
            for (j in 0 until 5) {
                if (j == 4) {
                    arrayToFormat.append("""${array[i][j]}_""")
                } else {
                    arrayToFormat.append("""${array[i][j]},""")
                }

            }
        }


        var json = JSONObject()
        json.put("card", arrayToFormat.substring(0, arrayToFormat.length - 1))

        if (lineStr.length > 1) {
            json.put("coordinate", lineStr.substring(0, lineStr.length - 1))
        } else {
            json.put("coordinate", "")
        }

        json.put("freeCount", roundCur)
        json.put("coin", winCoin.toLong())

        //抽水
        if (winCoin > 0) {
            Hall.jPCoin(winCoin.toLong())//JP of System
        }
        //更新免费游戏次数
        if (roundCur != 0) roundCur = roundCur - 1

        Send(agent, Msg(1, 1, json, "startNew", cmd))


        //更新数据库中对应的机器的参数
        val robot = GambleService.queryRid(rid)
        if (winCoin > 0) {
            robot.total = robot.total + 1
            robot.win = robot.win + 1
            //赢得比率
            robot.rate = robot.win.rem(robot.total.toDouble())
            GambleService.updateXcslData(robot)

        } else {
            robot.total = robot.total + 1
            GambleService.updateXcslData(robot)
        }
    }


    //记录重复的坐标
    fun judge(c: Int): ArrayList<ArrayList<Pair<Int, Int>>> {
        var list = ArrayList<ArrayList<Pair<Int, Int>>>()
        for (i in 1 until 5) {
            var record = ArrayList<Pair<Int, Int>>()
            var tolist = ArrayList<Int>()
            //转换list
            for (j in 0 until 4) {
                tolist.add(array[j][i])
            }

            if (tolist.contains(c)) {               //检测是否包含第一列的数字
                for ((s, u) in tolist.withIndex())
                    if (u == c) {
                        record.add(Pair(s, i))
                    }
            } else if (tolist.contains(12) || tolist.contains(13)) { //检测是否包含X2或X3
                for ((s, u) in tolist.withIndex())
                    if (u == 12 || u == 13) {
                        record.add(Pair(s, i))
                    }
            } else {
                break
            }

            list.add(record)
        }
        return list
    }

    /**
     * determine：当前值
     * range：值范围
     */
    fun probabilityRP(determine: Int, range: Int): Int {

        when (determine) {
            10 -> {
                if (Random().nextInt(100) == RP) {
                    return determine
                } else {
                    return Random().nextInt(range)
                }
            }
            11 -> {
                if (Random().nextInt(100) == RP) {
                    return determine
                } else {
                    return Random().nextInt(range)
                }
            }
        }

        return determine
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

    fun Send(agent: Agent, msg: String) = agent.Send(agent.CID, msg)

    override fun RoomDelete() {}
    override fun SendData(seat: Int, msg: String) {}
    override fun Log() {}
    override fun SeatNext(seat: Int) = if (seat == (numMax - 1)) 0 else (seat + 1)
    override fun SeatLast(seat: Int) = if (seat == 0) (numMax - 1) else (seat - 1)
    override fun RoundStart() {}
    override fun Status(seat: Int) {}
    override fun GameFapai() {}
    override fun Ai() {}
    override fun RoundCount() {}
    override fun RoundEnd() {}
    override fun RoundReset() {}
    override fun RoundRestart() {}
    override fun TimeOut_DoAi() {}
    override fun HasUserSeat(user: User) = arrPlayers.indexOf(user)
    override fun OnClientClose(user: User) {}
    override fun OnUserLeave(seat: Int) {}
    override fun OnUserReady(seat: Int) {}
    override fun OnUserSit(user: User): Int {
        return 4
    }

    override fun RoomLeave() {}
    override fun RoomSeatSync() {}
}

