package com.shine.controller.aHall

import com.shine.agent.Agent
import com.shine.amodel.*
import com.shine.aservice.achievement.AchievementTempService
import com.shine.aservice.achievement.AchievementUserService
import com.shine.aservice.prize.PrizeService
import com.shine.aservice.shop.FmccService
import com.shine.aservice.shop.StoreService
import com.shine.aservice.user.UserService
import com.shine.aservice.util.DayGet
import org.json.JSONObject
import java.util.*

/**
 * 奖励系统
 */
object AwardController {

    fun HandleAll(agent: Agent, root: JSONObject){
        var detail=root.get("data") as String
        when(detail){
            "pz"            -> Into_CoinOrGem(agent,root)           //成就 领取
            "sign"          -> Into_Sign(agent,root)                //签到奖励 领取
            "achievers"     -> Achievement_Award(agent,root)        //金银铜成就奖励
            "userMsg"       -> Achievement_User_Msg(agent, root)    //获取用户成就信息
            "online"        -> onlineGet(agent,root)                //获取用户每日信息
            "fmcc"          -> Get_All_Buff(agent, root)            //获取buf信息
            "iner"          -> Get_All_AC(agent, root)              // 成就/签到    奖励信息
            else            ->return
        }
    }
    fun onlineGet(agent:Agent, root:JSONObject){
      var aUser=AchievementUserService.selectOnline(AchievementUser(atype=root["type"] as String,uid=agent.user.uid,isCreated = 0))  //查询所有type 类型的成就奖励
        if(aUser == null){
            Send(agent, RoomController.Msg(0, detail = "null", data = "online", command =  root["command"]))
            return
        }
      Send(agent, RoomController.Msg(1, detail = mutableListOf(aUser), data = "online", command =  root["command"]))
    }

    //获取用户 成就信息
    fun Achievement_User_Msg(agent:Agent, root:JSONObject){
        var isCreate=0
        if("day".equals(root["type"] as String)){
            isCreate=2
        }
        var aUserList= AchievementUserService.getAchievementCount(AchievementUser(atype =root["type"] as String ,uid=agent.UID,isCreated = isCreate))  //查询所有type 类型的成就奖励
        Send(agent, RoomController.Msg(1, detail = aUserList, data = root["data"], command =  root["command"]))
    }

    fun Get_All_Buff(agent:Agent, root:JSONObject){
        var fmcc= FmccService.selectFmcc(Fmcc(uid=agent.UID))
        Send(agent, RoomController.Msg(1, detail = fmcc, data = root["data"], command = root["command"]))
    }

    fun Get_All_AC(agent:Agent, root:JSONObject){
        var aTemp= AchievementTempService.selectAchievementTemp(AchievementTemp(type=root["type"] as String))
        var allSign= mutableListOf<Prize>()
        for ( i in aTemp){
            allSign.addAll(PrizeService.selectPrizeByTempId(i.atid))
        }
        var count=AchievementUserService.selectAchievementUser(AchievementUser(uid=agent.UID,atype=root["type"] as String,isCreated = 0,isfulfill = 1))
//        AchievementController.Send(agent, RoomController.Msg(result = 1, detail = allSign, data = String.format("%s_%s","inner",root["type"] as String), command = root["command"], count = count.size))
        Send(agent, RoomController.Msg(result = 1, detail = count, data = String.format("%s_%s","inner",root["type"] as String), command = root["command"]))
    }

    //第一天
    fun isFirstDayOfMonth(date: Date):Boolean {
        val calendar = Calendar.getInstance()
        calendar.setTime(date)
        return calendar.get(Calendar.DAY_OF_MONTH) == 1
    }

    fun  clearAllSinMsg(agent: Agent){
        //删除所有跟签到有关的成就
        var user=UserService.getUserMsgByUID(agent.UID)
        println("清空累计签到信息"+user)
        if(user.medal2==null||user.medal2==0||user.medal2!=DayGet.getYearMonthDay()){
            AchievementUserService.deleteAchievementUser(AchievementUser(uid = agent.UID,atype="sign"))
            UserService.updateUserMsgByUID(User(uid=agent.UID,signTimes = 0,signCount = 0,medal2 = DayGet.getYearMonthDay()))
        }
    }


    //获取成就战争奖牌奖励金银铜
    fun Achievement_Award(agent: Agent, root : JSONObject){
        var aUser = AchievementUserService.selectAchievementUserOne(AchievementUser(atid =root["atid"] as Int
                , uid = agent.UID)) as AchievementUser?          // 获取用户此项成就信息
        if(aUser !=null){
            AchievementUserService.updateAchievementUser(AchievementUser(uid=agent.UID,atid=root["atid"] as Int,teps=(aUser.teps+root["count"] as Int),time = Get_This_DayNum()))  //修改成就奖牌获取数量
        }else{
            AchievementUserService.insertAchievementUser(AchievementUser(uid = agent.UID,atid =root["atid"] as Int,
                    aname= root["goodsType"] as String ,teps =root["count"] as Int,isCreated = 1,time = Get_This_DayNum())) //存入数据库 新的奖牌信息
        }
        Send(agent, RoomController.Msg(1,detail="",data=root["data"],command =  root["command"]))
    }
    //获得奖品
    fun Into_Sign(agent: Agent, root : JSONObject){
        var aUser=AchievementUserService.selectAchievementUserOne(AchievementUser(uid=agent.UID,atid=root["atid"] as Int))
        if(aUser ==null||aUser.isCreated==1) {
            Send(agent, RoomController.Msg(0,detail="",data=root["data"],command =  root["command"]))
            return
        }
        StoreService.Get_Prize(agent,root,"onSign")   //领取奖励
        AchievementUserService.updateAchievementUser(//根据签到或者领取成就的  成就模板id 来修改模板领取情况
                AchievementUser(uid=agent.UID,atid =root["atid"] as Int,isCreated = 1,time = Get_This_DayNum())
        )
        var res= PrizeService.selectPrizeByTempId(root["atid"] as Int)
        Send(agent, RoomController.Msg(1,detail=res,data=root["data"],command =  root["command"]))
    }

    //获得奖品
    fun Into_CoinOrGem(agent: Agent, root : JSONObject){
        var aUser=AchievementUserService.selectAchievementUserOne(AchievementUser(uid=agent.UID,atid=root["atid"] as Int))
        if(aUser ==null||aUser.isfulfill!=1||aUser.isCreated==1)
        {
            Send(agent, RoomController.Msg(0,detail="",data=root["data"],command = root["command"]))
            return
        }
        StoreService.Get_Prize(agent,root,aUser.atype)
        if("day".equals(aUser.atype)){
        AchievementUserService.updateAchievementUser(     //根据签到或者领取成就的  成就模板id 来修改模板领取情况
                AchievementUser(uid=agent.UID,atid =root["atid"] as Int,isCreated = 1,isfulfill = 1,time = Get_This_DayNum()))
        }else{
            AchievementUserService.updateAchievementUser(  //根据签到或者领取成就的  成就模板id 来修改模板领取情况
                    AchievementUser(auid = aUser.auid,uid=agent.UID,isCreated = 1,isfulfill = 1,time = Get_This_DayNum()))
            var aTemp= AchievementTempService.selectAtempByteps(AchievementTemp(triggerType = aUser.triggerType,teps=aUser.teps))
            if(aTemp !=null)
           aTemp.let {
               var aUse=AchievementUser(uid = agent.UID
                       , atid = it.atid, aname = it.name, teps =aUser.teps-aUser.nextTeps,atype = it.type
                       , triggerType = it.triggerType, isCreated = 0,isfulfill = 0, time = Get_This_DayNum(),nextTeps = it.teps)
               if("online".equals(aUser.atype)){
                   aUse.isfulfill=1
                   aUse.teps= it.teps
               }
               AchievementUserService.insertAchievementUser(aUse)
           }
        }
        var res= PrizeService.selectPrizeByTempId(root["atid"] as Int)
        Send(agent, RoomController.Msg(1,detail=res,data=root["data"],command = root["command"]))
        if("online".equals(aUser.atype)){
            onlineGet(agent,root)
        }
    }
    //获取当前时间
    fun Get_This_DayNum():Int{
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        return  calendar.get(Calendar.DAY_OF_MONTH)
    }
    fun Send(agent: Agent,msg:String)=agent.Send(agent.CID,msg)
}