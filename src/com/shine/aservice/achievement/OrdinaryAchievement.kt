package com.shine.aservice.achievement

import com.shine.agent.Agent
import com.shine.amodel.*
import com.shine.aservice.util.DayGet.Get_This_DayNum
import com.shine.controller.aHall.RoomController

object OrdinaryAchievement {


    //购买物品累计普通成就
    fun Buy_Goods(agent: Agent,user: User,count:Int, goodsType: String) {
        if("".equals(get_triggerType(goodsType))) return
        //检查只能获取一次的成就类型为：triggerType
            var aUser = AchievementUserService.selectAchievementUserOne(AchievementUser(uid = user.uid, triggerType =get_triggerType(goodsType),atype = "achievement"))
            var isFull=0
            if(aUser.teps+count>=aUser.nextTeps){
                isFull=1
                Send(agent, RoomController.Msg(1,detail="",data="achievement",command = "achievement"))
            }
                AchievementUserService.updateAchievementUser(AchievementUser(uid=user.uid,atid=aUser.atid
                        ,time = Get_This_DayNum(),teps=count+aUser.teps,isfulfill = isFull))     //存入用户成就表
    }
    val mapUser= listOf("300000","320000","340000","360000","380000")
    fun get_triggerType(goodsType:String):String{
        when{
            goodsType in mapUser                ->return "shopFashion"
            "660000".equals(goodsType)          ->return "shopBg"
            "210005".equals(goodsType)          ->return "userAgate"
        }
        return ""
    }

    //使用物品累计成就
    fun Use_Achievements(agent: Agent,user: User,commodityId:Int,count:Int){
        var triggerType=get_triggerType(commodityId.toString())
        if("".equals(triggerType))return
        var aUser = AchievementUserService.selectAchievementUserOne(AchievementUser(uid = user.uid, triggerType =triggerType,atype = "achievement"))
        var isFull=0
        if(aUser.teps+count>=aUser.nextTeps){
            isFull=1
            Send(agent, RoomController.Msg(1,detail="",data="achievement",command ="achievement"))
        }
            AchievementUserService.updateAchievementUser(AchievementUser(uid=user.uid,atid=aUser.atid
                    ,time = Get_This_DayNum(),teps=count+aUser.teps,isfulfill = isFull))     //存入用户成就表
    }
//修改累计消费
    fun User_useMoney(agent: Agent,user: User,moeny:Int) {
    var aUser = AchievementUserService.selectAchievementUserOne(AchievementUser(uid = user.uid, triggerType = "consume", atype = "achievement"))
    var isFull = 0
    if (aUser == null) {
        var aTemp = AchievementTempService.selectAchievementTempByTeps(AchievementTemp(triggerType = "consume", teps = moeny))
        if (aTemp != null) {
            var aUser = AchievementUser(uid = agent.UID, atid = aTemp.atid, aname = aTemp.name, teps = moeny, atype = aTemp.type
                    , triggerType = aTemp.triggerType, isCreated = 0, isfulfill = 0, time = Get_This_DayNum(), nextTeps = aTemp.teps)
            AchievementUserService.insertAchievementUser(aUser)
        }
    } else {
        if (aUser.teps + moeny >= aUser.nextTeps) {
            isFull = 1
            Send(agent, RoomController.Msg(1, detail = "", data = "achievement", command = "achievement"))
        }
        AchievementUserService.updateAchievementUser(AchievementUser(uid = user.uid, atid = aUser.atid
                , time = Get_This_DayNum(), teps = moeny + aUser.teps, isfulfill = isFull))     //存入用户成就表
     }
    }
    fun Send(agent: Agent, msg:String)=agent.Send(agent.CID,msg)
}