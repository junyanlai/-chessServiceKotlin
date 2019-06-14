
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
class TableCb constructor(override val rid: Int, override var armyBoo: Int):Room {

    override val pwd=""
    override val mTime=0
    override val creator=-1
    override val roundMax=0
    override val di=0
    override val numMax=1
    override val numMin=1
    override val type="cb"
    override val timeCreate=System.currentTimeMillis()
    override var cardMount=IntArray(1)
    override var numCur=0
    override var timeWait=0     //baozi
    override var roundCur=0     //huangjin

    override var isStart = false
    override val arrSeats = IntArray(numMax)
    override val arrPlayers = arrayOfNulls<User>(numMax)
    override val arrLeavers = IntArray(numMax)

    override val mapRoom = Hall.mapRoom
    override val mapUserRoom = Hall.mapUserRoom
    override val mapHalfRoom = HashMap<Int,Room>()

    override val rand= Random()
    override var switchAi=false
    override val switchLog = true
    override val serviceScheduled = Executors.newSingleThreadScheduledExecutor()
    var integral=0
    override val agentRoom = RoomController.RoomAgent
    override val all = 255
    override val cmd="msg_"+type
    var allBei= HashMap<Int,Int>()

    override fun HasUserSeat(user: User): Int {
        if (!arrPlayers.contains(user)) return -1
        else return arrPlayers.indexOf(user)
    }

    override fun OnClientClose(user: User) {
        val seat=HasUserSeat(user)
        OnUserLeave(seat)
    }

    override fun OnUserSit(user: User): Int {
        //坐下 开始游戏
        integral=0
        return 1
    }

    override fun OnUserLeave(uid: Int) {
        mapUserRoom.remove(uid)
        integral=0
    }

    override fun RoomSeatSync() {
        var numNull=0
        for ((i,u) in arrPlayers.withIndex()){
            if (u==null){
                numNull++
            }else{
                if (arrSeats[i]==0)
                    arrSeats[i]=1
            }
        }
        numCur=numMax-numNull
    }


    override fun Log() {
        if (!switchLog)    return
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

        if (seat==all){
            for ( (i,status) in arrSeats.withIndex()){
                if (status<1) continue
                if (arrPlayers[i]==null) continue
                if (arrPlayers[i]!!.uid==1) continue            //robot

                arrPlayers[i]?.let { agentRoom.Send(it.cid,msg) }
            }
            return
        }
        if (arrSeats[seat]<1)   return
        arrPlayers[seat]?.let { agentRoom.Send(it.cid,msg) }
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
    var result=IntArray(53)

    //所有图片id
    var stakess = intArrayOf(0,1,2,3,4,5,6,7,8,9)

    fun ya(agent:Agent,root:JSONObject){
        var id=root["key"] as Int
        var bei=root["bei"] as Int
        allBei.put(id,bei)
        Send(agent, RoomController.Msg(1,detail=allBei,data="ya",command = root["command"]))
    }

    fun money(agent:Agent,root:JSONObject){
        var inOrBack=root["type"] as Boolean
        if(inOrBack){
            var money=AccountUtil.inOrOutMoney(agent,root)
            integral=money+integral
        }else{
            var money=AccountUtil.inOrOutMoney(agent,root)

            if(integral<money)
                integral=0
            else
                integral=integral-money
        }
    }
    //Logic
    val ra=Random()
    fun rand()= ra.nextInt(8)
    fun kai(agent:Agent,root:JSONObject){
        var ya=root["ya"] as Int
        if(integral<(ya)){
            Send(agent, RoomController.Msg(0,detail="err_noMoney",data="no",command = root["command"]))
            return
        }
        var list= mutableListOf<Int>()
        for(i in 0..2){
            list.add(rand())
        }
        Send(agent, RoomController.Msg(1,detail=list,data="kai",command = root["command"]))
        var over= over(ya,list).toLong()
        over=over-Hall.jPCoin(over)
        //结算信息
        integral=integral+over.toInt()
        Send(agent, RoomController.Msg(1,detail=over,data="over",command = root["command"]))
    }
    fun tubiaofen(tu:Int):Int{
        when(tu){
            0->return 50
            1->return 100
            2->return 150
            3->return 200
            4->return 250
            5->return 350
            6->return 500
            7->return 1000
        }
        return 0
    }
    //结算
    fun  over(ya:Int,lis:MutableList<Int>):Int{
        if(lis[0]==lis[1]&&lis[0]==lis[2]){
            integral=integral-(ya)
            return ya*tubiaofen(lis[0])
        }
        integral=integral-(ya)
        return 0
    }

    fun stop(agent:Agent,root: JSONObject){
        if(root["detail"]==null) return
        var res=0
        var lis=JSONTool.toList(JSONTool.toJson(root["detail"]).toString(),Int::class.java)!!
        if(lis[0]==lis[1]&&lis[0]==lis[2]){
            if(allBei.containsKey(lis[0])) {
                res= (allBei[lis[0]] as Int) *  (tubiaofen(lis[0]))
            }
        }
        allBei.clear()
        Send(agent, RoomController.Msg(1,detail=res,data="stop",command = root["command"]))
    }
    fun timeOutOver(agent:Agent,root: JSONObject){
        val callable = java.lang.Runnable{
            stop(agent,root)
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
    override fun Status(seat: Int){}
    override fun SeatNext(seat: Int) = if (seat==(numMax-1)) 0 else (seat+1)
    override fun SeatLast(seat: Int) = if (seat==0) (numMax-1) else (seat-1)
    override fun RoomLeave() {}
    override fun RoomDelete() {}
    override fun GameFapai() {}
    override fun RoundEnd() {}
    override fun RoundReset() {}
    override fun RoundRestart() {}
    override fun OnUserReady(seat: Int) {}

    override fun Ai(){}
    override fun TimeOut_DoAi(){}


    fun baoZiTou(kaiTou: IntArray)=if (kaiTou[0] == kaiTou[1] && kaiTou[1] == kaiTou[2]) kaiTou[0] else 0


    fun Send(agent: Agent, msg:String)=agent.Send(agent.CID,msg)
}