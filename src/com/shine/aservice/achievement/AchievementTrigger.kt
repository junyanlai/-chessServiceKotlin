package com.shine.aservice.achievement

import com.shine.Config
import com.shine.agent.Agent
import com.shine.amodel.*
import com.shine.aservice.recharge.impl.TopBufferTableServiceImpl
import com.shine.aservice.shop.GoodsService
import com.shine.aservice.shop.RechargeService
import com.shine.aservice.shop.StoreService
import com.shine.aservice.user.UserService
import com.shine.aservice.util.DayGet.Get_This_DayNum
import com.shine.aservice.util.RechargeUtil
import com.shine.controller.aHall.RoomController
import com.shine.controller.poker.Landlords.tool.JSONTool
import org.json.JSONObject

object AchievementTrigger {


    fun choseMethod(agent:Agent,root:JSONObject){
        if (!root.has("data")) return
        when(root["data"] as String){
            "1"-> payResults(agent,root)
            "2"-> payError(agent,root)
        }

    }

    /**
     * 充值暂存   参数 ： autoCode facTradSeq method
     */
    fun payError(agent:Agent,root:JSONObject){
        if (!root.has("authCode")&&!root.has("facTradeSeq")&&!root.has("customerId")&&!root.has("tradeSeq")) return
        var authCode= root["authCode"] as String
        var facTradeSeq=root["facTradeSeq"] as String
        var customerId=root["customerId"] as String
        var tradeSeq=root["tradeSeq"] as String
       when(root["method"]){
           "start" ->TopBufferTableServiceImpl.insertTop(TopBufferTable(uid=agent.user.uid,authCode =authCode,facTradeSeq = facTradeSeq,customerId =customerId,tradeSeq =tradeSeq  ))
           "end"-> TopBufferTableServiceImpl.deleteTop(TopBufferTable(facTradeSeq = facTradeSeq))
       }

    }

    fun payResults(agent:Agent,root:JSONObject){
        if (!root.has("authCode")) return
        var res= RechargeUtil.sendHttp(root["authCode"] as String, Config.URL_FOR_TradeQuery)
            res.customerId=root["customerId"] as String
            res.TradeSeq=root["tradeSeq"] as String
        println("充值请求回值："+ JSONTool.toJson(res))
        var roos=JSONObject()
        if(res.PayResult=="3"||"3".equals(res.PayResult)){
            RechargeUtil.sendHttp(root["authCode"] as String, Config.URL_FOR_PaymentConfirm)  //发送确认码到mycard
            roos.put("gem",(agent.user.gem+(res.Amount.toInt()*10)))
            roos.put("gold",(agent.user.coin))
            Send(agent, RoomController.Msg(1, detail = roos, data = root["data"], command = "hall_user"))
            vip_grade(agent,res.Amount.toInt(),res)
            TopBufferTableServiceImpl.deleteTop(TopBufferTable(facTradeSeq = res.FacTradeSeq))
        }else{
            roos.put("gem",(agent.user.gem))
            roos.put("gold",(agent.user.coin))
            Send(agent, RoomController.Msg(0, detail = roos,  data = root["data"], command = "hall_user"))
        }
    }
    //获取VIP称号
    fun vip_grade(agent:Agent,money:Int,res:ResultMsg){
        var user=UserService.getUserMsgByUID(agent.UID)
        var msg =RechargeService.selectRechargeOneForCard(Recharge(uid=user.uid))   //查询充值记录+
        var aUser = AchievementUserService.selectAchievementUserOne(AchievementUser(uid = user.uid, atype = "achievement",triggerType = "vip",isCreated=0)) //获取用户每日金币获取信息
        var isFull=0
        if(msg==null) {//第一次充值
            var aTemp=AchievementTempService.selectAchievementTempByTeps(AchievementTemp(triggerType= "vip",teps=(money)))//查询第一次充值是否达成vip等级评定
            if(aTemp!=null){  //有信息 说明达成vip等级
                UserService.updateUserMsgByUID(User(uid=user.uid,vipLevel = aTemp.atid,coin=user.coin+aTemp.award))    //修改用户vip 等级id 与达成后奖励金币
                isFull=1
//                insMedal(aTemp,user)
                Send(agent, RoomController.Msg(1, detail = "", data = "achievement", command = "achievement"))

            }
            AchievementUserService.updateAchievementUser(AchievementUser(uid=user.uid,atid=if(aUser==null||aUser.equals("")) 3 else aUser.atid
                    ,time = Get_This_DayNum(),teps=money+if(aUser==null||aUser.equals("")) 0 else aUser.teps,isfulfill = isFull))     //存入用户成就表
        }else{ //如果不是第一次充值
            var aTemp=AchievementTempService.selectAchievementTempByTeps(AchievementTemp(triggerType= "vip",teps=(money+msg.accruingAmounts)))//查询充值是否达成vip等级评定
            if(aTemp!=null){ //达成
//                insMedal(aTemp,user)   //获得勋章
                UserService.updateUserMsgByUID(User(uid=user.uid,vipLevel = aTemp.addLevel,coin=user.coin+aTemp.award))
                isFull=1
                Send(agent, RoomController.Msg(1, detail = "", data = "achievement", command = "achievement"))
            }
            AchievementUserService.updateAchievementUser(AchievementUser(uid=user.uid,atid=aUser.atid
                    ,time = Get_This_DayNum(),teps=money+aUser.teps,isfulfill = isFull))     //存入用户成就表
        }
//        println("查询充值记录"+msg)
        var rechart=Recharge(
                uid = user.uid
                , money = money
                , accruingAmounts =(if(msg==null||msg.equals("")) 0 else msg.accruingAmounts) + money
                , device=user.device
                ,tradeSeq = res.TradeSeq
                , facTradeSeq = res.FacTradeSeq
                , payMentType = res.PaymentType
                , currency = res.Currency
                , myCardTradeNo = res.MyCardTradeNo
                , myCardType = res.MyCardType
                , promoCode = res.PromoCode
                , serialld = res.Serialld
                ,customerId = res.customerId)
//        println("充值记录存入信息："+rechart)
        RechargeService.insertRecharge(rechart) //存入充值记录表
        UserService.updateUserMsgByUID(User(uid=user.uid,gem=user.gem+(money*10)))    //修改用户vip 等级id 与达成后奖励金币
        Send(agent, RoomController.Msg(1, detail =user, data = "payResults", command = "payResults"))   //充值成功后修改用户的剩余钻石数量
    }

    fun  insMedal(aTemp:AchievementTemp,user:User){
        var goods= GoodsService.goodsSelectOneByGoods(Goods(type="medal",price=aTemp.teps))   //获取徽章信息
        StoreService.goodsAdd(Store(uid=user.uid,
                name=goods.name,
                detail=goods.detail,
                commodityId = goods.commodityId,
                goodsCount=1,
                goodsState = 1,
                goodsType = goods.otherType
        ))
    }
    fun Send(agent: Agent,msg:String)=agent.Send(agent.CID,msg)
}