package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.AchievementUser

class AchievementUserDao : AchievementUserMapper {

    val session = getSessionFactory().openSession()
    val achievementUserMapper = session.getMapper(AchievementUserMapper::class.java)

    override fun selectAchievementUser(achievementUser: AchievementUser): List<AchievementUser> {
        val list = achievementUserMapper.selectAchievementUser(achievementUser)
        session.close()
        return list
    }

    override fun selectAchievementUserOne(achievementUser: AchievementUser): AchievementUser {
        val obj = achievementUserMapper.selectAchievementUserOne(achievementUser)
        session.close()
        return obj
    }

    override fun insertAchievementUser(achievementUser: AchievementUser) {
        val r = achievementUserMapper.insertAchievementUser(achievementUser)
        session.commit()
        session.close()
        return r
    }

    override fun updateAchievementUser(achievementUser: AchievementUser) {
        val r = achievementUserMapper.updateAchievementUser(achievementUser)
        session.commit()
        session.close()
        return r
    }

    override fun deleteAchievementUser(achievementUser: AchievementUser) {
        val r = achievementUserMapper.deleteAchievementUser(achievementUser)
        session.commit()
        session.close()
        return r
    }

    override fun getAchievementCount(achievementUser: AchievementUser): List<AchievementUser> {
        val list = achievementUserMapper.getAchievementCount(achievementUser)
        session.close()
        return list
    }

    override fun selectAccomplishAchievementUser(achievementUser: AchievementUser): List<Int> {
        val list = achievementUserMapper.selectAccomplishAchievementUser(achievementUser)
        session.close()
        return list
    }

    override fun selectAuserMsg(achievementUser: AchievementUser): List<AchievementUser> {
        val list = achievementUserMapper.selectAuserMsg(achievementUser)
        session.close()
        return list
    }

    override fun selectOnline(achievementUser: AchievementUser): AchievementUser {
        val obj = achievementUserMapper.selectOnline(achievementUser)
        session.close()
        return obj
    }
}