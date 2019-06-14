package com.shine.controller.poker.xcsl


import com.shine.agent.Agent
import com.shine.amodel.Room
import com.shine.amodel.User
import com.shine.aservice.gamble.GambleService
import com.shine.controller.aHall.Hall
import com.shine.controller.aHall.RoomController
import com.shine.controller.gamble.xcsl.util.MultipleRate.award777
import com.shine.controller.gamble.xcsl.util.MultipleRate.awardLd
import com.shine.controller.gamble.xcsl.util.MultipleRate.awardPt
import com.shine.controller.gamble.xcsl.util.MultipleRate.awardRB
import com.shine.controller.gamble.xcsl.util.MultipleRate.awardREPLAY
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class TableSl constructor(override val rid: Int, override val creator: Int, override val pwd: String,
                          override val di: Int, override val roundMax: Int,
                          override val mTime: Int, override var armyBoo: Int) : Room {

    override val numMax = 4
    override val numMin = 4
    override val type = "xcsl"
    override val timeCreate = System.currentTimeMillis()

    override var numCur = 0
    override var timeWait = 0
    override var roundCur = 0

    override var isStart = false
    override val arrSeats = IntArray(numMax)            //-1- leaved, 0-safeNull, 1-sit, 2-ready, 3-
    override val arrPlayers = arrayOfNulls<User>(numMax)
    override val arrLeavers = IntArray(numMax)

    override val mapRoom = Hall.gMapRoom
    override val mapUserRoom = Hall.gMapUserRoom
    override val mapHalfRoom = RoomController.mapHalfRoom_ssz       //拉霸游戏不需要这个属性


    override val switchLog = true
    override var switchAi = true
    override val agentRoom = RoomController.RoomAgent
    override val all = 255
    override val cmd = "msg_" + type

    override val rand = Random()
    override var cardMount = IntArray(52)
    override val serviceScheduled = Executors.newSingleThreadScheduledExecutor()

    var count: Int = 0      //最多循环99次
    var list = ArrayList<IntArray>()

    var golist = arrayListOf<Int>(16, 256, 409, 536, 888)
    var array = Array(3) { IntArray(3) }
    var bb = false
    var rb = false
    var REPLAY = false

    fun statrCard(agent: Agent, root: JSONObject) {
        val index = gogo()
        //初始化
        bb = false
        rb = false

        if (index != -1) {
            var json = JSONObject()
            json.put("gogo", index)
            Send(agent, Msg(1, 1, json, "gogo", cmd))
            return
        }
        var str = StringBuilder()


        if (root.has("lucky")) {
            val lucky = root["lucky"] as Int

            if (lucky == 1) {                    //BB或BR
                println("进入BB或BR免费模式")
                val lu = lucky()
                //初始化值&生成连线
                for (i in 0 until 3) {
                    for (j in 0 until 3) {
                        array[i][j] = lu[i][j]
                        if (j == 2) {
                            str.append("""${array[i][j]}""")
                        } else {
                            str.append("""${array[i][j]},""")
                        }
                    }
                    if (i != 2) str.append("_")
                }
            } else {
                for (i in 0 until 3) {
                    for (j in 0 until 3) {
                        val r = randomCard()
                        array[i][j] = r

                        if (j == 2) {
                            str.append("""${r}""")
                        } else {
                            str.append("""${r},""")
                        }
                    }

                    if (i != 2) str.append("_")
                }
            }
        }


        val score = firstLine() + twoLine() + threeLine()

        var json = JSONObject()
        json.put("array", str)
        json.put("score", score)
        json.put("bb", bb)
        json.put("rb", rb)
        json.put("REPLAY", REPLAY)

        Send(agent, Msg(1, 1, json, "start", cmd))
        //抽水
        updateUserCoin(agent.user, score.toFloat())

        //更新数据库中对应的机器的参数
        val robot = GambleService.queryRid(rid)
        if (score > 0) {
            robot.total = robot.total + 1
            robot.win = robot.win + 1
            if (count > robot.banker) robot.banker = count

            if (score > 0) {

                if (bb) {
                    robot.bb = robot.bb + 1
                }
                if (rb) {
                    robot.rr = robot.rr + 1
                }

            }
            //赢得比率
            robot.rate = robot.win.rem(robot.total.toDouble())
            GambleService.updateXcslData(robot)
        } else {
            robot.total = robot.total + 1
            GambleService.updateXcslData(robot)
        }
    }

    fun updateUserCoin(user: User, coin: Float) {
        val c = Hall.jPCoin(coin.toLong())//JP of System
    }

    fun firstLine(): Int {
        var v = IntArray(3)
        var o = IntArray(3)

        v[0] = array[0][0]
        v[1] = array[0][1]
        v[2] = array[0][2]

        o[0] = array[0][0]
        o[1] = array[1][1]
        o[2] = array[2][2]
        var score = judge(v) + judge(o)
        return score
    }

    fun twoLine(): Int {
        var v = IntArray(3)

        v[0] = array[1][0]
        v[1] = array[1][1]
        v[2] = array[1][2]

        var score = judge(v)
        return score
    }

    fun threeLine(): Int {
        var v = IntArray(3)
        var o = IntArray(3)

        v[0] = array[2][0]
        v[1] = array[2][1]
        v[2] = array[2][2]

        o[0] = array[2][0]
        o[1] = array[1][1]
        o[2] = array[0][2]
        var score = judge(v) + judge(o)
        return score
    }


    fun judge(array: IntArray): Int {
        var score = 0
        when (array[0]) {
            1 -> {
                bb = award777(array)
                rb = awardRB(array)
            }
            3 -> {
                score = score + awardLd(array)
            }
            4 -> {
                score = score + awardPt(array)
            }
            5 -> {
                score = score + 2
            }
            6 -> {
                REPLAY = awardREPLAY(array)
            }
        }
        return score
    }


    fun randomCard(): Int {
        while (true) {
            val s = Random().nextInt(7)
            if (s != 0) {
                return s
                break
            }
        }
    }

    fun random(r: Int): Int {
        while (true) {
            val s = Random().nextInt(r)
            if (s != 0) {
                return s
                break
            }
        }
    }

    fun gogo(): Int {
        val RandomNum = Random().nextInt(1000)
        var index = -1
        if (golist.indexOf(RandomNum) != -1) {
            index = golist.indexOf(RandomNum) + 1
        }
        return index
    }

    fun lucky(): Array<IntArray> {
        var lucky = Array(3) { IntArray(3) }

        lucky[0][0] = random(5)
        lucky[0][1] = 6
        lucky[0][2] = random(3)

        lucky[1][0] = 3
        lucky[1][1] = 3
        lucky[1][2] = 3


        lucky[2][0] = random(5)
        lucky[2][1] = 6
        lucky[2][2] = random(3)

        return lucky
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
    override fun RoomDelete() {}
    override fun RoomSeatSync() {}
}

