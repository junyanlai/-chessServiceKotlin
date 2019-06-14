package com.shine.controller.aHall

import com.shine.agent.Agent
import com.shine.amodel.AchievementTemp
import com.shine.amodel.AchievementUser
import com.shine.amodel.Prize
import com.shine.amodel.User
import com.shine.aservice.achievement.AchievementTempService
import com.shine.aservice.achievement.AchievementUserService
import com.shine.aservice.prize.PrizeService
import com.shine.aservice.shop.StoreService
import com.shine.aservice.user.UserService
import com.shine.aservice.util.DayGet.Get_This_DayNum
import org.apache.log4j.Logger
import org.json.JSONObject
/**
 * 奖励系统
 */
object AchievementController {

    var logger = Logger.getLogger("AchievementController")
    fun HandleAll(agent: Agent, root: JSONObject){
        var detail=root.get("data") as String
        when(detail){
            "sign"          ->Sign_Insert(agent,root)       //签到
             else            ->{
                 logger.error(String.format("未知命令:%s%s",root["data"],root["command"]))
                return
            }
        }
    }

    //查询所有类型参数
    fun Achievement_Msg(agent:Agent, root:JSONObject){
        var aTempList= AchievementTempService.selectAchievementTemp(AchievementTemp(type=root["type"] as String ))  //查询所有type 类型的成就奖励
        Send(agent, RoomController.Msg(1,detail=aTempList,data="inner",command = "hall_attendance"))
    }

    //每日签到
    fun Sign_Insert(agent:Agent, root:JSONObject){
        var result =0
        var user= UserService.getUserMsgByUID(agent.UID)!! as User   //获取用户信息
        //如果已签到直接返回
       if(user.signTimes==Get_This_DayNum()){
            Send(agent, RoomController.Msg(2,detail=(user?.signCount?:0),data="sign",command = "hall_attendance"))
            return
        }
        var aTemp= AchievementTempService.selectAchievementTempOne(AchievementTemp(type="sign",
                teps =( (user?.signCount?:0)+1)))   as AchievementTemp?  //签到模板信息
        var dayTemp= AchievementTempService.selectAchievementTempOne(AchievementTemp(type="oneSign",
                triggerType = "oneSign"))   //签到模板信息
        if(aTemp !=null) {
            var aUser = AchievementUserService.selectAchievementUserOne(AchievementUser(atid = aTemp!!.atid
                    , uid = agent.UID)) as AchievementUser?          //获取用户成就信息
            if(aUser==null){
                result=1
                AchievementUserService.insertAchievementUser(AchievementUser(uid=agent.UID,atid = aTemp.atid,
                        atype = aTemp.type,teps=aTemp.teps,isCreated = 0,aname =aTemp.name,time =Get_This_DayNum(),isfulfill = 1,triggerType = aTemp.triggerType,nextTeps = aTemp.teps))  //存入用户成就表
            }else if( aUser!=null&&aUser.isCreated==0){
                result=1
            }
        }else{
            AchievementUserService.insertAchievementUser(AchievementUser(uid=agent.UID,atid = dayTemp.atid,
                    atype = dayTemp.type,teps=1,isCreated = 1,aname =dayTemp.name,time =Get_This_DayNum(),isfulfill = 1,triggerType = dayTemp.triggerType,nextTeps = dayTemp.teps))  //存入用户成就表
        }

        UserService.updateUserMsgByUID(User(uid = agent.UID,signCount =(user?.signCount?:0)+1,signTimes =Get_This_DayNum() ))  //修改用户签到次数与签到时间
        root.put("atid",dayTemp.atid)
        StoreService.Get_Prize(agent,root,"")   //奖励存入用户信息
        var res=PrizeService.selectPrizeByTempId(dayTemp.atid)
        var results= mutableListOf<Prize>()
        for(i in res){
            i.signCount=(user?.signCount?:0)+1
            i.signTimes=Get_This_DayNum()
            results.add(i)
        }
        Send(agent, RoomController.Msg(result,detail=results,data="sign",command = "hall_attendance"))
    }

    fun Send(agent: Agent,msg:String)=agent.Send(agent.CID,msg)
}