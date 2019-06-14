package com.shine.controller.poker.Landlords

import com.shine.agent.Agent
import com.shine.controller.aHall.Hall
import com.shine.controller.poker.ddz.TableDdz
import org.json.JSONObject
import java.util.*

/**
 *  Create by Colin
 *  Date:2018/6/7.
 *  Time:18:05
 */
object ManagerDdz {
    val type="ddz"
    val rand= Random()
    val mapRoom= Hall.mapRoom
    val mapUserRoom= Hall.mapUserRoom
    val mapUserLeaveRom=Hall.mapUserLeaveRom
    fun OnJoin(agent: Agent, root: JSONObject){

        val rid=root["detail"].toString().toInt()
        val table= mapRoom[rid]

        val pwd=root["type"].toString()
        if (!table?.pwd.equals(pwd)){
            ManagerDdz.Send(agent, ManagerDdz.Msg(5, "wrong pwd", "join", "hall_room"))
            return
        }
        val resultJoin=table?.OnUserSit(agent.user)
        when(resultJoin){
            0       -> ManagerDdz.Send(agent, ManagerDdz.Msg(resultJoin, "no room", "join", "hall_room"))
            1       -> ManagerDdz.Send(agent, ManagerDdz.Msg(resultJoin, "success", "join", "hall_room"))
            2       -> ManagerDdz.Send(agent, ManagerDdz.Msg(resultJoin, "game started", "join", "hall_room"))
            3       -> ManagerDdz.Send(agent, ManagerDdz.Msg(resultJoin, "no seat", "join", "hall_room"))
            4       -> ManagerDdz.Send(agent, ManagerDdz.Msg(resultJoin, "wrong", "join", "hall_room"))
            5       -> ManagerDdz.Send(agent, ManagerDdz.Msg(resultJoin, "wrong pwd", "join", "hall_room"))
        }
    }

    //data=list
    //command=hall_room
    fun OnList(agent: Agent, root: JSONObject){

        val rids= MutableList(0,{0})
        //val tables= MutableList(0,{ room0() })
        val detail = root["detail"] as JSONObject

        val di=detail["di"].toString().toInt()
        val time=detail["time"].toString().toInt()
        val round=detail["round"].toString().toInt()

        if (di==0 && di==time && time==round){

            for((rid,table) in mapRoom){
                if (table.type!= type)  continue
                if (table.isStart)  continue
                if (table.numCur==4)    continue
                if (table.pwd!="")  continue
                rids.add(rid)
            }

            ManagerDdz.Send(agent, ManagerDdz.Msg(rids.size, rids, "list", "hall_room"))
            return
        }

        for((rid,table) in mapRoom){
            if (table.type!= type)  continue

            if (table.isStart)  continue
            if (table.numCur==4)    continue
            if (table.pwd!="")  continue
            if (table.di!=di)   continue

            if (table.roundMax!=round)  continue

            rids.add(rid)
        }
        ManagerDdz.Send(agent, ManagerDdz.Msg(rids.size, rids, "list", "hall_room"))
    }



    //command=msg_maj
    //data=leave/ready/ chi/gang/peng/hu/pass
    fun HandleAll(agent: Agent, root:JSONObject){

        if (!root.has("card")) return       //must
        if (!root.has("detail")) return     //must
        val doo = root["data"] as String
        val uid = agent.UID
        val user= agent.user
        if (mapUserRoom[uid]==null) return
        val table = mapUserRoom[uid] as TableDdz
        if (table==null){
            Send(agent, Msg(0, "illegal request", doo, "msg_" + ManagerDdz.type))
            return
        }

        val seat = table.HasUserSeat(user)
        if (seat==-1){
            Send(agent,Msg(0, "not in room", doo, "msg_" + ManagerDdz.type))
            return
        }

        val seatStatus=table.arrSeats[seat]
        if (seatStatus<1){
            Send(agent, Msg(0, "no sit", doo, "msg_" + ManagerDdz.type))
            return
        }
        table.thisAgent(agent,seat)
        when(doo){
            "tuichu"    ->table.innerMap(agent,seat)
            "status"    -> table.Status(seat)           //？
            "leave"     -> table.OnUserLeave(seat)    //离开
            "ready"     ->  readyMethod(table,agent,root,seat) //准备
            "mingpai"   -> table.GameMing(seat)

            "dizhu"     -> table.jiaodizhu(root)
            "jiabei"    -> table.DoJiabei(seat,user)

            "pass"      -> table.DoPass(seat)
            "da"        -> table.playHand(root)
        }
        return
    }

    fun readyMethod(table:TableDdz,agent: Agent,root:JSONObject,seat:Int){
        table.OnUserSit(agent.user)
//        Send(agent,ManagerDdz.Msg(1, seat, "ready", "hall_room"))
        table.TimeOut_NewCreatRoom(agent,root,seat)
    }
    fun Rand(min:Int,max:Int)= rand.nextInt(max-min)+min
    fun RidSafeGet():Int{
        val rid=Rand(100000, 999999)
        if (ManagerDdz.mapUserRoom[rid]==null) return rid
        else return RidSafeGet()
    }

    //data=create
    //command=hall_room
    fun OnCreate(agent: Agent, root: JSONObject) {
        var haveRoom=false
        val uid =agent.UID
        var rid = ManagerDdz.RidSafeGet()
        val detail = root["detail"] as JSONObject
        val pwd=detail["pwd"].toString()                          //创建时候的密码
        val di=detail["di"].toString().toInt()                       //底
        val round=detail["round"].toString().toInt()                 //局数
        var time=detail["time"].toString().toInt()                   // 等待时间
        var type=root["type"].toString()                             // 房间类型
        var armyBoo = 0;
        if (time<2)    time=10

        //军团战相关

        if (root.has("armyBoo")) {
            armyBoo = 1
        }

        for (i in mapRoom.values){                                 //查找房间 如果存在没有满人的进入
            if(i.arrSeats.contains(0)&&type.equals(i.type)&&pwd.equals(i.pwd)&&di.equals(i.di)) {
                rid=i.rid
                haveRoom=true
//                println("#1_________RoomCreate: $rid")
                break
            }
        }
        val table:Any
        if(haveRoom){
            table= mapRoom[rid] as TableDdz
            table.arrPlayers.toMutableList().add(agent.user)
//            println("#2_________RoomCreate: $rid")
        }else{
            table=TableDdz(rid,uid,pwd, di,round,time,armyBoo)                    //生成房间对象
            mapRoom[rid]=table                                                       //存入房间map
//            println("#_3________RoomCreate: $rid")
        }
        Send(agent,ManagerDdz.Msg(1, "create", mapRoom[rid], "hall_room"))
        table.OnUserSit(agent.user)
        table.TimeOut_DoAi()
    }
    fun returnRomeMsg(agent: Agent, root: JSONObject){
        var tab=mapUserLeaveRom[agent.user.uid] as TableDdz
        if(tab.rid!=root["detail"] as Int) return
        var seat=tab!!.HasUserSeat(agent.user)
        tab.arrPlayers[seat]=agent.user
        tab.arrSeats[seat]=2
        Send(agent,ManagerDdz.Msg(1, tab.isDizhuSeat, "dizhu","hall_room"))
        Send(agent,ManagerDdz.Msg(1, tab.isMingCard, "mingpai","hall_room"))
        Send(agent,ManagerDdz.Msg(1, mutableListOf(tab.card), "card","hall_room"))
        Send(agent,ManagerDdz.Msg(1, seat, "seat","hall_room"))
        Send(agent,ManagerDdz.Msg(1, tab.saps(),"allPeople","hall_room"))
        Send(agent,ManagerDdz.Msg(1, tab.allPermission, "seatCard","hall_room"))
        Send(agent,ManagerDdz.Msg(1, tab.seatPoint,"thisPeople","hall_room"))
        Send(agent,ManagerDdz.Msg(1, tab.thisTime, "thisTime","hall_room"))
        mapUserRoom[agent.user.uid]=tab
        mapUserLeaveRom.remove(agent.user.uid)
    }
    //get SendMsg
    fun Msg(result:Any?,detail:Any?,data:Any?,command:Any?):String{
        val msg = JSONObject()
        msg.put("command", command)
        msg.put("result", result)
        msg.put("data", data)
        msg.put("detail", detail)
        return msg.toString()
    }
    fun Send(agent: Agent,msg:String)=agent.Send(agent.CID,msg)
}