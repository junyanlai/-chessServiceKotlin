package com.shine.aservice.army

import com.shine.amodel.*
import com.shine.aservice.user.UserService
import com.shine.controller.aHall.Hall.isOnLine
import com.shine.dao.ArmyAdminDao
import com.shine.dao.UserDao
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashSet

object ArmyAdminService : IArmyAdminService {


    var subscript = HashSet<Int>()
    override fun recommendArmy(): JSONArray {

        var list = ArmyAdminDao().selectAllArmyInfo()
        var array = JSONArray()
        if (list.size == 0) return array
        getRandom(list.size)
        subscript.forEach {
            var json = JSONObject()
            var army = list[it]
            json.put("id", army.id)
            json.put("adminid", army.adminid)
            json.put("level", army.level)
            json.put("num", army.num)
            json.put("icon", army.icon)
            json.put("name", army.name)
            json.put("announcement", army.announcement)
            json.put("armyTitle", army.armyTitle)
            json.put("createDate", army.createDate)
            json.put("curren", currentNumberPeople(army.id))
            array.put(json)
        }
        return array
    }

    fun getRandom(value: Int) {
        subscript.clear()
        if (value >= 3) {
            while (true) {
                subscript.add(Random().nextInt(value))
                if (subscript.size == 3) break
            }
        } else {
            while (true) {
                subscript.add(Random().nextInt(value))
                if (subscript.size == value) break
            }
        }
    }

    override fun createArmy(army: Army): Int {
        ArmyAdminDao().insertOneArmy(army)
        var centerInfo = ArmyAdminDao().getBuildingFormwork("center", 0)
        var armyId = army.id            //ID

        var armyCenter = ArmyBuild(0, "軍團中心", armyId, centerInfo.level, 0, 0, centerInfo.gold, 0, centerInfo.integral, 0, 0, centerInfo.type, 0)
        ArmyAdminDao().createArmyBuild(armyCenter)

        var centerId = armyCenter.id
        var shopInfo = ArmyAdminDao().getBuildingFormwork("shop", 0)
        var armyShop = ArmyBuild(0, shopInfo.name, armyId, shopInfo.level, centerId, 0, shopInfo.gold, 0, 0, 0, 0, shopInfo.type, shopInfo.reward.toInt())
        ArmyAdminDao().createArmyBuild(armyShop)

        var workshopInfo = ArmyAdminDao().getBuildingFormwork("workshop", 0)
        var armyWorkshop = ArmyBuild(0, workshopInfo.name, armyId, workshopInfo.level, centerId, 0, 0, 0, 0, 0, 0, workshopInfo.type, workshopInfo.reward.toInt())
        ArmyAdminDao().createArmyBuild(armyWorkshop)

        var orderInfo = ArmyAdminDao().getBuildingFormwork("order", 0)
        ArmyAdminDao().insertArmyFmcc(initFmcc(orderInfo, armyWorkshop.id))

        var techtreeInfo = ArmyAdminDao().getBuildingFormwork("techtree", 0)
        var armyTechtree = ArmyBuild(0, techtreeInfo.name, armyId, techtreeInfo.level, centerId, 0, techtreeInfo.gold, 0, 0, 0, 0, techtreeInfo.type, techtreeInfo.reward.toInt())
        ArmyAdminDao().createArmyBuild(armyTechtree)
        var buffQsInfo = ArmyAdminDao().getBuildingFormwork("buffQs", 1)
        ArmyAdminDao().insertArmyFmcc(initFmcc(buffQsInfo, armyTechtree.id))
        var buffDsInfo = ArmyAdminDao().getBuildingFormwork("buffDs", 1)
        ArmyAdminDao().insertArmyFmcc(initFmcc(buffDsInfo, armyTechtree.id))
        var buffXbwInfo = ArmyAdminDao().getBuildingFormwork("buffXbw", 1)
        ArmyAdminDao().insertArmyFmcc(initFmcc(buffXbwInfo, armyTechtree.id))
        var buffXyxInfo = ArmyAdminDao().getBuildingFormwork("buffXyx", 1)
        ArmyAdminDao().insertArmyFmcc(initFmcc(buffXyxInfo, armyTechtree.id))
        var buffCsInfo = ArmyAdminDao().getBuildingFormwork("buffCs", 1)
        ArmyAdminDao().insertArmyFmcc(initFmcc(buffCsInfo, armyTechtree.id))
        var buffDrInfo = ArmyAdminDao().getBuildingFormwork("buffDr", 1)
        ArmyAdminDao().insertArmyFmcc(initFmcc(buffDrInfo, armyTechtree.id))
        var warfareInfo = ArmyAdminDao().getBuildingFormwork("warfare", 0)
        var armyWarfare = ArmyBuild(0, warfareInfo.name, armyId, warfareInfo.level, centerId, 0, warfareInfo.gold, 0, 0, 0, 0, warfareInfo.type, warfareInfo.reward.toInt())

        ArmyAdminDao().createArmyBuild(armyWarfare)

        return armyId
    }

    override fun queryNameByArmy(armyName: String): JSONArray {
        var list = ArmyAdminDao().selectNameByArmy(armyName)
        var array = JSONArray()
        for ((i, v) in list.withIndex()) {
            var json = JSONObject()
            json.put("id", v.id)
            json.put("adminid", v.adminid)
            json.put("level", v.level)
            json.put("num", v.num)
            json.put("icon", v.icon)
            json.put("name", v.name)
            json.put("announcement", v.announcement)
            json.put("armyTitle", v.armyTitle)
            json.put("createDate", v.createDate)
            json.put("curren", currentNumberPeople(v.id))
            array.put(json)
        }
        return array
    }

    override fun applyArmy(uid: Int, name: String, armyId: Int, icon: String): Int {
        var armyUser = ArmyUser()
        armyUser.uid = uid
        armyUser.status = 2
        armyUser.name = name
        armyUser.armyId = armyId
        armyUser.icon = icon
        var re = ArmyAdminDao().insertArmyUser(armyUser)
        return re
    }

    override fun getArmyInfo(id: Int): Army {
        val army = ArmyAdminDao().getArmyInfo(id)
        return army
    }

    fun getArmyInfoString(id: String): Army {
        val army = ArmyAdminDao().getArmyInfoString(id)
        return army
    }

    override fun updategrain(grain: Long, armyId: Int): Int {
        var re = ArmyAdminDao().updategrain(grain, armyId)
        return re
    }

    override fun dissolutionArmy(id: Int): Int {
        var re = ArmyAdminDao().deleteArmy(id)
        return re
    }

    override fun quitArmy(uid: Int, armyId: Int): Int {
        var re = ArmyAdminDao().deleteArmyUser(uid, armyId)
        return re
    }

    override fun applyArmyList(armyId: Int): JSONArray {
        var list = ArmyAdminDao().applyArmyList(armyId)
        var array = JSONArray()
        for ((i, v) in list.withIndex()) {
            var json = JSONObject()

            json.put("uid", v.uid)
            json.put("name", v.name)
            json.put("icon", v.icon)
            json.put("state", isOnLine(v.uid))
            json.put("armyId", v.armyId)
            array.put(json)
        }
        return array
    }

    override fun donationGold(uid: Int, armyId: Int, gold: Long): Int {
        var user = UserService.getUserMsgByUID(uid)
        var notArmy = Army()
        var army = ArmyAdminDao().getArmyInfo(armyId) ?: notArmy
        var coin = army.donate
        if (gold > user.coin) return 0
        army.donate = coin + gold
        UserDao().updateUserGold(user.coin - gold, uid)
        var re = ArmyAdminDao().updateArmyDonate(army.donate, armyId)
        return re
    }

    override fun donationDiamonds(uid: Int, armyId: Int, donategem: Long): Int {
        var user = UserService.getUserMsgByUID(uid)
        var notArmy = Army()
        var army = ArmyAdminDao().getArmyInfo(armyId) ?: notArmy
        var coin = army.donategem
        if (donategem > user.gem) return 0
        army.donate = coin + donategem
        UserDao().updateUserGem(user.gem - donategem, uid)
        var re = ArmyAdminDao().updateArmyDiamonds(army.donate, armyId)
        return re
    }

    override fun updateArmyIcon(icon: String, armyId: Int): Int {
        var re = ArmyAdminDao().updateArmyIcon(icon, armyId)
        return re
    }

    override fun updateArmyName(name: String, armyId: Int): Int {
        var re = ArmyAdminDao().updateArmyName(name, armyId)
        return re
    }

    override fun updateArmyAnnouncement(announcement: String, armyId: Int): Int {
        var re = ArmyAdminDao().updateArmyAnnouncement(announcement, armyId)
        return re
    }


    override fun userArmyInfo(uid: Int): ArmyUser {
        val armyUser = ArmyAdminDao().userArmyInfo(uid)
        return armyUser
    }

    override fun isJoinArmy(uid: Int): Int {
        var list = ArmyAdminDao().isJoinArmy(uid)
        if (list.size > 0) {
            return list.get(0).toInt()
        }
        return 0
    }

    override fun repeatJoinArmy(uid: Int, armyId: Int): Boolean {
        val list = ArmyAdminDao().repeatJoinArmy(uid)
        list.forEach {
            if (it == armyId) return true
        }
        return false
    }

    override fun getArmyUser(uid: Int): ArmyUser {
        val list = ArmyAdminDao().getArmyUser(uid)
        if (list.size > 0) return list[0] else return ArmyUser()
    }

    override fun getArmyAllPeople(armyId: Int): JSONArray {
        var list = ArmyAdminDao().getArmyAllPeople(armyId)
        var array = JSONArray()
        list.forEach {
            var json = JSONObject()
            json.put("id", it.id)
            json.put("uid", it.uid)
            json.put("name", it.name)
            json.put("icon", it.icon)
            json.put("state", isOnLine(it.uid))
            json.put("armyId", it.armyId)
            json.put("armyJob", it.armyJob)
            json.put("inerTime", it.inerTime)
            array.put(json)
        }
        return array
    }

    override fun updatePosition(uid: Int, armyId: Int, armyJob: Int): Int {
        var re = ArmyAdminDao().updatePosition(uid, armyId, armyJob)
        return re
    }

    override fun AgreeJoinArmy(uid: Int, armyId: Int): Int {
        var re = ArmyAdminDao().agreeJoinArmy(uid, armyId)
        return re
    }

    override fun ignoreJoinArmy(uid: Int, armyId: Int): Int {
        var re = ArmyAdminDao().ignoreJoinArmy(uid, armyId)
        return re
    }

    override fun proposeArmy(uid: Int, armyId: Int): Int {
        var re = ArmyAdminDao().proposeArmy(uid, armyId)
        return re
    }

    override fun delAllArmyUser(armyId: Int) {
        ArmyAdminDao().delAllArmyUser(armyId)
    }

    override fun distributionArmy(uid: Int, money: Long): Int {
        var user = UserDao().getUserMsgByUID(uid)
        val c = user.coin + money
        val re = UserDao().updateUserGold(c, uid)
        return re
    }

    override fun updateArmyUid(armyId: Int, uid: Int): Int {
        var re = ArmyAdminDao().updateArmyUid(armyId, uid)
        return re
    }

    override fun disDiamondsArmy(uid: Int, money: Long): Int {
        var user = UserDao().getUserMsgByUID(uid)
        val c = user.gem + money
        val re = UserDao().updateUserGem(c, uid)
        return re
    }

    override fun initArmyUser(armyUser: ArmyUser): Int {
        var re = ArmyAdminDao().insertArmyUser(armyUser)
        return re
    }

    override fun updateAarmyCoin(armyId: Int, money: Long): Int {
        var re = ArmyAdminDao().updateAarmyCoin(armyId, money)
        return re
    }

    override fun updateAarmyDiamonds(armyId: Int, money: Long): Int {
        var re = ArmyAdminDao().updateAarmyDiamonds(money, armyId)
        return re
    }

    override fun descCoin(armyId: Int): JSONArray {
        var array = JSONArray()
        ArmyAdminDao().descCoin(armyId).forEach {
            var json = JSONObject()
            json.put("name", it.name)
            json.put("icon", it.icon)
            json.put("armyJob", it.armyJob)
            json.put("coin", it.donationCoin)
            array.put(json)
        }
        return array
    }

    override fun armyProvisions(armyId: Int): String {
        val str = ArmyAdminDao().armyProvisions(armyId)
        return str
    }

    override fun armyExp(armyId: Int): List<armyExp> {
        val list = ArmyAdminDao().armyExp()
        return list
    }

    override fun currentNumberPeople(armyId: Int): Int {
        val i = ArmyAdminDao().currentNumberPeople(armyId)
        return i
    }

    override fun delSurplusApplyUser(uid: Int): Int {
        val list = ArmyAdminDao().repeatArmyUser(uid)
        for ((i, v) in list.withIndex()) {
            ArmyAdminDao().delSurplusApplyUser(v.id)
        }

        val relist = ArmyAdminDao().repeatArmyUser(uid)
        if (relist.size == 0) {
            return 0
        } else {
            for ((i, v) in relist.withIndex()) {
                ArmyAdminDao().delSurplusApplyUser(v.id)
            }
        }
        return relist.size
    }

    override fun armyNameCheck(name: String): Boolean {
        if (ArmyAdminDao().armyNameCheck(name) > 0) {
            return true
        }
        return false
    }

    override fun delArmyBuil(armyId: Int): Int {
        val re = ArmyAdminDao().delArmyBuil(armyId)
        return re
    }

    override fun queryArmyBuilType(armyId: Int, type: String): ArmyBuild {
        val armyBuild = ArmyAdminDao().queryArmyBuilType(armyId, type)

        return armyBuild
    }

    override fun delArmyFmcc(BuildId: Int): Int {
        val re = ArmyAdminDao().delArmyFmcc(BuildId)
        return re
    }

    override fun delArmyOrder(armyId: Int): Int {
        val re = ArmyAdminDao().delArmyOrder(armyId)
        return re
    }

    override fun delUserOrder(uid: Int): Int {
        val re = ArmyAdminDao().delUserOrder(uid)
        return re
    }

    override fun querywarAction(id: Int): ArmyAction {

        val r = ArmyAdminDao().queryArmyAction(id)
        return r
    }

    override fun updateCurrentNumberPeople(currentNumberPeople: String, armyId: Int): Int {
        val r = ArmyAdminDao().updateCurrentNumberPeople(currentNumberPeople, armyId)
        return r
    }

    override fun deleteArmyRankingAll(): Int {
        val r = ArmyAdminDao().deleteArmyRankingAll()
        return r
    }

    fun initFmcc(buildingFormwork: BuildingFormwork, id: Int): ArmyFmcc {
        var fmcc = ArmyFmcc()
        fmcc.name = buildingFormwork.name
        fmcc.buildId = id
        fmcc.addition = buildingFormwork.reward
        fmcc.level = buildingFormwork.level
        fmcc.type = buildingFormwork.type
        fmcc.fullgold = buildingFormwork.gold
        fmcc.nextAddition = buildingFormwork.nextReward
        fmcc.number = 0
        return fmcc
    }

    override fun updateArmyActionTime(id: Int, time: String) {
        ArmyAdminDao().updateArmyActionTime(id, time)
    }

    override fun updateArmyAction(armyAction: ArmyAction) {
        ArmyAdminDao().updateArmyAction(armyAction)
    }
}