package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.AchievementTemp
import com.shine.amodel.AchievementUser

class AchievementTempDao : AchievementTempMapper {


    val session = getSessionFactory().openSession()
    val achievementTempMapper = session.getMapper(AchievementTempMapper::class.java)

    override fun selectAchievementTemp(achievementTemp: AchievementTemp): List<AchievementTemp> {
        val list = achievementTempMapper.selectAchievementTemp(achievementTemp)
        session.close()
        return list
    }
    override fun selectAllTitleByAtid(atid: Int):List<AchievementTemp> {
        val list = achievementTempMapper.selectAllTitleByAtid(atid)
        session.close()
        return list
    }
    override fun selectAllTempMsg(achievementTemp: AchievementTemp): List<AchievementTemp> {
        val list = achievementTempMapper.selectAllTempMsg(achievementTemp)
        session.close()
        return list
    }

    override fun selectAchievementTempOne(achievementTemp: AchievementTemp): AchievementTemp {
        val obj = achievementTempMapper.selectAchievementTempOne(achievementTemp)
        session.close()
        return obj
    }

    override fun selectAchievementTempByTeps(achievementTemp: AchievementTemp): AchievementTemp {
        val obj = achievementTempMapper.selectAchievementTempByTeps(achievementTemp)
        session.close()
        return obj
    }

    override fun selectAtempByteps(achievementTemp: AchievementTemp): AchievementTemp {
        val obj = achievementTempMapper.selectAtempByteps(achievementTemp)
        session.close()
        return obj
    }

    override fun selectAchievementTempByTepsAll(achievementTemp: AchievementTemp): List<AchievementTemp> {
        val list = achievementTempMapper.selectAchievementTempByTepsAll(achievementTemp)
        session.close()
        return list
    }

    override fun insertAchievementTemp(achievementTemp: AchievementTemp) {
        val r = achievementTempMapper.insertAchievementTemp(achievementTemp)
        session.commit()
        session.close()
        return r
    }

    override fun updateAchievementTemp(achievementTemp: AchievementTemp) {
        val r = achievementTempMapper.updateAchievementTemp(achievementTemp)
        session.commit()
        session.close()
        return r
    }

    override fun deleteAchievementTemp(achievementTemp: AchievementTemp) {
        val r = achievementTempMapper.deleteAchievementTemp(achievementTemp)
        session.commit()
        session.close()
        return r
    }

    override fun selectUnAchievementTemp(finishList: List<AchievementUser>): List<AchievementTemp> {
        val list = achievementTempMapper.selectUnAchievementTemp(finishList)
        session.close()
        return list
    }
}