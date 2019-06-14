package com.shine.aservice.army

import com.shine.amodel.*
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Update
import org.apache.ibatis.session.SqlSession
import org.json.JSONArray
import org.json.JSONObject

interface IArmyAdminService {
    fun recommendArmy(): JSONArray

    fun createArmy(army: Army): Int

    fun queryNameByArmy(armyName: String): JSONArray

    fun applyArmy(uid: Int, name: String, armyId: Int, icon: String): Int

    fun getArmyInfo(id: Int): Army

    fun dissolutionArmy(id: Int): Int

    fun quitArmy(uid: Int, armyId: Int): Int

    fun applyArmyList(armyId: Int): JSONArray

    fun donationGold(uid: Int, armyId: Int, gold: Long): Int

    fun donationDiamonds(uid: Int, armyId: Int, donategem: Long): Int

    fun updateArmyIcon(icon: String, armyId: Int): Int

    fun updateArmyName(name: String, armyId: Int): Int

    fun updateArmyAnnouncement(icon: String, armyId: Int): Int

    fun updategrain(grain: Long, armyId: Int): Int

    fun userArmyInfo(uid: Int): ArmyUser

    fun isJoinArmy(uid: Int): Int

    fun repeatJoinArmy(uid: Int, armyId: Int): Boolean

    fun getArmyUser(uid: Int): ArmyUser

    fun getArmyAllPeople(armyId: Int): JSONArray

    fun updatePosition(uid: Int, armyId: Int, armyJob: Int): Int

    fun AgreeJoinArmy(uid: Int, armyId: Int): Int

    fun ignoreJoinArmy(uid: Int, armyId: Int): Int

    fun proposeArmy(uid: Int, armyId: Int): Int

    fun delAllArmyUser(armyId: Int)

    fun distributionArmy(uid: Int, money: Long): Int

    fun disDiamondsArmy(uid: Int, money: Long): Int

    fun updateArmyUid(armyId: Int, uid: Int): Int

    fun initArmyUser(armyUser: ArmyUser): Int

    fun updateAarmyCoin(armyId: Int, money: Long): Int

    fun updateAarmyDiamonds(armyId: Int, money: Long): Int

    fun descCoin(armyId: Int): JSONArray

    fun armyProvisions(armyId: Int): String

    fun armyExp(armyId: Int): List<armyExp>

    fun currentNumberPeople(armyId: Int): Int

    fun delSurplusApplyUser(uid: Int): Int

    fun armyNameCheck(name: String): Boolean

    fun delArmyBuil(armyId: Int): Int

    fun queryArmyBuilType(armyId: Int, type: String): ArmyBuild

    fun delArmyFmcc(BuildId: Int): Int

    fun delArmyOrder(armyId: Int): Int

    fun delUserOrder(uid: Int): Int

    fun querywarAction(id: Int): ArmyAction

    fun updateCurrentNumberPeople(currentNumberPeople: String, armyId: Int): Int

    fun deleteArmyRankingAll(): Int
    fun updateArmyActionTime(id: Int, time: String)

    fun updateArmyAction(armyAction: ArmyAction)
}