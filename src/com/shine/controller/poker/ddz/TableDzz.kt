package com.shine.controller.poker.ddz


import com.shine.amodel.Room
import com.shine.amodel.User
import com.shine.controller.aHall.Hall
import com.shine.controller.aHall.RoomController
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 *  Create by Colin
 *  Date:2018/6/8.
 *  Time:16:16
 */
class TableDzz constructor(override val rid: Int, override val creator: Int, override val pwd:String,
                           override val di: Int, override val roundMax: Int, override val mTime:Int, override var armyBoo: Int): Room {

    override val numMax=3
    override val numMin=3
    override val type="dzz"
    override val timeCreate=System.currentTimeMillis()

    override var numCur=0
    override var timeWait=0
    override var roundCur=0

    override var isStart = false
    override val arrSeats = IntArray(numMax)
    override val arrPlayers = arrayOfNulls<User>(numMax)
    override val arrLeavers = IntArray(numMax)

    override val mapRoom = Hall.mapRoom
    override val mapUserRoom = Hall.mapUserRoom
    override val mapHalfRoom = RoomController.mapHalfRoom_dzz

    override val rand=Random()
    override var switchAi=false
    override val switchLog = true
    override val serviceScheduled = Executors.newSingleThreadScheduledExecutor()

    override val agentRoom = RoomController.RoomAgent
    override val all = 255
    override val cmd="msg_"+type

    var cards3=IntArray(3)

    var jiabeiDone=false
    var jiabeiFirst=false
    val jiabeiMark=BooleanArray(numMax)
    var jiabeiPoint=0
    var jiabeiSeat=0
    var jiabeiMul=-1

    val intarr=IntArray(3)
    val cardBox=MutableList(0,{intarr})
    val cardBox2=MutableList(0,{intarr})
    var onedone=true
    var firstCall=false
    var timesPass=0

    var timeDo=0
    var seatWin=-1
    var seatPoint=-1
    val cardArray = Array(numMax) { MutableList(0,{0}) }
    val cardKinds= Array(numMax) { MutableList(0,{cardTypeDzz.ERROR}) }


    //...
    fun saps():Array<User>{

        val safeArr=Array(numMax,{RoomController.RoomUser})
        for (i in 0 until numMax)
            arrPlayers[i]?.let { safeArr[i]=it }
        return safeArr
    }

    override fun HasUserSeat(user: User): Int {
        if (!arrPlayers.contains(user)) return -1
        else return arrPlayers.indexOf(user)
    }

    override fun OnClientClose(user: User) {
        val seat=HasUserSeat(user)
        OnUserLeave(seat)
    }

    override fun OnUserSit(user: User): Int {
        if (arrPlayers.contains(user)){
            val seat=arrPlayers.indexOf(user)
            if (isStart) {arrSeats[seat]=2;mapHalfRoom.remove(arrPlayers[seat]?.uid)}
            else         arrSeats[seat]=1
            user.rid=rid
            SendData(all,Msg(1,seat,saps(),"sit",cmd))
            if (user.uid!=1) mapUserRoom.put(user.uid,this)
            numCur++
            return 1
        }

        RoomSeatSync()

        if (isStart)    return 2
        if (numCur==numMax)    return 3

        for ((i,u) in arrPlayers.withIndex()){
            if (u==null){
                arrSeats[i]=1
                arrPlayers[i]=user

                user.rid=rid
                SendData(all,Msg(1,i,saps(),"sit",cmd))
                OnUserReady(i)

                if (user.uid!=1) mapUserRoom.put(user.uid,this)
                numCur++

                return 1
            }
        }

        return 4
    }

    override fun OnUserLeave(seat: Int) {
        if (isStart){

            SendData(all,Msg(1,seat,2,"leave",cmd))
            arrSeats[seat]=-1
            arrPlayers[seat]?.let {
                if (it.uid!=1) mapHalfRoom.put(it.uid,this)
                if (it.uid!=1) mapUserRoom.remove(it.uid)
            }
            numCur--

            for (i in 0 until numMax)//for roomleave easy
                if (arrPlayers[i]?.uid==1){
                    arrSeats[i]=-1
                }

            RoomLeave()
            return
        }

        arrPlayers[seat]?.rid=0
        SendData(all,Msg(1,seat,1,"leave",cmd))//send before truly remove(or it cannot receive msg)

        arrSeats[seat]=0
        arrPlayers[seat]?.let {
            if (it.uid!=1) mapUserRoom.remove(it.uid)
            arrLeavers[seat]=it.uid
        }
        arrPlayers[seat]=null
        numCur--
        RoomLeave()
    }

    override fun OnUserReady(seat: Int) {
        arrSeats[seat]=2
        SendData(all,Msg(1,seat,arrSeats,"ready",cmd))

        if (isStart) return      //just unstart can start
        var allReady = true
        for (s in arrSeats)
            if (s!=2)
                allReady=false

        if (allReady)
            RoundStart()
    }


    //
    override fun RoomLeave() {
        var noLiveOne=true
        for ( status in arrSeats )
            if (status > 0)
                noLiveOne=false

        println("##### in leave =${noLiveOne}")
        if (noLiveOne)
            RoomDelete()
    }
    override fun RoomDelete() {
        for ( u in arrPlayers ){
            if (u==null)    continue
            if (u.uid==1)   continue
            mapUserRoom.remove(u.uid)
            mapHalfRoom.remove(u.uid)
            u.rid=0
        }
        for ( uid in arrLeavers ){
            if (uid==0)    continue
            if (uid==1)   continue
            mapHalfRoom.remove(uid)
        }
        mapRoom.remove(rid)
        switchAi=false
        serviceScheduled.shutdown()
        serviceScheduled.shutdownNow()
        if (switchLog) println("#_________ Room is deleted")
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
        println("#_________mapHalfRoom")
        println("#_________size=:${mapHalfRoom.size}")
        println("#_________content=:${mapHalfRoom}")
        println()
    }
    override fun SendData(seat: Int, msg: String) {
        if (seat>(numMax-1) && seat<all){
            val si=seat-numMax
            if (arrSeats[si]<1)   return
            arrPlayers[si]?.let { agentRoom.Send(it.cid,msg) }
            val json= JSONObject(msg)
            json.put("detail","hide")

            for (i in 0 until numMax){
                if (i==si) continue
                arrPlayers[i]?.let { agentRoom.Send(it.cid,json.toString()) }
            }
            return
        }

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

    //=====Logic Part
    override var cardMount= LogicDzz.GetCardMount()

    override fun SeatNext(seat: Int) = if (seat==(numMax-1)) 0 else (seat+1)
    override fun SeatLast(seat: Int) = if (seat==0) (numMax-1) else (seat-1)

    override fun GameFapai(){

        cards3=LogicDzz.MountSlice(cardMount,cardArray)
        for (i in 0 until numMax)
            SendData(i,Msg(1,jiabeiPoint,cardArray[i],"fapai",cmd))
    }


    fun DoJiabei(seat: Int, cards:IntArray){

        val m=cards[0]
        if(jiabeiDone){
            SendData(seat,Msg(0,seat,"jiabei done","jiabei",cmd))
            return
        }
        if (m<0 || m>3){
            SendData(seat,Msg(0,seat,"wrong jiabei","jiabei",cmd))
            return
        }
        if (jiabeiMark[seat]){
            SendData(seat,Msg(0,seat,"jiabeied","jiabei",cmd))
            return
        }
        if (seat!=jiabeiPoint){
            SendData(seat,Msg(0,seat,"wrong order","jiabei",cmd))
            return
        }
        if (jiabeiMul!=0 && m<=jiabeiMul){
            SendData(seat,Msg(0,seat,"wrong jiabei","jiabei",cmd))
            return
        }

        jiabeiPoint=SeatNext(seat)
        if (m>0){
            jiabeiMul=m
            jiabeiSeat=seat
        }

        jiabeiMark[seat]=true
        jiabeiMul=m
        if (!jiabeiFirst){
            jiabeiFirst=true
            jiabeiMark[seat]=false
        }

        SendData(all,Msg(1,seat,m,"jiabei",cmd))

        if (jiabeiMark[0]&& jiabeiMark[1]&& jiabeiMark[2]){
            JiabeiDone()
            return
        }
        if (m==3) JiabeiDone()
    }

    fun JiabeiDone(){

        jiabeiDone=true
        seatPoint=jiabeiSeat

        SendData(all,Msg(1,jiabeiSeat,jiabeiMul,"jiabeidone",cmd))
    }


    fun DoPass(seat: Int){

        if (seat!=seatPoint){
            SendData(seat,Msg(0,seat,"wrong order","pass",cmd))
            return
        }

        if (onedone){
            SendData(seat,Msg(0,seat,"round done","pass",cmd))
            return
        }

        cardBox2.add(intArrayOf(0))
        timeDo++
        timesPass++
        seatPoint=SeatNext(seat)
        SendData(all,Msg(1,seat,"success","pass",cmd))

        if (timesPass==3){
            timesPass=0
            onedone=true
        }

        TimeOut_Da(SeatNext(seat))
    }

    fun DoDa(seat: Int,cards:IntArray){

        if(!jiabeiDone){
            SendData(seat,Msg(0,seat,"unjiabei","da",cmd))
            return
        }
        if (seat!=seatPoint){
            SendData(seat,Msg(0,seat,"wrong order","da",cmd))
            return
        }
        if (!cardArray[seat].containsAll(cards.asList())){
            SendData(seat,Msg(0,seat,"wrong card","da",cmd))
            return
        }
        if (LogicDzz.Type(cards).order==0){
            SendData(seat,Msg(0,seat,"wrong type","da",cmd))
            return
        }

        if (onedone) {//call
            if (!firstCall && !cards.contains(3)) {
                SendData(seat, Msg(0, seat, "wrong call", "da", cmd))
                return
            }
            if (!firstCall && cards.contains(3)) firstCall = true
            onedone=false
        }

        else//follow
            if (!LogicDzz.Done(cardBox[0],cards)){
                SendData(seat,Msg(0,seat,"less weight","da",cmd))
                return
            }

        cardArray[seat].removeAll(cards.asList())
        cardBox.add(0,cards)
        cardBox2.add(0,cards)
        cardKinds[seat].add(LogicDzz.Type(cards))
        timeDo++
        timesPass=0
        seatPoint=SeatNext(seat)
        SendData(all,Msg(1,seat,cards,"da",cmd))

        if (cardArray[seat].size==0){
            isStart=false
            seatWin=seat
            seatPoint=-1
            RoundCount()
            RoundEnd()
            return
        }

        TimeOut_Pa(SeatNext(seat))
    }


    override fun Status(seat: Int){

        Log()

        val cardNums=IntArray(numMax)
        for (i in 0 until numMax)
            cardNums[i]=cardArray[i].size

        /*val status=JSONObject()
        status.put("cardHand"+seat,cardArray[seat])
        status.put("cardNums"+seat,cardNums)*/

        println("SSS:seatPoint=$seatPoint")

        SendData(seat,Msg(cardArray[seat],seatPoint,cardNums,"status",cmd))
    }

    override fun RoundStart() {
        isStart=true
        jiabeiDone=false

        roundCur++
        GameFapai()
        TimeOut_Pa()
    }

    override fun RoundCount() {

        val m= intArrayOf(0,0,0,0)
        val meansChange=IntArray(numMax)
        for (i in 0 until numMax)
            meansChange[i]=di*m[i]

        val detail=JSONObject()
        detail.put("roundCur",roundCur)
        detail.put("multiple",m)
        detail.put("meansChange",meansChange)
        for (i in 0 until numMax)
            detail.put("cardHand"+i,cardArray[i])

        SendData(all,Msg(1,seatWin,detail,"multiples",cmd))
    }

    override fun RoundEnd() {

        isStart=false
        for ((i,user) in arrPlayers.withIndex()){
            if (user==null) {
                arrSeats[i]=0
                continue
            }
            if (user.uid==1){
                arrSeats[i]=0
                OnUserLeave(i)
            }
            if (arrSeats[i]>0) arrSeats[i]=2
            if (arrSeats[i]<1) arrSeats[i]=0
        }

        if (roundCur>=roundMax) RoomDelete()
        else    RoundRestart()
    }

    override fun RoundReset() {

        timeWait= Random().nextInt(10)+5
        RoomSeatSync()

        for (i in 0 until numMax) {

        }

    }

    override fun RoundRestart() {
        if (isStart) return
        timeWait=10
        println("!!!### will restart in 10 second")
        val callable = java.lang.Runnable{
            RoundReset()
            Ai()
        }
        serviceScheduled.schedule(callable, timeWait.toLong(), TimeUnit.SECONDS)
    }



    val waittime = mTime + 1
    fun TimeOut_Pa(){
        if (!isStart)   return
        if (!switchAi)  return
        val callable = java.lang.Runnable{

            if (jiabeiDone) return@Runnable
            for (i in 0 until numMax)
                if (jiabeiMark[i])   continue
                else DoPass(i)

            println("############超时# 13秒 SwitchPASS")
            TimeOut_Da(seatPoint)
        }
        serviceScheduled.schedule(callable, 13L, TimeUnit.SECONDS)
    }
    fun TimeOut_Pa(seat: Int){
        if (!isStart)   return
        if (!switchAi)  return
        val dotimebefore = timeDo
        val callable = java.lang.Runnable{

            if (timeDo != dotimebefore)
                return@Runnable
            if (seatPoint!=seat)
                return@Runnable

            DoPass(seat)
            println("############超时# " + waittime + "秒 PASS")
        }
        serviceScheduled.schedule(callable, waittime.toLong(), TimeUnit.SECONDS)
    }
    fun TimeOut_Da(seat: Int){
        if (!isStart)   return
        if (!switchAi)  return
        val dotimebefore = timeDo
        val callable = java.lang.Runnable{
            if (timeDo != dotimebefore) return@Runnable
            if (seatPoint!=seat)    return@Runnable

            val cards=IntArray(1)

            if (!firstCall) cards[0]=3
            else    cards[0]=cardArray[seat][0]

            DoDa(seat,cards)
            println("############超时# " + waittime + "秒 da")
        }
        serviceScheduled.schedule(callable, waittime.toLong(), TimeUnit.SECONDS)
    }

    override fun Ai(){
        for (i in 0 until numMax){
            if (arrSeats[i]==0) {
                val user = User(
                        uid = 1,
                        avatar = Random().nextInt(9).toString(),
                        nick = Hall.listRobotName[Random().nextInt(34270)])
                OnUserSit(user)
            }
            OnUserReady(i)
        }
    }
    override fun TimeOut_DoAi() {
        if (isStart) return
        if (!switchAi)return
        timeWait= Random().nextInt(10)+5
        println("timeWait=$timeWait")
        val callable = java.lang.Runnable{
            Ai()
        }
        serviceScheduled.schedule(callable, timeWait.toLong(), TimeUnit.SECONDS)
    }


}