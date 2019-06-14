package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.*

class ArmyAdminDao : ArmyAdminMapper {

    val session = getSessionFactory().openSession()
    val armyAdminMapper = session.getMapper(ArmyAdminMapper::class.java)

    override fun selectAllArmyInfo(): List<Army> {
        val list = armyAdminMapper.selectAllArmyInfo()
        session.close()
        return list
    }

    override fun insertOneArmy(army: Army): Int {
        val r = armyAdminMapper.insertOneArmy(army)
        session.commit()
        session.close()
        return r
    }

    override fun createArmyBuild(armyBuild: ArmyBuild): Int {
        val r = armyAdminMapper.createArmyBuild(armyBuild)
        session.commit()
        session.close()
        return r
    }


    override fun selectNameByArmy(name: String): List<Army> {
        val r = armyAdminMapper.selectNameByArmy(name)
        session.close()
        return r
    }

    override fun getArmyInfo(id: Int): Army {
        val r = armyAdminMapper.getArmyInfo(id)
        session.close()
        return r
    }

    override fun armyByName(id: Int): armyByNmae {
        val r = armyAdminMapper.armyByName(id)
        session.close()
        return r
    }

    override fun getArmyInfoString(id: String): Army {
        val army = armyAdminMapper.getArmyInfoString(id)
        session.close()
        return army
    }

    override fun deleteArmy(id: Int): Int {
        val r = armyAdminMapper.deleteArmy(id)
        session.commit()
        session.close()
        return r
    }

    override fun getBuildingFormwork(type: String, level: Int): BuildingFormwork {
        val obj = armyAdminMapper.getBuildingFormwork(type, level)
        session.close()
        return obj
    }

    override fun insertArmyUser(armyUser: ArmyUser): Int {
        val r = armyAdminMapper.insertArmyUser(armyUser)
        session.commit()
        session.close()
        return r
    }

    override fun deleteArmyUser(uid: Int, armyId: Int): Int {
        val r = armyAdminMapper.deleteArmyUser(uid, armyId)
        session.commit()
        session.close()
        return r
    }

    override fun applyArmyList(armyId: Int): List<ArmyUser> {
        val r = armyAdminMapper.applyArmyList(armyId)
        session.close()
        return r
    }

    override fun updateArmyDonate(donate: Long, armyId: Int): Int {
        val r = armyAdminMapper.updateArmyDonate(donate, armyId)
        session.commit()
        session.close()
        return r
    }

    override fun updateArmyDiamonds(donategem: Long, armyId: Int): Int {
        val r = armyAdminMapper.updateArmyDiamonds(donategem, armyId)
        session.commit()
        session.close()
        return r
    }

    override fun updateAarmyCoin(armyId: Int, competcoin: Long): Int {
        val r = armyAdminMapper.updateAarmyCoin(armyId, competcoin)
        session.commit()
        session.close()
        return r
    }

    override fun getArmyBuiling(armyId: Int): List<ArmyBuild> {
        val list = armyAdminMapper.getArmyBuiling(armyId)
        session.close()
        return list
    }

    override fun updateAarmyDiamonds(competgem: Long, armyId: Int): Int {
        val r = armyAdminMapper.updateAarmyDiamonds(competgem, armyId)
        session.commit()
        session.close()
        return r
    }

    override fun updateArmyIcon(icon: String, armyId: Int): Int {
        val r = armyAdminMapper.updateArmyIcon(icon, armyId)
        session.commit()
        session.close()
        return r
    }

    override fun updateArmyName(name: String, armyId: Int): Int {
        val r = armyAdminMapper.updateArmyName(name, armyId)
        session.commit()
        session.close()
        return r
    }

    override fun updateArmyAnnouncement(announcement: String, armyId: Int): Int {
        val r = armyAdminMapper.updateArmyAnnouncement(announcement, armyId)
        session.commit()
        session.close()
        return r
    }

    override fun updateCurrentNumberPeople(currentNumberPeople: String, armyId: Int): Int {
        val r = armyAdminMapper.updateCurrentNumberPeople(currentNumberPeople, armyId)
        session.commit()
        session.close()
        return r
    }

    override fun updategrain(grain: Long, armyId: Int): Int {
        val r = armyAdminMapper.updategrain(grain, armyId)
        session.commit()
        session.close()
        return r
    }

    override fun userArmyInfo(uid: Int): ArmyUser {
        val user = armyAdminMapper.userArmyInfo(uid)
        session.close()
        return user
    }

    /**
     * 可能存在多条记录，但那样是错误的，只能先取一条
     */
    override fun isJoinArmy(uid: Int): List<String> {
        val list = armyAdminMapper.isJoinArmy(uid)
        session.close()
        return list
    }

    override fun repeatJoinArmy(uid: Int): List<Int> {
        val list = armyAdminMapper.repeatJoinArmy(uid)
        session.close()
        return list
    }

    override fun getArmyUser(uid: Int): List<ArmyUser> {
        val list = armyAdminMapper.getArmyUser(uid)
        return list
        session.close()
    }

    override fun insertArmyFmcc(armyFmcc: ArmyFmcc): Int {
        val r = armyAdminMapper.insertArmyFmcc(armyFmcc)
        session.commit()
        session.close()
        return r
    }

    override fun getArmyAllPeople(armyId: Int): List<ArmyUser> {
        val list = armyAdminMapper.getArmyAllPeople(armyId)
        session.close()
        return list
    }

    override fun updatePosition(uid: Int, armyId: Int, armyJob: Int): Int {
        val r = armyAdminMapper.updatePosition(uid, armyId, armyJob)
        session.commit()
        session.close()
        return r
    }

    override fun agreeJoinArmy(uid: Int, armyId: Int): Int {
        val r = armyAdminMapper.agreeJoinArmy(uid, armyId)
        session.commit()
        session.close()
        return r
    }

    override fun ignoreJoinArmy(uid: Int, armyId: Int): Int {
        val r = armyAdminMapper.ignoreJoinArmy(uid, armyId)
        session.commit()
        session.close()
        return r
    }

    override fun proposeArmy(uid: Int, armyId: Int): Int {
        val r = armyAdminMapper.proposeArmy(uid, armyId)
        session.commit()
        session.close()
        return r
    }

    override fun delAllArmyUser(armyId: Int) {
        val r = armyAdminMapper.delAllArmyUser(armyId)
        session.commit()
        session.close()
        return r
    }

    override fun updateArmyUid(armyId: Int, uid: Int): Int {
        val r = armyAdminMapper.updateArmyUid(armyId, uid)
        session.commit()
        session.close()
        return r
    }

    override fun descCoin(armyId: Int): List<descCoin> {
        val list = armyAdminMapper.descCoin(armyId)
        session.close()
        return list
    }

    override fun armyProvisions(armyId: Int): String {
        val r = armyAdminMapper.armyProvisions(armyId)
        session.close()
        return r
    }

    override fun insertRanking(r: ArmyRanking): Int {
        val r = armyAdminMapper.insertRanking(r)
        session.commit()
        session.close()
        return r
    }

    override fun queryAll(): List<ArmyRanking> {
        val list = armyAdminMapper.queryAll()
        session.close()
        return list
    }

    override fun updateRanking(id: Int, score: Long): Int {
        val r = armyAdminMapper.updateRanking(id, score)
        session.commit()
        session.close()
        return r
    }

    override fun queryByArmyId(id: Int): ArmyRanking {
        val obj = armyAdminMapper.queryByArmyId(id)
        session.close()
        return obj
    }

    override fun armyExp(): List<armyExp> {
        val list = armyAdminMapper.armyExp()
        session.close()
        return list
    }

    override fun currentNumberPeople(armyId: Int): Int {
        val r = armyAdminMapper.currentNumberPeople(armyId)
        session.close()
        return r
    }

    override fun repeatArmyUser(uid: Int): List<ArmyUser> {
        val list = armyAdminMapper.repeatArmyUser(uid)
        session.close()
        return list
    }

    override fun delSurplusApplyUser(id: Int): Int {
        val r = armyAdminMapper.delSurplusApplyUser(id)
        session.commit()
        session.close()
        return r
    }

    override fun armyNameCheck(name: String): Int {
        val r = armyAdminMapper.armyNameCheck(name)
        session.close()
        return r
    }

    override fun delArmyBuil(armyId: Int): Int {
        val r = armyAdminMapper.delArmyBuil(armyId)
        session.commit()
        session.close()
        return r
    }

    override fun queryArmyBuilType(armyId: Int, type: String): ArmyBuild {
        val obj = armyAdminMapper.queryArmyBuilType(armyId, type)
        session.close()
        return obj
    }

    override fun delArmyFmcc(buildId: Int): Int {
        val r = armyAdminMapper.delArmyFmcc(buildId)
        session.commit()
        session.close()
        return r
    }

    override fun delArmyOrder(armyId: Int): Int {
        val r = armyAdminMapper.delArmyOrder(armyId)
        session.commit()
        session.close()
        return r
    }

    override fun delUserOrder(uid: Int): Int {
        val r = armyAdminMapper.delUserOrder(uid)
        session.commit()
        session.close()
        return r
    }

    override fun queryArmyAction(id: Int): ArmyAction {
        val obj = armyAdminMapper.queryArmyAction(id)
        session.close()
        return obj
    }

    override fun updateArmyAction(armyAction: ArmyAction): Int {
        val r = armyAdminMapper.updateArmyAction(armyAction)
        session.commit()
        session.close()
        return r
    }

    override fun updateArmyActionTime(id: Int, time: String): Int {
        val r = armyAdminMapper.updateArmyActionTime(id, time)
        session.commit()
        session.close()
        return r
    }

    override fun deleteArmyRankingAll(): Int {
        val r = armyAdminMapper.deleteArmyRankingAll()
        session.commit()
        session.close()
        return r
    }


}