package com.shine.controller.aHall

import com.shine.agent.Agent
import com.shine.agent.SSF
import com.shine.amodel.*
import com.shine.aservice.army.*
import com.shine.aservice.notice.NoticeService.joinArmy
import com.shine.aservice.shop.FmccService
import com.shine.aservice.shop.GoodsService
import com.shine.aservice.user.UserService
import com.shine.controller.poker.Landlords.tool.JSONTool
import org.apache.commons.logging.LogFactory
import org.json.JSONObject
import redis.clients.jedis.Jedis
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

val log = LogFactory.getLog("rootLogger")

object ArmyController {

    fun HandleAll(agent: Agent, root: JSONObject) {//command,data,type,id
        if (!root.has("data")) return
        val data = root["data"] as String
        when (data) {
        //查看操作
            "inner" -> Army_Shop(agent, root)
            "techtree" -> Army_Tech_Tree(agent, root)
            "workshop" -> Army_workshop(agent, root)
            "order" -> Army_workshop_order(agent, root)
        //升级操作
            "addtree" -> Army_addtree_fmcc(agent, root)
            "addorder" -> Army_addorder_fmcc(agent, root)
            "addbuild" -> Army_addbuild_fmcc(agent, root)
        //修改订单星级
            "updatestart" -> Army_up_order(agent, root)
        //获取订单
            "getorder" -> Army_get_order(agent, root)
        //领取订单
            "prize" -> prizeOrder(agent, root)
        //订单开始
            "orderstart" -> startOrder(agent, root)
        //刷新商店
            "newShop" ->Army_Shop_Rest(agent,root)

            "refresh" -> refreshArmy(agent, root)
            "create" -> createArmy(agent, root)
            "queryByName" -> queryByName(agent, root)
            "apply" -> applyArmy(agent, root)
            "query" -> queryArmy(agent, root)
            "dissolution" -> dissolutionArmy(agent, root)
            "quit" -> quitArmy(agent, root)
            "applyList" -> applyList(agent, root)
            "dGold" -> dGold(agent, root)
            "diamonds" -> diamonds(agent, root)
            "updateIcon" -> updateIconArmy(agent, root)
            "updateName" -> updateNameArmy(agent, root)
            "updateAnn" -> updateAnnArmy(agent, root)
            "updategrain" -> updategrainArmy(agent, root)
            "into" -> intoArmy(agent, root)
            "isJoin" -> isJoinArmy(agent, root)
            "allPeople" -> getArmyAllPeople(agent, root)
            "updatePosition" -> updatePosition(agent, root)
            "agree" -> AgreeJoinArmy(agent, root)
            "ignore" -> ignoreJoinArmy(agent, root)
            "attorn" -> attornArmy(agent, root)
            "propose" -> proposeArmy(agent, root)
            "distribution" -> distributionArmy(agent, root)
            "disDiamonds" -> disDiamonds(agent, root)
            "descCoin" -> descCoin(agent, root)
            "armyProvisions" -> armyProvisions(agent, root)
            "useInfo" -> userArmyInfo(agent, root)
            "warTest" -> warTest(agent, root)
            else -> return
        }
    }

    fun warTest(agent: Agent, root: JSONObject) {
    }

    @JvmStatic
    fun main(args: Array<String>) {
        var jed= Jedis("127.0.0.1")
        var ui=User()

        println( jed.get(ui.uid.toString()))
    }
    /**
     * 进入军团商城
     */
    fun Army_Shop(agent: Agent, root: JSONObject) {
        if(SSF.getJedisMethod(agent).get(agent.UID.toString())==null||
                SSF.getJedisMethod(agent).get(String.format("%s_%s",agent.UID.toString(),"num"))==null){
            errorInt(agent)
        }
        var armyId = root["armyId"] as Int
        var newList = mutableListOf<Goods>()
        var uid=agent.user.uid
        var num="0"
        if(!"new".equals(SSF.getJedisMethod(agent).get(uid.toString()))) {        //如果集合中包含军团信息直接返回商品信息
            newList= JSONTool.toList(SSF.getJedisMethod(agent).get(uid.toString()),Goods::class.java)!!
        }else{
            newList=GoodsService.getArmyShopGoods(armyId)
            SSF.getJedisMethod(agent).set(uid.toString(),JSONTool.toJson(newList))
        }
        num =SSF.getJedisMethod(agent).get(String.format("%s_%s",uid.toString(),"num"))
        Send(agent, RoomController.Msg(1, detail = newList, data = root["data"], command = root["command"]))
        Send(agent, RoomController.Msg(1, detail = num, data = "inner_num", command = root["command"]))
        return
    }
    fun  errorInt(agent:Agent){
        Hall.everyDayClearArmyShop(agent)
    }
    /**
     * 刷新军团商城
     */
    fun Army_Shop_Rest(agent: Agent, root: JSONObject) {
        var uid=agent.user.uid
        var armyId = root["armyId"] as Int
        //第一步：验证是否是军团人员
        var armyUser = ArmyUserService.selectArmyUserOne(ArmyUser(uid = uid, armyId = armyId))
        if (armyUser == null) {
            Send(agent, RoomController.Msg(0, detail = "", data = root["data"], command = root["command"]))
            return
        }
        var userMsg = UserService.getUserMsgByUID(uid)                  //获取用户信息
        if(userMsg.coin<1000) return                  //验证金钱

        var num=SSF.getJedisMethod(agent).get(String.format("%s_%s",uid.toString(),"num"))
        if(num!=null&&num.toInt()>9)
            return   //如果刷新次数大于10次就不刷新了不反回任何信息
        if(num==null) {
            SSF.getJedisMethod(agent).set(String.format("%s_%s", uid.toString(), "num"), (0).toString()) //更新获取次数
            num="0"
        }
        var newList=GoodsService.getArmyShopGoods(armyId)       //获取最新商品信息
        SSF.getJedisMethod(agent).set(uid.toString(),JSONTool.toJson(newList))                //更新军团商品详情
        SSF.getJedisMethod(agent).set(String.format("%s_%s",uid.toString(),"num"),(num.toInt()+1).toString()) //更新获取次数
        UserService.updateUserCoin((userMsg.coin - 1000),uid)                //修改用户每次信息
        Send(agent, RoomController.Msg(1, detail = newList, data = root["data"], command = root["command"]))         //商品信息
        Send(agent, RoomController.Msg(1, detail = num.toInt()+1, data = "newShop_num", command = root["command"]))  //商店刷新次数
    }
    /**
     * 进入军团科技树
     */
    fun Army_Tech_Tree(agent: Agent, root: JSONObject) {
        var techTree = ArmyFmccService.selectArmyFmcc(ArmyFmcc(buildId = root["buildId"] as Int))
        Send(agent, RoomController.Msg(1, detail = techTree, data = root["data"], command = root["command"]))
    }

    /**
     * 进入军团作坊
     */
    fun Army_workshop(agent: Agent, root: JSONObject) {
        var workshop = ArmyOrderService.selectArmyOrder(agent.UID)
        Send(agent, RoomController.Msg(1, detail = workshop, data = String.format("%s_%s", root["data"], "order"), command = root["command"]))
        Send(agent, RoomController.Msg(1, detail = Date().time, data = String.format("%s_%s", root["data"], "date"), command = root["command"]))
        Send(agent, RoomController.Msg(1, detail = returnUserMsg(agent, root), data = String.format("%s_%s", root["data"], "userMsg"), command = root["command"]))
    }

    fun returnUserMsg(agent: Agent, root: JSONObject): MutableList<ArmyUser> {
        var user = ArmyUserService.selectArmyUserOne(ArmyUser(uid = agent.user.uid, armyId = root["armyId"] as Int))
        user.getOrderTime = lastTime(user.getOrderTime)
        return mutableListOf<ArmyUser>(user)
    }

    /**
     * 进入军团作坊-订单升级页面
     */
    fun Army_workshop_order(agent: Agent, root: JSONObject) {
        var order = ArmyFmccService.selectArmyFmcc(ArmyFmcc(buildId = root["buildId"] as Int, type = "order"))
        Send(agent, RoomController.Msg(1, detail = order, data = root["data"], command = root["command"]))
    }

    /**
     * 军团科技树升级
     */
    var treeMoney = 1000        //一次捐献金币数量

    fun Army_addtree_fmcc(agent: Agent, root: JSONObject) {
        var user = UserService.getUserMsgByUID(agent.UID)   //获取用户信息从数据库里
        var fmcc = ArmyFmccService.selectArmyFmccOne(ArmyFmcc(buildId = root["buildId"] as Int, type = root["type"] as String)) //查询buf等级
        var supId = ArmyFmccService.selectArmyTemp(ArmyFmcc(level = fmcc.level + 1, type = fmcc.type))
        var build = ArmyBuildService.selectArmyBuildOne(ArmyBuild(id = fmcc.buildId, type = "techtree"))
        var reduceMoney = fmcc_add(agent, treeMoney, "science")  //获取 如果用户有减少捐献金钱buf 后的 减少金钱数量
        when {
            user.coin < (treeMoney + reduceMoney) -> {                     //捐献金钱不足
                Send(agent, RoomController.Msg(0, detail = "", data = root["data"], command = root["command"]))
                return
            }
        }
        var isTrue = true
        when {
            supId < build.buildLevel -> isTrue = false
            (fmcc.number ?: 0) + treeMoney < fmcc.fullgold -> isTrue = false
        }
        if (isTrue) {
            ArmyFmccService.updateTreeGrade(ArmyFmcc(buildId = root["buildId"] as Int, type = root["type"] as String, level = (fmcc.level + 1)
                    , number = ((fmcc.number ?: 0) + treeMoney) - fmcc.fullgold.toInt())) //修改等级信息
        } else {
            ArmyFmccService.updateArmyFmcc(ArmyFmcc(buildId = root["buildId"] as Int, type = root["type"] as String
                    , number = ((fmcc.number ?: 0) + treeMoney)))
        }
        UserService.updateUserMsgByUID(User(uid = agent.UID, coin = (user.coin - (treeMoney + reduceMoney))))  //修改用户剩余金币（现有金钱-（一次捐献的金额+buf减少的金额））
        donation((treeMoney), 0, user)   //修改用户捐献总额
        var fmccAgen = ArmyFmccService.selectArmyFmccOne(ArmyFmcc(buildId = root["buildId"] as Int, type = root["type"] as String)) //查询buf等级

        Send(agent, RoomController.Msg(1, detail = mutableListOf(fmccAgen), data = root["data"], command = root["command"]))
    }

    /**
     * 军团作坊订单升级
     */
    var oneMoney = 1000        //一次捐献金币数量

    fun Army_addorder_fmcc(agent: Agent, root: JSONObject) {
        var user = UserService.getUserMsgByUID(agent.UID)   //获取用户信息从数据库里

        var fmcc = ArmyFmccService.selectArmyFmccOne(ArmyFmcc(buildId = root["buildId"] as Int, type = "order")) //查询buf等级
        if (fmcc == null) return
        var money = 0
        when {
            "one".equals(root["count"] as String)
            -> {
                if (user.coin < oneMoney) {//升级金额不足
                    Send(agent, RoomController.Msg(0, detail = "", data = root["data"], command = root["command"]))
                    return
                } else {
                    money = (fmcc.number ?: 0) + oneMoney
                }
            }
            "ten".equals(root["count"] as String)
            -> {
                if (user.coin < (oneMoney * 10)) {//升级金额不足
                    Send(agent, RoomController.Msg(0, detail = "", data = root["data"], command = root["command"]))
                    return
                } else {
                    money = (fmcc.number ?: 0) + oneMoney * 10
                }
            }
        }
        if (money > fmcc.fullgold) { //如果积累金钱可以升级默认升级否则存入累计
            ArmyFmccService.updateTreeGrade(ArmyFmcc(buildId = root["buildId"] as Int, type = "order"
                    , level = (fmcc.level + 1), number = money - fmcc.fullgold.toInt())) //修改等级信息
        } else {
            ArmyFmccService.updateArmyFmcc(ArmyFmcc(buildId = root["buildId"] as Int, type = "order", number = money))
        }
        donation((money - (fmcc.number ?: 0)), 0, user)   //修改用户捐献总额
        UserService.updateUserMsgByUID(User(uid = agent.UID, coin = (user.coin - (money - (fmcc.number ?: 0)))))  //修改用户剩余金币
        var fmccAgen = ArmyFmccService.selectArmyFmccOne(ArmyFmcc(buildId = root["buildId"] as Int, type = "order")) //查询buf等级
        Send(agent, RoomController.Msg(1, detail = mutableListOf(fmccAgen), data = root["data"], command = root["command"]))
    }

    /**
     * 军团建筑升级
     */
    var money = 1000          //一次捐献金币数量
    var gem = 1               //一次捐献钻石数量
    var gemShiftCoin = 3000   //一颗钻石转换金币的数量
    fun Army_addbuild_fmcc(agent: Agent, root: JSONObject) {
        var user = UserService.getUserMsgByUID(agent.UID)   //获取用户信息从数据库里
        if (root["armyId"] as Int == 0) return
        var build = ArmyBuildService.selectArmyBuildOne(ArmyBuild(armyId = root["armyId"] as Int, type = root["type"] as String))
        var coin = 0
        var diamond = 0
        var isTrue = true
        when (root["form"] as String) {
            "coin" -> when {
                "one".equals(root["count"] as String)
                -> {
                    if (user.coin < money) {//升级金额不足
                        Send(agent, RoomController.Msg(0, detail = "", data = root["data"], command = root["command"]))
                        return
                    }
                    when {
                        build.numberCoin + money < build.maxNumberCoin -> {
                            isTrue = false
                        }
                        build.numberGem < build.maxNumberGem -> {
                            isTrue = false
                        }
                        build.numberExp < build.maxNumberExp -> {
                            isTrue = false
                        }
                    }
                }
                "ten".equals(root["count"] as String)
                -> {
                    if (user.coin < (money * 10)) {//升级金额不足
                        Send(agent, RoomController.Msg(0, detail = "", data = root["data"], command = root["command"]))
                        return
                    }
                    when {
                        build.numberCoin + (money * 10) < build.maxNumberCoin -> {
                            isTrue = false
                        }
                        build.numberGem < build.maxNumberGem -> {
                            isTrue = false
                        }
                        build.numberExp < build.maxNumberExp -> {
                            isTrue = false
                        }
                    }
                }
            }

            "gem" -> when {
                "one".equals(root["count"] as String)
                -> {
                    if (user.gem < gem) {//升级金额不足
                        Send(agent, RoomController.Msg(0, detail = "", data = root["data"], command = root["command"]))
                        return
                    }
                    when {
                        build.numberGem + gem < build.maxNumberGem -> {
                            isTrue = false
                        }
                        build.numberCoin < build.maxNumberCoin -> {
                            isTrue = false
                        }
                        build.numberExp < build.maxNumberExp -> {
                            isTrue = false
                        }
                    }
                }
            /*"ten".equals(root["count"] as String)
            -> {
                if(user.gem<(gem*10)){//升级金额不足
                    Send(agent, RoomController.Msg(0, detail="" , data = root["data"], command = root["command"]))
                    return
                }
                when{
                    build.numberGem+(gem*10)       < build.maxNumberGem  ->{isTrue=false}
                    build.numberCoin               < build.maxNumberCoin ->{isTrue=false}
                    build.numberExp                < build.maxNumberExp  ->{isTrue=false}
                }
            }*/
            }
            "exp" -> {
                when {
                    (build.numberExp + root["num"] as Int) < build.maxNumberExp -> {
                        isTrue = false
                    }
                    build.numberGem < build.maxNumberGem -> {
                        isTrue = false
                    }
                    build.numberCoin < build.maxNumberCoin -> {
                        isTrue = false
                    }
                }
            }
        }
        //如果捐献的不是军团中心 那么判断等级如果 大于或者等于中心等级 则不可以升级 累计金钱
        if (!"center".equals(root["type"])) {
            var centerBuild = ArmyBuildService.selectArmyBuildOne(ArmyBuild(id = build.centreId))
            if (build.buildLevel >= centerBuild.buildLevel) {
                isTrue = false
            }
        } else {

        }
        //true  说明满足了升级条件可以进行升级  不满足  把数据累计
        if (isTrue) {
            var update = ArmyBuild(armyId = root["armyId"] as Int, type = root["type"] as String, buildLevel = build.buildLevel + 1)
            when (root["form"] as String) {
                "coin" -> {
                    when {
                        "one".equals(root["count"] as String)
                        -> {
                            coin = (build.numberCoin + money).toInt()
                            update.numberCoin = (build.numberCoin + money) - build.maxNumberCoin
                        }
                        "ten".equals(root["count"] as String)
                        -> {
                            coin = (build.numberCoin + (money * 10)).toInt()
                            update.numberCoin = (build.numberCoin + (money * 10)) - build.maxNumberCoin
                        }
                    }
                }
                "gem" -> {
                    when {
                        "one".equals(root["count"] as String)
                        -> {
                            diamond = (gem)
                            update.numberGem = (build.numberGem + gem) - build.maxNumberGem
                        }
                    /*  "ten".equals(root["count"] as String)
                      ->{
                          diamond=((gem*10))
                          update.numberGem=(build.numberGem+(gem*10)) - build.maxNumberGem
                      }*/
                    }
                }
                "exp" -> {
                    update.numberExp = build.numberExp + root["num"] as Int - build.maxNumberExp
                }
            }
            ArmyBuildService.updateArmyBuildByTemp(update)
        } else {
            var abuild = ArmyBuild(armyId = root["armyId"] as Int, type = root["type"] as String)
            when (root["form"] as String) {
                "coin" -> {
                    when {
                        "one".equals(root["count"] as String)
                        -> {
                            coin = (money)
                            abuild.numberCoin = build.numberCoin + money
                        }
                        "ten".equals(root["count"] as String)
                        -> {
                            coin = (money * 10)
                            abuild.numberCoin = build.numberCoin + (money * 10)
                        }
                    }
                }
                "gem" -> {
                    when {
                        "one".equals(root["count"] as String)
                        -> {
                            diamond = (gem)
                            if (build.numberCoin < build.maxNumberCoin) {
                                abuild.numberCoin = (gem * gemShiftCoin.toLong() + build.numberCoin)
                            } else {
                                abuild.numberGem = build.numberGem + gem
                            }
                        }
                    }
                }
                "exp" -> {
                    abuild.numberExp = build.numberExp + root["num"] as Int
                }
            }
            ArmyBuildService.updateArmyBuild(abuild)
        }
        donation((coin), diamond, user)   //修改用户捐献总额
        UserService.updateUserMsgByUID(User(uid = agent.UID, coin = (user.coin - money), gem = (user.gem - diamond)))  //修改用户剩余金币
        var buildAgent = ArmyBuildService.selectArmyBuildOne(ArmyBuild(armyId = root["armyId"] as Int, type = root["type"] as String))
        Send(agent, RoomController.Msg(1, detail = mutableListOf(buildAgent), data = root["data"], command = root["command"]))
    }


    /**
     * 获取订单
     */
    fun Army_get_order(agent: Agent, root: JSONObject) {
        var armyId = root["armyId"] as Int
        var orderList = ArmyOrderService.selectArmyOrder(agent.UID)   //查询用户订单信息
        var build = ArmyBuildService.selectArmyBuildOne(ArmyBuild(armyId = armyId, type = "workshop")) //查询军团建筑信息
        var userMsg = ArmyUserService.selectArmyUserOne(ArmyUser(uid = agent.UID, armyId = armyId))
        if (userMsg == null) return
        when {//如果订单允许数量 小于 或者等于 当前持有订单数 返回 获取失败
            userMsg.getOrderTime != 0L && isBigThisTime(userMsg.getOrderTime) -> {
                Send(agent, RoomController.Msg(0, detail = orderList, data = root["data"], command = root["command"]))
                Send(agent, RoomController.Msg(0, detail = returnUserMsg(agent, root), data = String.format("%s_%s", root["data"], "userMsg"), command = root["command"]))
                return
            }
            build.count <= orderList.size -> {
                Send(agent, RoomController.Msg(0, detail = orderList, data = root["data"], command = root["command"]))
                Send(agent, RoomController.Msg(0, detail = returnUserMsg(agent, root), data = String.format("%s_%s", root["data"], "userMsg"), command = root["command"]))
                return
            }
            build.count > orderList.size -> {
                for (i in 1..(build.count - orderList.size)) {
                    ArmyOrderService.insertArmyOrder(ArmyOrder(uid = agent.UID, armyId = armyId, buildId = build.id))
                }
            }
        }
        var orderLists = ArmyOrderService.selectArmyOrder(agent.UID)   //查询用户订单信息
        ArmyUserService.updateArmyUser(ArmyUser(uid = agent.UID, armyId = armyId, getOrderTime = get_closing_time(12)))
        Send(agent, RoomController.Msg(1, detail = orderLists, data = root["data"], command = root["command"]))
        Send(agent, RoomController.Msg(1, detail = returnUserMsg(agent, root), data = String.format("%s_%s", root["data"], "userMsg"), command = root["command"]))
    }

    //获取预计到期时间
    fun get_closing_time(outDate: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.setTime(Date())
        calendar.add(Calendar.HOUR_OF_DAY, outDate)
        return calendar.time.time
    }

    //获取预计到期时间
    fun isBigThisTime(thisTime: Long): Boolean {
        if (0L == thisTime) return true
        return thisTime > Date().time
    }

    //获取预计到期时间
    fun lastTime(thisTime: Long): Long {
        if (0L == thisTime) return 0L

        if (thisTime < Date().time) {
            return 0L
        }
        var time = thisTime - Date().time
        return time
    }

    /**
     * 修改订单等级
     */
    fun Army_up_order(agent: Agent, root: JSONObject) {
        var user = UserService.getUserMsgByUID(agent.UID)   //获取用户信息从数据库里
        var order = ArmyOrderService.selectArmyOrderOne(ArmyOrder(id = root["orderId"] as Int))
        if (user.coin < order.fullGold) {
            Send(agent, RoomController.Msg(0, detail = "", data = root["data"], command = root["command"]))
            return
        }
        ArmyOrderService.updateArmyOrder(ArmyOrder(id = root["orderId"] as Int, orderLevel = order.orderLevel + 1))  //修改订单等级
        var orderAgen = ArmyOrderService.selectArmyOrderOne(ArmyOrder(id = root["orderId"] as Int))
        Send(agent, RoomController.Msg(1, detail = mutableListOf(orderAgen), data = root["data"], command = root["command"]))
    }

    /**
     * 修改用户累计金额
     */
    fun donation(coin: Int, gem: Int, user: User) {
        var armyUser = ArmyUserService.selectArmyUserOne(ArmyUser(uid = user.uid))
        ArmyUserService.updateArmyUser(ArmyUser(uid = user.uid, donationCoin = (armyUser.donationCoin + coin), donationGem = armyUser.donationGem + gem))

    }

    /**
     * 领取订单奖励
     */
    fun prizeOrder(agent: Agent, root: JSONObject) {
        var order = ArmyOrderService.selectArmyOrderOne(ArmyOrder(id = root["orderId"] as Int))  //获取订单信息
        var armyFmcc = ArmyFmccService.selectArmyFmccOne(ArmyFmcc(type = "order", buildId = order.buildId))            //获取订单加成信息
        var army = ArmyAdminService.getArmyInfo(order.armyId)                                   //获取军团信息
        var user = UserService.getUserMsgByUID(agent.UID)   //获取用户信息从数据库里
        if (order == null || armyFmcc == null || army == null || user == null) return
        //（现有 + 军团buf加成 + 道具buf加成）
        if (order.time <= Date().time) {
            ArmyAdminService.updategrain(armyId = order.armyId
                    , grain = (army.grain + (order.getProvisions * armyFmcc.addition) + fmcc_add(agent, order.getProvisions, "order")).toLong()) //修改军团军粮
            UserService.updateUserMsgByUID(User(uid = agent.UID
                    , coin = user.coin + (order.getGold + (order.getGold * armyFmcc.addition) + fmcc_add(agent, order.getGold, "order")).toLong()))  //修改用户金币
            ArmyOrderService.deleteArmyOrder(ArmyOrder(id = order.id))                          //领取订单 后 删除订单
            Send(agent, RoomController.Msg(1, detail = MutableList<ArmyOrder>(1) { order }, data = root["data"], command = root["command"]))
        } else {
            Send(agent, RoomController.Msg(0, detail = "", data = root["data"], command = root["command"]))
        }
    }

    /**
     * 加成计算
     */
    fun fmcc_add(agent: Agent, num: Int, type: String): Long {
        var fmcc = FmccService.selectFmccByUid(Fmcc(uid = agent.UID, type = type))
        var number = 0L
        if (fmcc.isEmpty()) return number
        var sp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        for (f in fmcc) {
            when (f.outDateType) {
                "hour" -> {
                    if (sp.parse(f.closingTime) > Date()) {
                        if (number != 0L) {
                            number += (f.addition * num).toLong()
                        } else {
                            number = (f.addition * num).toLong()
                        }
                    } else {
                        FmccService.deleteFmcc(Fmcc(fid = f.fid))
                    }
                }
                "num" -> {
                    if ((f.outDate ?: 0) > 0) {
                        if (number != 0L) {
                            number += (f.addition * num).toLong()
                        } else {
                            number = (f.addition * num).toLong()
                        }
                        FmccService.updateFmcc(Fmcc(fid = f.fid, outDate = f.outDate ?: 0-1))
                    } else {
                        FmccService.deleteFmcc(Fmcc(fid = f.fid))
                    }
                }
            }
        }
        return number
    }

    /**
     * 开始订单
     */
    fun startOrder(agent: Agent, root: JSONObject) {
        var order = ArmyOrderService.selectArmyOrderOne(ArmyOrder(id = root["orderId"] as Int))
        ArmyOrderService.updateArmyOrder(ArmyOrder(id = root["orderId"] as Int, orderLevel = order.orderLevel, status = 1))  //修改订单等级
        var orders = ArmyOrderService.selectArmyOrderOne(ArmyOrder(uid = agent.UID, id = root["orderId"] as Int))
//        orders.time= lastTime(orders.time)
        Send(agent, RoomController.Msg(1, detail = mutableListOf(orders), data = root["data"], command = root["command"]))
    }

    fun refreshArmy(agent: Agent, root: JSONObject) {
        var army = ArmyAdminService.recommendArmy()
        if (army == null) {
            Send(agent, Msg(1, "zero", "refresh", root["command"]))
        } else {
            Send(agent, Msg(1, army, "refresh", root["command"]))
        }
    }

    fun createArmy(agent: Agent, root: JSONObject) {
        if (!root.has("uid") || !root.has("name")) return

        if (ArmyAdminService.armyNameCheck(root["name"] as String)) {
            Send(agent, Msg(1, "repeat", "create", root["command"]))
            return
        }

        if (ArmyAdminService.isJoinArmy(agent.user.uid) != 0) {
            Send(agent, Msg(1, "existence", "create", root["command"]))
            return
        }

        if (ArmyAdminService.armyNameCheck(root["name"] as String)) {
            Send(agent, Msg(1, "repeat", "create", root["command"]))
            return
        }

        var uid = root["uid"] as Int

        var user = UserService.getUserMsgByUID(uid)

        if (user.gem >= 500) {       //更新钻石（减少500）
            var gem = user.gem - 500
            UserService.updateUserGem(gem, uid)
        } else {
            Send(agent, Msg(1, "less", "create", root["command"]))
            return
        }

        var army = Army()
        army.adminid = uid
        if (root["icon"] != null) {
            army.icon = root["icon"].toString()
        }
        army.name = root["name"] as String
        if (root["announcement"] != null) {
            army.announcement = root["announcement"].toString()
        }

        army.createDate = LocalDate.now().toString()

        val armyId = ArmyAdminService.createArmy(army)

        if (armyId > 0) {
            // 初始化军团长的信息
            var armyUser = ArmyUser()
            armyUser.uid = user.uid
            armyUser.status = 1
            armyUser.icon = user.avatar
            armyUser.name = user.nick
            armyUser.armyId = armyId
            armyUser.armyJob = 2
            ArmyAdminService.initArmyUser(armyUser)

            Send(agent, Msg(1, "success", "create", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "create", root["command"]))
        }
    }

    fun queryByName(agent: Agent, root: JSONObject) {
        if (!root.has("armyName")) return

        var json = ArmyAdminService.queryNameByArmy(root["armyName"] as String)
        if (json != null) Send(agent, Msg(1, json, "queryByName", root["command"]))
        else Send(agent, Msg(1, "fail", "queryByName", root["command"]))
    }

    fun applyArmy(agent: Agent, root: JSONObject) {
        if (!root.has("uid")) return
        if (!root.has("armyId")) return
        if (!root.has("icon")) return

        var uid = root["uid"] as Int
        var name = root["name"] as String
        var armyId = root["armyId"] as Int
        var icon = root["icon"] as String

        if (ArmyAdminService.repeatJoinArmy(uid, armyId)) {
            Send(agent, Msg(1, "repeat", "apply", root["command"]))
            return
        }

        if (ArmyAdminService.applyArmy(uid, name, armyId, icon) > 0) {
            Send(agent, Msg(1, "success", "apply", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "apply", root["command"]))
        }
    }

    fun queryArmy(agent: Agent, root: JSONObject) {

        if (!root.has("armyId")) return
        val armyId = root["armyId"] as Int

        var army = ArmyAdminService.getArmyInfo(armyId)
        if (army != null) {
            army.currentNumberPeople = ArmyAdminService.currentNumberPeople(armyId)     //当前人数
            army.armyJob = ArmyAdminService.getArmyUser(agent.user.uid).armyJob
            Send(agent, Msg(1, mutableListOf(army), "query", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "query", root["command"]))
        }
    }

    fun dissolutionArmy(agent: Agent, root: JSONObject) {
        if (!root.has("armyId") || !root.has("uid")) return

        var army = ArmyAdminService.getArmyInfo(root["armyId"] as Int)
        if (army.adminid != agent.user.uid) {
            Send(agent, Msg(1, "denied", "dissolution", root["command"]))
            return
        }
        val armyId = root["armyId"] as Int
        if (ArmyAdminService.dissolutionArmy(armyId) > 0) {
            //军团状态-军团订单-军团建筑-军团人员
            val typeList = listOf<String>("workshop", "techtree")

            typeList.forEach {
                val build = ArmyAdminService.queryArmyBuilType(armyId, it).id
                ArmyAdminService.delArmyFmcc(build)
            }                                           //状态
            ArmyAdminService.delArmyOrder(armyId)       //订单
            ArmyAdminService.delArmyBuil(armyId)        //建筑
            ArmyAdminService.delAllArmyUser(armyId)     //人员
            Send(agent, Msg(1, "success", "dissolution", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "dissolution", root["command"]))
        }

    }


    fun quitArmy(agent: Agent, root: JSONObject) {
        if (!root.has("uid") || !root.has("armyId")) return
        if (agent.user.uid != root["uid"] as Int) {
            Send(agent, Msg(1, "denied", "quit", root["command"]))
            return
        }
        val armyId = root["armyId"] as Int
        val uid = root["uid"] as Int

        if (ArmyAdminService.quitArmy(uid, armyId) > 0) {
            //删除对应的订单信息
            ArmyAdminService.delUserOrder(uid)

            val currentNumberPeople = ArmyAdminService.currentNumberPeople(armyId) - 1          //当前人数
            ArmyAdminService.updateCurrentNumberPeople(currentNumberPeople.toString(), armyId)  //更新人数
            Send(agent, Msg(1, "success", "quit", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "quit", root["command"]))
        }
    }

    fun applyList(agent: Agent, root: JSONObject) {
        if (!root.has("armyId")) return

        val armyUser = jurisdictionArmy(agent.user.uid)
        if (armyUser != null) {
            if (armyUser.armyJob == 0) {
                Send(agent, Msg(1, "denied", "applyList", root["command"]))
                return
            }
        }

        var json = ArmyAdminService.applyArmyList(root["armyId"] as Int)
        if (json != null) {
            Send(agent, Msg(1, json, "applyList", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "applyList", root["command"]))
        }
    }

    fun dGold(agent: Agent, root: JSONObject) {
        if (!root.has("uid") || !root.has("armyId") || !root.has("gold")) return

        var uid = root["uid"] as Int
        var armyId = root["armyId"] as Int
        var gold = root["gold"].toString().toLong()
        if (ArmyAdminService.donationGold(uid, armyId, gold) > 0) {
            Send(agent, Msg(1, "success", "dGold", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "dGold", root["command"]))
        }
    }


    fun diamonds(agent: Agent, root: JSONObject) {
        if (!root.has("uid") || !root.has("armyId") || !root.has("diamonds")) return

        var uid = root["uid"] as Int
        var armyId = root["armyId"] as Int
        var diamonds = root["diamonds"].toString().toLong()
        if (ArmyAdminService.donationDiamonds(uid, armyId, diamonds) > 0) {
            Send(agent, Msg(1, "success", "diamonds", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "diamonds", root["command"]))
        }
    }

    fun updateIconArmy(agent: Agent, root: JSONObject) {
        if (!root.has("icon") || !root.has("armyId") || !root.has("uid")) return
        var uid = root["uid"] as Int
        var armyId = root["armyId"] as Int
        var icon = root["icon"].toString()

        if (ArmyAdminService.getArmyInfo(armyId).adminid != uid) {
            Send(agent, Msg(1, "denied", "updateIcon", root["command"]))
            return
        }

        var re = ArmyAdminService.updateArmyIcon(icon, armyId)
        if (re > 0) {
            Send(agent, Msg(1, "success", "updateIcon", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "updateIcon", root["command"]))
        }
    }


    fun updateNameArmy(agent: Agent, root: JSONObject) {
        if (!root.has("name") || !root.has("armyId") || !root.has("uid")) return

        var uid = root["uid"] as Int
        var armyId = root["armyId"] as Int
        var name = root["name"].toString()

        if (ArmyAdminService.getArmyInfo(armyId).adminid != uid) {
            Send(agent, Msg(1, "denied", "updateIcon", root["command"]))
            return
        }
        var re = ArmyAdminService.updateArmyName(name, armyId)
        if (re > 0) {
            Send(agent, Msg(1, "success", "updateName", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "updateName", root["command"]))
        }
    }

    fun updateAnnArmy(agent: Agent, root: JSONObject) {
        if (!root.has("announcement") || !root.has("armyId") || !root.has("uid")) return
        var uid = root["uid"] as Int
        var armyId = root["armyId"] as Int
        var announcement = root["announcement"].toString()

        if (ArmyAdminService.getArmyInfo(armyId).adminid != uid) {
            Send(agent, Msg(1, "denied", "updateIcon", root["command"]))
            return
        }

        var re = ArmyAdminService.updateArmyAnnouncement(announcement, armyId)
        if (re > 0) {
            Send(agent, Msg(1, "success", "updateAnn", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "updateAnn", root["command"]))
        }
    }

    fun updategrainArmy(agent: Agent, root: JSONObject) {

        if (!root.has("grain") || !root.has("armyId") || !root.has("uid")) return

        var uid = root["uid"] as Int
        var armyId = root["armyId"] as Int
        var grain = root["grain"].toString().toLong()

        if (ArmyAdminService.getArmyInfo(armyId).adminid != uid) {
            Send(agent, Msg(1, "denied", "updateIcon", root["command"]))
            return
        }

        var re = ArmyAdminService.updategrain(grain, armyId)
        if (re > 0) {
            Send(agent, Msg(1, "success", "updategrain", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "updategrain", root["command"]))
        }
    }

    fun intoArmy(agent: Agent, root: JSONObject) {

        if (!root.has("armyId") || !root.has("uid")) return
        var armyUser = ArmyAdminService.getArmyUser(root["uid"] as Int)
        if (armyUser == null) {
            Send(agent, Msg(1, "denied", "into", root["command"]))
            return
        } else {
            if (armyUser.armyId != root["armyId"] as Int) {
                Send(agent, Msg(2, "denied", "into", root["command"]))
                return
            }
        }


        var json = ArmyBuildService.selectArmyBuildByArmyId(root["armyId"] as Int)
        if (json != null) {
            Send(agent, Msg(1, json, "into", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "into", root["command"]))
        }


    }


    fun isJoinArmy(agent: Agent, root: JSONObject) {
        if (!root.has("uid")) return
        var json = ArmyAdminService.isJoinArmy(root["uid"] as Int)
        Send(agent, Msg(1, json, "isJoin", root["command"]))
    }

    fun getArmyAllPeople(agent: Agent, root: JSONObject) {

        if (!root.has("armyId")) return
        var json = ArmyAdminService.getArmyAllPeople(root["armyId"] as Int)

        if (json != null) {
            Send(agent, Msg(1, json, "allPeople", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "allPeople", root["command"]))
        }
    }

    fun updatePosition(agent: Agent, root: JSONObject) {
        if (!root.has("mid") || !root.has("uid") || !root.has("armyId") || !root.has("armyJob")) return

        var mid = root["mid"] as Int
        var uid = root["uid"] as Int
        var armyId = root["armyId"] as Int

        var armyUser = jurisdictionArmy(root["mid"] as Int)
        if (armyUser.armyJob == 1 || armyUser.armyJob == 2) {
            if (ArmyAdminService.updatePosition(uid, armyId, root["armyJob"] as Int) > 0) {
                Send(agent, Msg(1, "success", "updatePosition", root["command"]))
            } else {
                Send(agent, Msg(1, "fail", "updatePosition", root["command"]))
            }

        } else {
            Send(agent, Msg(1, "fail", "agree", root["command"]))
        }


    }

    fun AgreeJoinArmy(agent: Agent, root: JSONObject) {
        if (!root.has("mid") || !root.has("uid") || !root.has("armyId")) return

        val uid = root["uid"] as Int
        val armyId = root["armyId"] as Int
        var armyUser = jurisdictionArmy(root["mid"] as Int)
        if (armyUser.armyJob == 1 || armyUser.armyJob == 2) {
            if (ArmyAdminService.AgreeJoinArmy(uid, armyId) > 0) {
                Send(agent, Msg(1, "success", "agree", root["command"]))

                //加入军团的全团公告
                joinArmy(armyId, ArmyAdminService.getArmyInfo(armyId).name, UserService.getUserMsgByUID(uid).name)
                //防止存在已经申请通过但还存在申请记录
                val size = ArmyAdminService.delSurplusApplyUser(uid)
                if (size != 0) {
                    if (ArmyAdminService.delSurplusApplyUser(uid) != 0) {
                        Send(agent, Msg(1, "malfunction", "agree", root["command"]))
                        log.info("Legion agrees to malfunction uid:${uid}")

                    }
                }
            } else {
                Send(agent, Msg(1, "fail", "agree", root["command"]))
            }
        } else {
            Send(agent, Msg(1, "fail", "agree", root["command"]))
        }
    }

    fun ignoreJoinArmy(agent: Agent, root: JSONObject) {
        if (!root.has("mid") || !root.has("uid") || !root.has("armyId")) return

        if (ArmyAdminService.ignoreJoinArmy(root["uid"] as Int, root["armyId"] as Int) > 0) {
            Send(agent, Msg(1, "success", "ignore", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "ignore", root["command"]))
        }
    }

    fun attornArmy(agent: Agent, root: JSONObject) {

        if (!root.has("mid") || !root.has("uid") || !root.has("armyId")) return

        var mid = root["mid"] as Int
        var uid = root["uid"] as Int
        var armyId = root["armyId"] as Int

        var army = ArmyAdminService.getArmyInfo(armyId)

        if (army == null) {
            Send(agent, Msg(1, "fail", "attorn", root["command"]))
            return
        }

        if (army.adminid == mid) {
            if (ArmyAdminService.updatePosition(uid, armyId, 2) > 0) {
                ArmyAdminService.updateArmyUid(armyId, uid)

                ArmyAdminService.updatePosition(mid, armyId, 0)   //update job

            }
            Send(agent, Msg(1, "success", "attorn", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "attorn", root["command"]))
        }
    }

    fun proposeArmy(agent: Agent, root: JSONObject) {
        if (!root.has("mid") || !root.has("uid") || !root.has("armyId")) return

        var army = jurisdictionArmy(root["mid"] as Int)
        if (army.armyJob == 1 || army.armyJob == 2) {
            if (ArmyAdminService.proposeArmy(root["uid"] as Int, root["armyId"] as Int) > 0) {
                //删除对应的订单信息
                ArmyAdminService.delUserOrder(root["uid"] as Int)

                Send(agent, Msg(1, "success", "propose", root["command"]))
            } else {
                Send(agent, Msg(1, "fail", "propose", root["command"]))
            }
        } else {
            Send(agent, Msg(1, "fail", "propose", root["command"]))
        }
    }

    fun distributionArmy(agent: Agent, root: JSONObject) {
        if (!root.has("mid") || !root.has("uidArray") || !root.has("money")) return

        var armyUser = jurisdictionArmy(root["mid"] as Int)

        if (armyUser == null) {
            Send(agent, Msg(1, "fail", "distribution", root["command"]))
            return
        }


        var army = ArmyAdminService.getArmyInfo(armyUser.armyId)
        var coin = root["money"].toString().toLong()
        if (armyUser.armyJob == 2) {
            val uidArryString = root["uidArray"].toString()
            var uidArry = uidArryString.substring(1, uidArryString.length - 1).split(",")
            var armyCoin = army.competcoin
            if (armyCoin < (uidArry.size * coin)) return

            coin = armyCoin - (uidArry.size * coin)
            if (ArmyAdminService.updateAarmyCoin(army.id, coin) > 0) {
                val money = root["money"].toString().toLong()
                for ((i, v) in uidArry.withIndex()) {
                    var re = ArmyAdminService.distributionArmy(v.toInt(), money)
                    if (re == 0) {
                        Send(agent, Msg(1, "fail#${v}", "distribution", root["command"]))
                    }
                }
                Send(agent, Msg(1, "success", "distribution", root["command"]))
            }
        } else {
            Send(agent, Msg(1, "fail", "distribution", root["command"]))
        }
    }


    fun disDiamonds(agent: Agent, root: JSONObject) {
        if (!root.has("mid") || !root.has("uidArray") || !root.has("money")) return
        var armyUser = jurisdictionArmy(root["mid"] as Int)
        var army = ArmyAdminService.getArmyInfo(armyUser.armyId)    //军团信息
        var gem = root["money"].toString().toLong()

        if (armyUser.armyJob == 2) {
            val uidArryString = root["uidArray"].toString()
            var uidArry = uidArryString.substring(1, uidArryString.length - 1).split(",")

            var armyGem = army.competgem
            if (armyGem < (uidArry.size * gem)) return
            if (ArmyAdminService.updateAarmyDiamonds(army.id, armyGem - (uidArry.size * gem)) > 0) {
                for ((i, v) in uidArry.withIndex()) {
                    var re = ArmyAdminService.disDiamondsArmy(v.toBigInteger().toInt(), gem)
                    if (re == 0) {
                        Send(agent, Msg(1, "fail#${v}", "disDiamonds", root["command"]))
                    }
                }
                Send(agent, Msg(1, "success", "disDiamonds", root["command"]))
            }
        } else {
            Send(agent, Msg(1, "fail", "disDiamonds", root["command"]))
        }
    }

    fun descCoin(agent: Agent, root: JSONObject) {
        if (!root.has("armyId")) return
        val json = ArmyAdminService.descCoin(root["armyId"] as Int)
        if (json != null) {
            Send(agent, Msg(1, json, "descCoin", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "descCoin", root["command"]))
        }
    }

    fun armyProvisions(agent: Agent, root: JSONObject) {
        if (!root.has("armyId")) return
        val s = ArmyAdminService.armyProvisions(root["armyId"] as Int)
        if (s.length > 0) {
            Send(agent, Msg(1, s, "armyProvisions", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "armyProvisions", root["command"]))
        }
    }

    fun userArmyInfo(agent: Agent, root: JSONObject) {
        if (!root.has("uid")) return
        val user = ArmyAdminService.userArmyInfo(root["uid"] as Int)
        if (user != null) {
            Send(agent, Msg(1, user.toString(), "useInfo", root["command"]))
        } else {
            Send(agent, Msg(1, "fail", "useInfo", root["command"]))
        }
    }

    fun jurisdictionArmy(mid: Int): ArmyUser {
        var user = ArmyAdminService.getArmyUser(mid)
        return user
    }

    //get SendMsg
    fun Msg(result: Any?, detail: Any?, data: Any?, command: Any?): String {
        val msg = JSONObject()
        msg.put("command", command)
        msg.put("result", result)
        msg.put("data", data)
        msg.put("detail", detail)
        return msg.toString()
    }

    fun Send(agent: Agent, msg: String) = agent.Send(agent.CID, msg)
}