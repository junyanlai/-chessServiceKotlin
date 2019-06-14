package com.shine.controller.gamble.toubao

import com.shine.agent.Agent
import com.shine.amodel.Manager
import com.shine.controller.aHall.Hall
import org.json.JSONObject
import java.util.*

/**
 *  Create by Colin
 *  Date:2018/6/25.
 *  Time:9:39
 *  14803101
 */
object ManagerTb:Manager {

    override val type="tb"
    override val rand=Random()
    override val mapRoom= Hall.mapRoom
    override val mapUserRoom= Hall.mapUserRoom

    fun Create(){
        mapRoom[14803101]= TableTb(14803101, 50,0)
        mapRoom[14803102]= TableTb(14803102, 50,0)
        mapRoom[14803103]= TableTb(14803103, 50,0)
        mapRoom[14803104]= TableTb(14803104, 50,0)

        mapRoom[14803111]= TableTb(14803111, 500,0)
        mapRoom[14803112]= TableTb(14803112, 500,0)
        mapRoom[14803113]= TableTb(14803113, 500,0)
        mapRoom[14803114]= TableTb(14803114, 500,0)

        for ( i in 14803101 .. 14803114)
            mapRoom[i]?.RoundStart()
    }

    override fun OnJoin(agent: Agent, root: JSONObject) {
        val rid=root["detail"].toString().toInt()
        val table= mapRoom[rid] as TableTb

        val resultJoin=table?.OnUserSit(agent.user)
        when(resultJoin){
            0       ->Send(agent, Msg(resultJoin,"no room","join","hall_room"))
            1       ->Send(agent, Msg(resultJoin,"success","join","hall_room"))
            2       ->Send(agent, Msg(resultJoin,"game started","join","hall_room"))
            3       ->Send(agent, Msg(resultJoin,"no seat","join","hall_room"))
            4       ->Send(agent, Msg(resultJoin,"wrong","join","hall_room"))
            5       ->Send(agent, Msg(resultJoin,"wrong pwd","join","hall_room"))
        }
    }

    override fun OnList(agent: Agent, root: JSONObject) {

        val detail=JSONObject()
        for (i in (14803101..14803104))
            mapRoom[i]?.let {
                detail.put( it.rid.toString(), intArrayOf(it.timeWait,it.roundCur))
            }
        for (i in (14803111..14803114))
            mapRoom[i]?.let {
                detail.put( it.rid.toString(), intArrayOf(it.timeWait,it.roundCur))
            }
        Send(agent,Msg(1,detail,"list","hall_room"))
    }

    override fun HandleAll(agent: Agent, root: JSONObject){

        if (!root.has("card")) return       //must
        if (!root.has("detail")) return     //must

        val doo = root["data"] as String
        val card= root["card"].toString()

        val cards
                =card
                .removeSurrounding("[", "]")
                .split(",")
                .map { it.toInt() }
                .toIntArray()

        val uid = agent.UID
        val user= agent.user

        if (mapUserRoom[uid]==null) return
        val table = mapUserRoom[uid] as TableTb

        val seat = table.HasUserSeat(user)
        if (seat==-1){
            Send(agent, Msg(0,"not in room",doo,"msg_"+ type))
            return
        }

        val seatStatus=table.arrSeats[seat]
        if (seatStatus<1){
            Send(agent, Msg(0,"no sit",doo,"msg_"+ type))
            return
        }

        when(doo){
            "status"    -> table.Status(seat)
            "leave"     -> table.OnUserLeave(seat)

            "stake"     -> table.Stake(seat,cards)
            "onstake"   -> table.OnStake(seat,cards)
        }
    }

}