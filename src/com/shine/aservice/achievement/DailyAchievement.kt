package com.shine.aservice.achievement

import com.shine.agent.Agent
import com.shine.amodel.*
import com.shine.aservice.util.DayGet.Get_This_DayNum
import com.shine.controller.aHall.RoomController
object DailyAchievement {


    //每日物品使用成就
    fun Prop_Use(agent: Agent,propType:String,user: User){
        when(propType){
            "gold" ->coin(agent,"useCoin",user)
            "exp"  ->coin(agent,"useExp",user)
        }
    }
    //金币/经验加成道具使用成就
    fun coin(agent: Agent,type:String,user: User){
     var aUser= AchievementUserService.selectAchievementUserOne(AchievementUser(uid=user.uid,triggerType =type))
        var isFull=0
        if(aUser.nextTeps<=(1+aUser.teps)){
            isFull=1
            Send(agent, RoomController.Msg(1, detail = "", data = "day", command = "achievement"))
        }
        AchievementUserService.updateAchievementUser(AchievementUser(uid=user.uid,atid=aUser.atid
                ,time = Get_This_DayNum(),teps=1+aUser.teps,isfulfill = isFull))     //修改成就信息
    }
    //每日购买物品成就
    fun Prop_Shop(agent: Agent,goods: Goods, user: User){
        if(!goods.otherType.equals("400000")) return
        when(goods.currency){
            "coin" ->coin(agent,"shopCoin",user)
            "gem"  ->coin(agent,"shopGem",user)
        }
    }
    //每日魅力值获取
    fun Charm_Count(agent: Agent,charm:Int,user: User){
        var aUser = AchievementUserService.selectAchievementUserOne(AchievementUser(uid = user.uid, triggerType = "getCharm")) //获取用户每日魅力值获取信息
        var isFull=0
        if(aUser.teps+charm>=aUser.nextTeps){
            isFull=1
            Send(agent, RoomController.Msg(1, detail = "", data = "day", command = "achievement"))
        }
        AchievementUserService.updateAchievementUser(AchievementUser(uid=user.uid,atid=aUser.atid
                ,time = Get_This_DayNum(),teps=aUser.teps+charm,isfulfill = isFull))     //修改成就信息+
    }
    //每日和一次金币成就获取
    fun consumeTemp(agent: Agent,money:Int,user: User):User{
        var aUserDay = AchievementUserService.selectAchievementUserOne(AchievementUser(uid = user.uid, triggerType = "coin")) //获取用户每日成就金币获取
        var aUser = AchievementUserService.selectAchievementUserOne(AchievementUser(uid = user.uid, triggerType = "getcoin")) //获取用户成就金币
        var isFullDay=0
        var isFull=0
        if(aUser.teps+money>=aUser.nextTeps){
            isFullDay=1
            Send(agent, RoomController.Msg(1, detail = "", data = "achievement", command = "achievement"))
        }
        if(aUserDay.teps+money>=aUserDay.nextTeps){
            isFull=1
            Send(agent, RoomController.Msg(1, detail = "", data = "day", command = "achievement"))
        }
        AchievementUserService.updateAchievementUser(AchievementUser(uid=user.uid,atid=aUserDay.atid
                ,time = Get_This_DayNum(),teps=aUserDay.teps+money,isfulfill = isFullDay))     //修改成就信息
        AchievementUserService.updateAchievementUser(AchievementUser(uid=user.uid,atid=aUser.atid
                ,time = Get_This_DayNum(),teps=aUser.teps+money,isfulfill = isFull))     //修改成就信息
        return user
    }
    //每日和一次金币成就获取
    fun consumeTemp(cid: Int,money:Int,user: User):User{
        var aUserDay = AchievementUserService.selectAchievementUserOne(AchievementUser(uid = user.uid, triggerType = "coin")) //获取用户每日成就金币获取
        var aUser = AchievementUserService.selectAchievementUserOne(AchievementUser(uid = user.uid, triggerType = "getcoin")) //获取用户成就金币
        var isFullDay=0
        var isFull=0
        if(aUser.teps+money>=aUser.nextTeps){
            isFullDay=1
            Send(cid, RoomController.Msg(1, detail = "", data = "achievement", command = "achievement"))
        }
        if(aUserDay.teps+money>=aUserDay.nextTeps){
            isFull=1
            Send(cid, RoomController.Msg(1, detail = "", data = "day", command = "achievement"))
        }
        AchievementUserService.updateAchievementUser(AchievementUser(uid=user.uid,atid=aUserDay.atid
                ,time = Get_This_DayNum(),teps=aUserDay.teps+money,isfulfill = isFullDay))     //修改成就信息
        AchievementUserService.updateAchievementUser(AchievementUser(uid=user.uid,atid=aUser.atid
                ,time = Get_This_DayNum(),teps=aUser.teps+money,isfulfill = isFull))     //修改成就信息
        return user
    }
    fun Send(agent: Agent, msg:String)=agent.Send(agent.CID,msg)
    fun Send(cid:Int,msg:String)=Agent.agents[cid]?.SendMessage(msg)
}