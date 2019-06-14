import com.shine.agent.Agent
import com.shine.amodel.Room
import com.shine.amodel.User
import com.shine.aservice.util.AccountUtil
import com.shine.controller.aHall.Hall
import com.shine.controller.aHall.RoomController
import com.shine.controller.poker.Landlords.tool.JSONTool
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
class TableMl constructor(override val rid: Int, override var armyBoo: Int) : Room {

    override val pwd = ""
    override val mTime = 0
    override val creator = -1
    override val roundMax = 0
    override val di = 0
    override val numMax = 1
    override val numMin = 1
    override val type = "ml"
    override val timeCreate = System.currentTimeMillis()
    override var cardMount = IntArray(1)
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
    override val switchLog = true
    override val serviceScheduled = Executors.newSingleThreadScheduledExecutor()

    override val agentRoom = RoomController.RoomAgent
    override val all = 255
    override val cmd = "msg_" + type
    var allBei = HashMap<Int, Int>()
    var photoType = PhotoType()

    class PhotoType() {
        var photoNum: Int = 0
        var multiple: Int = 0
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
        //坐下 开始游戏
        if (isStart) {
            return 0
        }
        isStart = true
        return 1
    }

    override fun OnUserLeave(uid: Int) {
        mapUserRoom.remove(uid)
        isStart = false
        photoType.photoNum = 0
        photoType.multiple = 0
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

    var result = IntArray(53)

    //Logic
    val ra = Random()

    fun rand() = ra.nextInt(24)
    var idexs = mutableListOf<Int>()

    //num  : 结果数值
    fun ya(root: JSONObject, pt: PhotoType): PhotoType {
        var bar = root["bar"] as Int
        var seven = root["seven"] as Int
        var star = root["star"] as Int
        var watermelon = root["watermelon"] as Int
        var smallBell = root["smallBell"] as Int
        var grape = root["grape"] as Int
        var orange = root["orange"] as Int
        var appal = root["appal"] as Int
        when (pt.photoNum) {
            1 -> {
                photoType.multiple = (pt.multiple * bar)
            }
            2 -> {
                photoType.multiple = (pt.multiple * seven)
            }
            3 -> {
                photoType.multiple = (pt.multiple * star)
            }
            4 -> {
                photoType.multiple = (pt.multiple * watermelon)
            }
            5 -> {
                photoType.multiple = (pt.multiple * smallBell)
            }
            6 -> {
                photoType.multiple = (pt.multiple * grape)
            }
            7 -> {
                photoType.multiple = (pt.multiple * orange)
            }
            8 -> {
                photoType.multiple = (pt.multiple * appal)
            }
        }

        //抽水JP
        if (photoType.multiple > 0) {
            photoType.multiple = (photoType.multiple.toLong() - Hall.jPCoin(photoType.multiple.toLong())).toInt()
        }

        return photoType
    }

    fun money(agent: Agent, root: JSONObject) {
//        println(root.toString())
        AccountUtil.inOrOutMoney(agent, root)
    }

    var fist = true
    fun kai(agent: Agent, root: JSONObject) {
        var idex = rand()
        if ((idex == 0 || idex == 12) && fist) {
            Send(agent, RoomController.Msg(1, detail = "", data = "luckTime", command = root["command"]))
            fist = false
            kai(agent, root)
            return
        } else {
            idex = if (idex == 0 || idex == 12) 1 else idex
            idexs.add(idex)
            Send(agent, RoomController.Msg(1, detail = idex, data = "kai", command = root["command"]))
        }
        fist = true


        //返回结算信息
        Send(agent, RoomController.Msg(1, detail = mutableListOf(ya(root, over(idex))), data = "over", command = root["command"]))
    }

    fun over(tu: Int): PhotoType {
        when (tu) {
            0 -> {
                photoType.multiple = 0
                photoType.photoNum = 0
                return photoType
            }
            1 -> {
                photoType.multiple = 5
                photoType.photoNum = 8
                return photoType
            }
            2 -> {
                photoType.multiple = 20
                photoType.photoNum = 5
                return photoType
            }
            3 -> {
                photoType.multiple = 10
                photoType.photoNum = 7
                return photoType
            }
            4 -> {
                photoType.multiple = 20
                photoType.photoNum = 5
                return photoType
            }
            5 -> {
                photoType.multiple = 100
                photoType.photoNum = 1
                return photoType
            }
            6 -> {
                photoType.multiple = 100
                photoType.photoNum = 1
                return photoType
            }
            7 -> {
                photoType.multiple = 5
                photoType.photoNum = 8
                return photoType
            }
            8 -> {
                photoType.multiple = 5
                photoType.photoNum = 8
                return photoType
            }
            9 -> {
                photoType.multiple = 15
                photoType.photoNum = 6
                return photoType
            }
            10 -> {
                photoType.multiple = 20
                photoType.photoNum = 4
                return photoType
            }
            11 -> {
                photoType.multiple = 20
                photoType.photoNum = 4
                return photoType
            }
            12 -> {
                photoType.multiple = 0
                photoType.photoNum = 0
                return photoType
            }
            13 -> {
                photoType.multiple = 30
                photoType.photoNum = 3
                return photoType
            }
            14 -> {
                photoType.multiple = 40
                photoType.photoNum = 2
                return photoType
            }
            15 -> {
                photoType.multiple = 5
                photoType.photoNum = 8
                return photoType
            }
            16 -> {
                photoType.multiple = 40
                photoType.photoNum = 2
                return photoType
            }
            17 -> {
                photoType.multiple = 10
                photoType.photoNum = 7
                return photoType
            }
            18 -> {
                photoType.multiple = 10
                photoType.photoNum = 7
                return photoType
            }
            19 -> {
                photoType.multiple = 15
                photoType.photoNum = 6
                return photoType
            }
            20 -> {
                photoType.multiple = 15
                photoType.photoNum = 6
                return photoType
            }
            21 -> {
                photoType.multiple = 20
                photoType.photoNum = 5
                return photoType
            }
            22 -> {
                photoType.multiple = 30
                photoType.photoNum = 3
                return photoType
            }
            223 -> {
                photoType.multiple = 30
                photoType.photoNum = 3
                return photoType
            }
        }
        photoType.multiple = 0
        photoType.photoNum = 0
        return photoType
    }

    fun tubiaofen(tu: Int): Int {
        when (tu) {
            0 -> return 50
            1 -> return 100
            2 -> return 150
            3 -> return 200
            4 -> return 250
            5 -> return 350
            6 -> return 500
            7 -> return 1000
            8 -> return 2000
            9 -> return 5000
        }
        return 0
    }

    //结算
    fun stop(agent: Agent, root: JSONObject) {
        if (root["detail"] == null) return
        var res = 0
        var lis = JSONTool.toList(JSONTool.toJson(root["detail"]).toString(), Int::class.java)!!
        if (lis.size == 0) return
        for (i in 0..lis.size - 1) {
            if (allBei.containsKey(lis[0])) {
                res += (allBei[lis[0]] as Int) * (tubiaofen(lis[0]))
            }
        }
        allBei.clear()
        Send(agent, RoomController.Msg(1, detail = res, data = "kai", command = root["command"]))
    }

    fun timeOutOver(agent: Agent, root: JSONObject) {
        val callable = java.lang.Runnable {
            stop(agent, root)
        }
        serviceScheduled.schedule(callable, 5.toLong(), TimeUnit.SECONDS)
    }

    override fun RoundStart() {
        val runnable = Runnable {
            //            start()
        }
        serviceScheduled.scheduleAtFixedRate(runnable, 0, 5.toLong(), TimeUnit.SECONDS)
    }

    override fun RoundCount() {}
    override fun Status(seat: Int) {}
    override fun SeatNext(seat: Int) = if (seat == (numMax - 1)) 0 else (seat + 1)
    override fun SeatLast(seat: Int) = if (seat == 0) (numMax - 1) else (seat - 1)
    override fun RoomLeave() {}
    override fun RoomDelete() {}
    override fun GameFapai() {}
    override fun RoundEnd() {}
    override fun RoundReset() {}
    override fun RoundRestart() {}
    override fun OnUserReady(seat: Int) {}
    override fun Log() {}
    override fun Ai() {}
    override fun TimeOut_DoAi() {}


    fun baoZiTou(kaiTou: IntArray) = if (kaiTou[0] == kaiTou[1] && kaiTou[1] == kaiTou[2]) kaiTou[0] else 0


    fun Send(agent: Agent, msg: String) = agent.Send(agent.CID, msg)
}