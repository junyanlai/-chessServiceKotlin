package com.shine.amodel

import com.shine.agent.Agent
import org.json.JSONObject
import java.util.*

/**
 *  Create by Colin
 *  Date:2018/6/25.
 *  Time:9:36
 */
interface Manager {

    val type:String
    val rand:Random
    val mapRoom:MutableMap<Int,Room>
    val mapUserRoom:MutableMap<Int,Room>

    //@hall_room/create
    fun OnCreate(agent: Agent, root: JSONObject) {}

    //@hall_room/join
    fun OnJoin(agent: Agent, root: JSONObject){}

    //@hall_room/list
    fun OnList(agent: Agent, root: JSONObject){}

    //@hall_room/viplist
    fun OnVipList(agent: Agent, root: JSONObject){}


    //@msg_type(leave/ready/ chi/gang/peng/hu/pass)
    fun HandleAll(agent: Agent, root: JSONObject){}



    //get RoomId
    fun Rand(min:Int,max:Int)= rand.nextInt(max-min)+min
    fun RidSafeGet():Int{

        val rid= Rand(100000,999999)
        if (mapUserRoom[rid]==null) return rid
        else return RidSafeGet()
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
    fun Send(agent: Agent, msg:String)=agent.Send(agent.CID,msg)
}