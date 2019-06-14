package com.shine.controller.aHall

import com.shine.agent.Agent
import com.shine.amodel.*
import com.shine.aservice.achievement.AchievementUserService
import com.shine.aservice.achievement.DailyAchievement.Charm_Count
import com.shine.aservice.achievement.DailyAchievement.Prop_Use
import com.shine.aservice.achievement.OrdinaryAchievement.Use_Achievements
import com.shine.aservice.army.ArmyUserService
import com.shine.aservice.shop.FmccService
import com.shine.aservice.shop.GoodsService
import com.shine.aservice.shop.StoreService
import com.shine.aservice.shop.UseHistoryService
import com.shine.aservice.user.UserService
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * 背包系统
 */
object StoreController {
    fun HandleAll(agent: Agent, root: JSONObject){
        var detail=root.get("data") as String
        var user= UserService.getUserMsgByUID(agent.UID)!! as User   //获取用户信息从数据库里
        when(detail){
            "use"       ->Items_Use(agent,root,user)       //使用道具
            "sell"      ->Items_Sell(agent,root,user)     //出售
            "iner"      ->Items_Iner(agent)              //进入背包
            "unfix"     ->Take_Off(agent,root,user)      //脱衣服
            "useMedal"  ->useMedal(agent,root)         //使用徽章
//            "unMedal"   ->takeOffMedal(agent,root)      //换勋章
            "selectMedal"->selectMedal(agent,root)     //查看佩戴 徽章根据uid
            "usehistory"->userHistory(agent,root)           //根据用户id 查看用户礼物使用记录
            "userBackground"->seleceBackground(agent,root)  //根据用户id 查看用户背景与挂件
            else        ->return
        }
    }

    /**
     * 查询勋章
     * 参数：uid
     */
    fun  selectMedal(agent: Agent, root: JSONObject){
        if(!root.has("uid")) return
        var store= StoreService.storeGetByState(Store(uid=root["uid"] as Int,goodsType = "700000",goodsState = 1))  //查询所有佩戴的勋章
        Send(agent, RoomController.Msg(1,detail=store,data=root["data"],command = root["command"]))
    }
    /**
     * 查看使用中的背景与挂件
     */
    fun  seleceBackground(agent: Agent, root: JSONObject){
        if(!root.has("uid")) return
        var allStore= mutableListOf<Store>()
        var background= StoreService.storeGetByState(Store(uid=root["uid"] as Int,goodsType = "660000",goodsState = 1))  //查询佩戴的背景
        var pendant= StoreService.storeGetByState(Store(uid=root["uid"] as Int,goodsType = "670000",goodsState = 1))  //查询佩戴的挂件
        allStore.addAll(background)
        allStore.addAll(pendant)
        Send(agent, RoomController.Msg(0,detail=allStore,data=root["data"],command = root["command"]))
    }
//    fun takeOffMedal(agent: Agent, root: JSONObject){
//        var goods= GoodsService.goodsSelectOneByGoods(Goods(commodityId = root["commodityId"] as Int))  //查询商品信息
//        var fmcc=FmccService.selectFmccByType(goods.otherType).toMutableList()
//        if(goods.commodityId==0) return
//        StoreService.updateGoodsCount(Store(uid=agent.UID,goodsState = 0,commodityId =goods.commodityId))
//        FmccService.deleteFmcc(Fmcc(uid=agent.UID,commodityId =goods.commodityId ))
//        Send(agent, RoomController.Msg(0,detail=fmcc,data=root["data"],command = root["command"]))
//    }

    /**
     * 修改用户徽章状态
     */
    fun updateStoreState(user:User,root: JSONObject,priority:Int){
        StoreService.updateStroeState(Store(uid=user.uid,priority = priority))  //卸下徽章
        if(root.has("commodityId")&&root["commodityId"] as Int!=0)
            StoreService.updateGoodsCount(Store(uid=user.uid,goodsState = 1,commodityId =root["commodityId"] as Int,priority = priority))  //穿戴最新徽章
    }
    fun useMedal(agent: Agent, root: JSONObject){
        if(!root.has("site"))  return
        when(root["site"] as Int){
            1->{
                updateStoreState(agent.user,root,1)
            }
            2->{
                updateStoreState(agent.user,root,2)
            }
            3->{
                updateStoreState(agent.user,root,3)
            }
        }
        Send(agent, RoomController.Msg(0,detail="success",data=root["data"],command = root["command"]))
    }
    //获取用户使用礼物历史纪录表
    fun userHistory(agent: Agent, root: JSONObject){
        Send(agent, RoomController.Msg(1,detail=UseHistoryService.selectUserHistory(UseHistory(uid=root["uid"]as Int)),data="usehistory",command = "hall_store"))
    }
    //脱下时装
    fun  Take_Off(agent: Agent, root: JSONObject,user: User){
        FmccService.deleteFmcc(Fmcc(commodityId=root["commodityId"] as Int,uid=agent.UID))  //删除时装加成buf
        StoreService.updateGoodsCount( Store(uid=agent.UID,commodityId=root["commodityId"] as Int,goodsState = 0))
        var goods= GoodsService.goodsSelectOneByGoods(Goods(commodityId = root["commodityId"] as Int))  //查询商品信息
        UserService.updateUserMsgByUID(User(uid=agent.UID,expFashion =
        if(((user?.expFashion?:0)-goods.attribute.toInt())<=0)
            0
        else
            ((user?.expFashion?:0)-goods.attribute.toInt())))
        Send(agent, RoomController.Msg(1,detail=get_goods_msg(agent,root["commodityId"] as Int),data="unfix",command = "hall_store"))
    }
    //获取背包信息
    fun  Items_Iner(agent: Agent){
        var stores=StoreService.storeGet(agent.UID)
        Send(agent, RoomController.Msg(1,detail=stores,data="iner",command = "hall_store"))
    }
    //道具出售
    fun Items_Sell(agent: Agent, root: JSONObject,user:User){
        var goods= GoodsService.goodsSelectOneByGoods(Goods(commodityId = root["commodityId"] as Int))   //获取商品信息
        //出售金额
        var users=Any()
        when(goods.currency){
            "gem"   ->  {
                users = User(uid=agent.UID,
                        coin=
                        ((((goods.price*30000) * root["count"] as Int) * 0.5)+user.coin).toLong(),
                        medal0 =
                        (user.medal0?:0)+((goods.price*30000) * root["count"] as Int * 0.5).toInt()
                )
            }
            "coin"  ->  {
                users = User(uid=agent.UID,
                        coin= (((goods.price * root["count"] as Int) * 0.5)+user.coin).toLong(),
                        medal0=(user.medal0?:0)+(goods.price * root["count"] as Int * 0.5).toInt()
                )
            }
            else ->return
        }
        var result=1
        //修改背包
        if(StoreService.Buy_Out_Backpacker(agent,root,"delete") ) {
//           users=DailyAchievement.consumeTemp(,users)   //计数累计消费 获得勋章
            UserService.updateUserMsgByUID(users)  //修改用户剩余金额
            Send(agent, RoomController.Msg(result,detail=get_goods_msg(agent,root["commodityId"] as Int),data="sell",command = "hall_store"))
        } else {
            result = 0
            Send(agent, RoomController.Msg(result,detail=get_goods_msg(agent,root["commodityId"] as Int),data="sell",command = "hall_store"))
        }
    }
    fun  get_goods_msg(agent: Agent,commodityId:Int):List<Store>{
        var list= mutableListOf<Store>()
        list.add(StoreService.storeGetOne(Store(uid=agent.UID,commodityId = commodityId)))
        return list
    }
    //道具使用
    fun Items_Use(agent: Agent, root: JSONObject,user:User){
        if (!root.has("commodityId")||root["commodityId"] as Int <=1) return
        var goods= GoodsService.goodsSelectOneByGoods(Goods(commodityId = root["commodityId"] as Int)) //查询商品信息
        //成就触发
        Prop_Use(agent,goods.buffType,user)

        //使用聚宝盆成就触发
        Use_Achievements(agent,user,goods.commodityId,if (!root.has("count")) 1 else root["count"] as Int)

        //根据使用商品的buffType来判断什么类型的道具来触发不同的每日成就
        if(goods.otherType.equals("400000")){        //如果使用的是礼物
            if(!StoreService.Buy_Out_Backpacker(agent,root)) {
                Send(agent, RoomController.Msg(0,detail="",data="use",command = "hall_store"))
                return
            }
//            Use_Goods_Charm(agent,goods,user,root)
            Charm_Count(agent,goods.attribute.toInt(),user) //检查魅力值获取成就
            var users=User(uid=agent.UID,expFashion = ((user.expFashion?:0)+goods.attribute.toInt()))
            UserService.updateUserMsgByUID(users)
            UseHistoryService.insertUseHistory(UseHistory(uid=agent.UID,goodsType = goods.otherType,name=goods.name,commodityId = goods.commodityId))
            Send(agent, RoomController.Msg(1,detail=get_goods_msg(agent,root["commodityId"] as Int),data="use",command = "hall_store"))
            return
        }
        //修改背包
        if(!StoreService.Buy_Out_Backpacker(agent,root)) {
            Send(agent, RoomController.Msg(0,detail="",data="use",command = "hall_store"))
            return
        }
        when(goods.buffType){
            "exp"           ->Use_Goods(agent,goods)
            "gold"          ->Use_Goods(agent,goods)
            "all"           ->Use_Goods(agent,goods)
            "order"         ->Use_Goods(agent,goods)
            "science"       ->Use_Goods(agent,goods)
            "sign"          ->day_sign(agent,goods)      //使用补签卡
            "name"          ->useNameCard(agent,root)
            "charm"         ->Use_Goods_Charm(agent,goods,user,root)    //衣服类型魅力加成道具使用
            else ->return
        }
    }
    fun  useNameCard(agent: Agent,root:JSONObject){
        var name=root["name"].toString()
//        println(name)
        UserService.updateUserMsgByUID(User(uid=agent.UID,nick=name))
        ArmyUserService.updateArmyUser(ArmyUser(uid=agent.UID,name=name))
        agent.user.name=name
        var user=UserService.getUserMsgByUID(agent.UID)
        Send(agent, RoomController.Msg(1,detail= mutableListOf(user),data="use",command = "hall_store"))
        Send(agent, RoomController.Msg(1,detail=get_goods_msg(agent,root["commodityId"] as Int),data="use",command = "hall_store"))
    }
    fun day_sign(agent: Agent,goods:Goods){
        UserService.updateUserMsgByUID(User(uid=agent.UID,signTimes = 1))
        AchievementUserService.deleteAchievementUser(AchievementUser(uid = agent.UID, atype = "day",triggerType = "oneSign"))
        Send(agent, RoomController.Msg(1,detail=get_goods_msg(agent,goods.commodityId),data="use",command = "hall_store"))
    }
    //经验加成道具
    fun Use_Goods(agent: Agent,goods:Goods){
        var fmcc= Fmcc(commodityId=goods.commodityId,uid=agent.UID)
        var f=FmccService.selectFmccOne(fmcc)
        if(f==null){
            //没有这个加成 想Fmcc表里存入加成信息
            var newFmcc= Fmcc(commodityId=goods.commodityId,uid=agent.UID,goodsType =goods.otherType,
                    type=goods.buffType,outDate = goods.outDate,
                    createUser= agent.UID,addition = goods.attribute,outDateType = goods.outDateType)

            if(!"num".equals(goods.otherType)&&goods.outDate!=0) {
                newFmcc.closingTime = get_closing_time(goods.outDate)
            }
//            println("加成时间a"+newFmcc.closingTime+">>>>"+newFmcc.outDateType)
            FmccService.insertFmcc(newFmcc)
        }else{
            //存在同样的buf修改加成时间
            f.outDate=(get_closing_time(f.closingTime)+goods.outDate)
            f.closingTime= get_closing_time(f.outDate?:0,f.closingTime)
//            println("加成时间b"+f.closingTime)
            FmccService.updateFmcc(f)
        }
        Send(agent, RoomController.Msg(1,detail=get_goods_msg(agent,goods.commodityId),data="use",command = "hall_store"))
    }

    //获取剩余时间
    fun get_closing_time(thisDate:String) :Int{
        var sp= SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date=  if("".equals(thisDate)) Date() else  sp.parse(thisDate)
        var time= (date.time - Date().time)
        val hours = (time/1000/60/60).toInt()
        return hours
    }
    //获取新增时间
    fun get_closing_time(outDate:Int,thisDate:String) :String{
        var sp= SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val calendar = Calendar.getInstance()
        calendar.setTime(sp.parse(thisDate))
        calendar.add(Calendar.HOUR_OF_DAY,outDate)
        return sp.format(calendar.time).toString()
    }

    //获取预计到期时间
    fun get_closing_time(outDate:Int) :String{
        var sp= SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val calendar = Calendar.getInstance()
        calendar.setTime(Date())
        calendar.add(Calendar.HOUR_OF_DAY,outDate)
        return sp.format(calendar.time).toString()
    }
    //    //金币加成道具
//    fun Use_Goods_Gold(agent: Agent,goods:Goods,user:User){
//        UserService.updateUserMsgByUID(User(uid=agent.UID,coin=(goods.attribute.toInt()+user.coin))) //修改用户金币数量
//        Send(agent, RoomController.Msg(1,detail="",data="use",command = "hall_store"))
//    }
    //服装加成道具使用
    fun Use_Goods_Charm(agent: Agent,goods:Goods,user:User,root: JSONObject){
        if(!"400000".equals(goods.otherType)){
            if(goods.goodsSex!=agent.user.sex&&goods.goodsSex!=0) {
                StoreService.updateGoodsCount(Store(uid=agent.UID,commodityId = goods.commodityId,goodsState = 0))
                Send(agent, RoomController.Msg(1, detail = "sexError", data = "use", command = "hall_store"))
                return
            }
        }
        var f=FmccService.selectFmccOne(Fmcc(uid=agent.UID,goodsType = goods.otherType))
        if(f==null){
            //没有这个加成 想Fmcc表里存入加成信息
            FmccService.insertFmcc(Fmcc(commodityId = goods.commodityId,uid=agent.UID,goodsType = goods.otherType
                    ,type=goods.buffType,status = 1,outDate = goods.outDate,createUser = agent.UID,addition = goods.attribute))
            UserService.updateUserMsgByUID(User(uid=agent.UID,expFashion =((user?.expFashion?:0)+goods.attribute.toInt()) ))
        }else{
            //如果同类型商品存在 覆盖商品
            FmccService.updateFmcc(Fmcc(fid=f.fid,commodityId = goods.commodityId,outDate = goods.outDate,addition = goods.attribute))
            UserService.updateUserMsgByUID(User(uid=agent.UID,expFashion =((user?.expFashion?:0)+goods.attribute.toInt()-f.addition.toInt())))
        }
        Send(agent, RoomController.Msg(1,detail=get_goods_msg(agent,goods.commodityId),data="use",command = "hall_store"))
    }


    fun Send(agent: Agent,msg:String)=agent.Send(agent.CID,msg)
}