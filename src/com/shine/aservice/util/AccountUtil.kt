package com.shine.aservice.util

import com.shine.agent.Agent
import com.shine.aservice.user.UserService
import com.shine.controller.aHall.Hall.Send
import com.shine.controller.aHall.RoomController
import org.json.JSONObject


object AccountUtil {

//结算增加用户金币
    fun plus(agent: Agent, coin:Int){
        if(coin==0) return
        var user=UserService.getUserMsgByUID(agent.UID)
        UserService.updateUserCoin(user.coin+coin,user.uid)
    }

    //减少用户金币
    fun minux(agent: Agent, coin:Int){
        var user=UserService.getUserMsgByUID(agent.UID)
        if(user.coin>=coin){
            UserService.updateUserCoin(user.coin-coin,user.uid)
        }else{
            UserService.updateUserCoin(0,user.uid)
        }
    }

    fun inOrOutMoney(agent:Agent,root: JSONObject):Int{
        if (!root.has("type")) return 0       //must
        var money=0
        var inOrBack=root["type"] as Boolean
        var user= UserService.getUserMsgByUID(agent.UID)
        var m=root["money"] as Int
        var getMoney=m
        when{
            inOrBack->{
                if(user.coin<getMoney){
                    Send(agent, RoomController.Msg(0,detail="",data="no money",command = root["command"]))
                    return 0
                }else{
                    money+=getMoney
                    Send(agent, RoomController.Msg(1,detail=m,data="intMoney",command = root["command"]))
                    AccountUtil.minux(agent,getMoney)  //减少用户兑换金币
                }
            }
            !inOrBack->{
                Send(agent, RoomController.Msg(1,detail=m,data="outMoney",command = root["command"]))
                AccountUtil.plus(agent,getMoney)    //退币 增加用户金币
            }
        }
        return m
    }


}