package com.shine.controller.aHall
import com.shine.agent.Agent
import com.shine.amodel.Goods
import com.shine.amodel.Store
import com.shine.amodel.User
import com.shine.aservice.achievement.DailyAchievement.Prop_Shop
import com.shine.aservice.achievement.OrdinaryAchievement.Buy_Goods
import com.shine.aservice.achievement.OrdinaryAchievement.User_useMoney
import com.shine.aservice.shop.GoodsService
import com.shine.aservice.shop.StoreService
import com.shine.aservice.user.UserService
import com.shine.controller.aHall.EmailController.Handle_OnSendMail
import org.json.JSONObject

/**
 * 商城系统
 */
object GoodsShopController {
    fun HandleAll(agent: Agent, root: JSONObject){
        var detail=root.get("data") as String
        when(detail){
            "iner"      ->Handle_GetGoods(agent) //查看商城信息
            "buy"       ->Handle_BuyGoods(agent,root) //购买
            "send"      ->Handl_send_goods(agent,root) //赠送
            else        ->return
        }
    }
    fun Handl_send_goods(agent:Agent,root:JSONObject){
        var goods= GoodsService.goodsSelectOneByGoods(Goods(commodityId = root["commodityId"] as Int))   //商品信息
        var user= UserService.getUserMsgByUID(agent.UID)   //获取用户信息从数据库里
        var count=  if (!root.has("count")) root["count"] as Int else 0
        var userMoney=0L
        var use=User(uid=agent.UID)
        when(goods.currency){
            "gem"  ->userMoney=user.gem
            "coin" ->userMoney=user.coin
        }
        if(userMoney<(goods.price *count)){
            Send(agent, RoomController.Msg(0,detail="",data=root["data"],command = root["command"]))
            return
        }
        when(goods.currency){
            "gem"  -> use.gem=user.gem-(goods.price*count)
            "coin" -> use.coin=user.coin-(goods.price*count)
        }
        UserService.updateUserMsgByUID(use)
        Handle_OnSendMail(agent,root)
        Send(agent, RoomController.Msg(1,detail="",data=root["data"],command = root["command"]))
    }

    //获取商品
    fun Handle_GetGoods(agent: Agent){
        var data= GoodsService.getGoodsByCommon()
        Send(agent, RoomController.Msg(1,detail=data,data="iner",command = "hall_goods"))
    }
    fun Handle_BuyGoods(agent: Agent, root:JSONObject){
        var user= UserService.getUserMsgByUID(agent.UID)!! as User   //获取用户信息从数据库里
        if(root["commodityId"] as Int ==null&&root["commodityId"] as Int ==0) return
        var goods= GoodsService.goodsSelectOneByGoods(Goods(commodityId = root["commodityId"] as Int))  //商品信息
        //商品类别
        when(goods.currency){
            "coin" ->Buy_Coin(agent,user,goods,root)    //金币
            "gem"  ->Buy_Gem(agent,user,goods,root)    //钻石
        }
        //购买触发成就
        Prop_Shop(agent,goods,user)

        //购买
        Buy_Goods(agent,user,root["count"] as Int,goods.otherType)
    }
    fun Bag_Is_Full(agent:Agent,goodsType:String,user:User):Int{
        //获取商品再背包里的数量
        var count = StoreService.selectStoreCount(Store(uid=agent.UID,goodsType = goodsType))
        when{
            user.packSize>count ->return 1
            user.packSize<count ->return 0
        }
        return 1
    }
    fun buy_count(agent: Agent,commodityId:Int):Boolean {
        var store=StoreService.storeGetOne(Store(uid = agent.UID,commodityId=commodityId))
        return  if(store.goodsCount==99) false else true
    }
    fun Buy_Gem(agent:Agent,user: User, goods: Goods,root:JSONObject){
        var money =(user.gem.toInt()-(goods.price*root["count"] as Int)).toLong()
//        println("用户的 钻石数量："+user.gem)
        if(money >=0){
            if(Bag_Is_Full(agent,goods.otherType,user)==0&&buy_count(agent,goods.commodityId)) {
                Send(agent, RoomController.Msg(0,detail="",data="buy",command = "hall_goods"))
                return
            }
            var users=User(uid=agent.UID,gem=money)
            if("coin".equals(goods.buffType)){
                users.coin=(goods.attribute.toInt()+user.coin)
            }
            var updatResult=UserService.updateUserMsgByUID(users)  //修改用户剩余金额
//            println("购买后剩余"+money)
            //背包存入
            StoreService.Buy_Into_Backpacker(agent,goods,root,null)
            Send(agent, RoomController.Msg(1,detail="",data="buy",command = "hall_goods"))
        }else{
            Send(agent, RoomController.Msg(0,detail="",data="buy",command = "hall_goods"))
        }
    }
    fun Buy_Coin(agent:Agent,user: User, goods: Goods,root:JSONObject){
        var money =(user.coin.toInt()-(goods.price*root["count"] as Int)).toLong()
        var result=1
        if(money >=0){
            if(Bag_Is_Full(agent,goods.otherType,user )==0) {
                result = 0
                Send(agent, RoomController.Msg(result,detail=money,data="buy",command = "hall_goods"))
                return
            }
//            println("购买后剩余"+money)
            var users=User(uid=agent.UID,coin=money,medal0 =((user.medal0?:0)+goods.price).toInt())
            User_useMoney(agent,users,(goods.price*root["count"] as Int))  //统计累计消费
            UserService.updateUserMsgByUID(users)  //修改用户剩余金额
            //背包存入
            StoreService.Buy_Into_Backpacker(agent,goods,root,null)
            Send(agent, RoomController.Msg(result,detail="",data="buy",command = "hall_goods"))
        }else{
            result=0
            Send(agent, RoomController.Msg(result,detail="",data="buy",command = "hall_goods"))
        }
    }


    fun Send(agent: Agent,msg:String)=agent.Send(agent.CID,msg)
}