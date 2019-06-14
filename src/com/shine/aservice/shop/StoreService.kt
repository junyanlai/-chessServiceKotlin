package com.shine.aservice.shop
import com.shine.agent.Agent
import com.shine.amodel.*
import com.shine.aservice.achievement.AchievementTempService
import com.shine.aservice.prize.PrizeService
import com.shine.aservice.user.UserService
import com.shine.dao.StoreDao
import org.json.JSONObject

object StoreService:IStoreService {


    override fun updateStroeState(store: Store): Int {
        return StoreDao().updateStroeState(store)
    }

    //所有不可删除类代码
    val mapUser= listOf("300000","320000","340000","360000","380000","660000","670000")

    override fun storeGetByState(store: Store): List<Store> {
        return StoreDao().storeGetByState(store)
    }

    //添加商品
    @Throws
    override fun goodsAdd(store: Store): Int {
        var r=0
        try {
            r=StoreDao().goodsAdd(store) as Int
        }catch (ex:Exception){
            throw Exception(ex.message)
        }
        return r
    }
    //查询商品
    override fun storeGet(uid: Int): List<Store> {
        return StoreDao().storeGet(uid)
    }
    //    override fun storeGets(uid: Int): List<Map<String,Any>> {
//        return StoreDao().storeGets(uid)
//    }
//获取商品数量
    override fun selectStoreCount(store: Store): Int {
        return StoreDao().selectStoreCount(store)
    }

    //查询单一商品
    override fun storeGetOne(store: Store): Store {
        return StoreDao().storeGetOne(store)
    }

    //修改商品数量
    @Throws
    override fun updateGoodsCount(store: Store): Int {
        var r=0
        try {
            r= StoreDao().updateGoodsCount(store) as Int
        }catch (ex:Exception){
            throw Exception(ex.message)
        }
        return r
    }
    //删除商品
    @Throws
    override fun deleteStore(store: Store){
        try {
            StoreDao().deleteStore(store)
        }catch (ex:Exception){
            throw Exception(ex.message)
        }
        return
    }

    //宝箱系统/签到奖励 领取接口
    override  fun Get_Prize(agent: Agent, root : JSONObject,atype:String){
        var user= UserService.getUserMsgByUID(agent.UID)!!   //获取用户信息从数据库里
        var prizeList=PrizeService.selectPrizeByTempId(root["atid"] as Int)
        var add=AchievementTempService.selectAchievementTempOne(AchievementTemp(atid=user.vipLevel))
        var addSize=0.0
        if(add!=null){
            when(atype){
                "day"->    addSize=add.addDaily
                "online"-> addSize=add.addOnline
            }
        }
        var users= User()
        for(prize in prizeList){
            var goods= GoodsService.goodsSelectOneByGoods(Goods(commodityId = prize.commodityId))   //商品信息
            when(prize.goodsType){
                "gem"   -> {//钻石奖励
                    users = User(agent.UID, gem=(prize.award+user.gem))
                    if(addSize!=0.0){
                        users.gem=(users.gem * addSize).toLong()
                    }
                }
                "coin"  ->{//金币奖励
                    users = User(agent.UID, coin=prize.award+user.coin)
                    if(addSize!=0.0){
                        users.gem=(users.gem * addSize).toLong()
                    }
                }
                "prop"      ->{ //道具奖励
                    StoreService.Buy_Into_Backpacker(agent,goods,root,prize)
                }
                else        ->return
            }

            UserService.updateUserMsgByUID(users)  //修改用户剩余金额
        }
    }

    //购买或者接收赠送 存入背包
    override fun Buy_Into_Backpacker(agent: Agent, goods: Goods, root:JSONObject,prize: Prize?){
        var selectStore=Store(uid=agent.UID,commodityId=goods.commodityId)
        var count=if (prize ==null) root["count"] as Int else prize.award
        var store=StoreService.storeGetOne(selectStore)
        if(store==null){
            StoreService.goodsAdd(Store(uid=agent.UID,commodityId=goods.commodityId,
                    goodsCount = count,goodsType = goods.otherType,name=goods.name,detail=goods.detail,priority = 0))
        }else{
            StoreService.updateGoodsCount(Store(uid=agent.UID
                    ,commodityId=goods.commodityId
                    ,goodsCount = (store.goodsCount?:0)+count))
        }
    }
    //购买或者接收赠送 存入背包 重写
    override fun Buy_Into_Backpacker(agent: Agent,root:JSONObject){
        if (!root.has("commodityId")) return
        if (!root.has("count")) return
        var goods= GoodsService.goodsSelectOneByGoods(Goods(commodityId = root["commodityId"] as Int))   //商品信息
        when(goods.otherType){
            "110000"->{
                var user= UserService.getUserMsgByUID(agent.UID)!! as User   //获取用户信息从数据库里
                var users=User(uid=agent.UID,coin = root["count"] as Int+user.coin )
                UserService.updateUserMsgByUID(users)
                return
            }
           "120002"->{
               var user= UserService.getUserMsgByUID(agent.UID)!! as User   //获取用户信息从数据库里
               var users=User(uid=agent.UID,gem = root["count"] as Int+user.gem )
               UserService.updateUserMsgByUID(users)
               return
           }
            else->{
                var selectStore = Store(uid = agent.UID, commodityId = goods.commodityId)
                var count = root["count"] as Int
                var store = StoreService.storeGetOne(selectStore)
                if (store == null) {
                    StoreService.goodsAdd(Store(uid = agent.UID, commodityId = goods.commodityId,
                            goodsCount = count, goodsType = goods.otherType, name = goods.name, detail = goods.detail,priority = 0))
                } else {
                    StoreService.updateGoodsCount(Store(uid = agent.UID
                            , commodityId = goods.commodityId
                            , goodsCount = (store.goodsCount ?: 0) + count))
                }
                return
            }
        }
    }

    //使用 后修改背包物品
    override  fun Buy_Out_Backpacker(agent: Agent, root : JSONObject):Boolean{
        var selectStore=Store(uid=agent.UID,commodityId=root["commodityId"] as Int)
        var store=StoreService.storeGetOne(selectStore)
        var count=if (!root.has("count")) 1 else root["count"] as Int
        if(store==null) return false
        var gc=if (store.goodsCount!=null&&((store.goodsCount?:0)-count)>=0) ((store.goodsCount?:0)-count)
        else -1
        var r=true
        if(gc<0) {
            r= false
        }else{
            if(!store.goodsType.equals("")&&store.goodsType in mapUser ){
                StoreService.updateGoodsCount(Store(uid=agent.UID,goodsType = store.goodsType,goodsState = 0))
                var newStore =Store(uid=agent.UID
                        ,commodityId=root["commodityId"] as Int
                        ,goodsState = 1)
                StoreService.updateGoodsCount(newStore)
            }else {
                var newStore = Store(uid=agent.UID
                        , commodityId=root["commodityId"] as Int
                        , goodsCount = gc)
                StoreService.updateGoodsCount(newStore)
            }
        }
        return r
    }

    //出售 后删除背包物品
    override  fun Buy_Out_Backpacker(agent: Agent, root : JSONObject,handle:String):Boolean{
        var selectStore=Store(uid=agent.UID,commodityId = root["commodityId"] as Int)
        var store=StoreService.storeGetOne(selectStore)
        if(store==null) return false
        var count=if (!root.has("count")) 1 else root["count"] as Int
        var gc=if (((store.goodsCount?:0)-count)>=0) ((store.goodsCount?:0)-count)
        else -1
        var r=true
        if(gc<0) {
            r= false
        }else{
            var newStore = Store(uid=agent.UID
                    , commodityId=root["commodityId"] as Int
                    , goodsCount = gc)
            StoreService.updateGoodsCount(newStore)
        }
        return r
    }

    fun Send(agent: Agent,msg:String)=agent.Send(agent.CID,msg)
}