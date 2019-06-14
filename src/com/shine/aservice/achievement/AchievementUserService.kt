package com.shine.aservice.achievement

import com.shine.amodel.AchievementUser
import com.shine.dao.AchievementUserDao


object AchievementUserService : IAchievementUserService {

    override fun selectOnline(achievementUser: AchievementUser): AchievementUser {
        var r: Any?
        try {
            r = AchievementUserDao().selectOnline(achievementUser)
        } catch (ex: Exception) {
            throw Exception(ex.message)
        }
        return r
    }

    override fun selectAchievementUser(achievementUser: AchievementUser): List<AchievementUser> {
        return AchievementUserDao().selectAchievementUser(achievementUser)
    }

    override fun selectAuserMsg(achievementUser: AchievementUser): List<AchievementUser> {
        return AchievementUserDao().selectAuserMsg(achievementUser)
    }

    override fun getAchievementCount(achievementUser: AchievementUser): List<AchievementUser> {
        return AchievementUserDao().getAchievementCount(achievementUser)
    }

    override fun selectAchievementUserOne(achievementUser: AchievementUser): AchievementUser {
        return AchievementUserDao().selectAchievementUserOne(achievementUser)
    }

    override fun insertAchievementUser(achievementUser: AchievementUser) {
        AchievementUserDao().insertAchievementUser(achievementUser)
        return
    }

    override fun updateAchievementUser(achievementUser: AchievementUser) {
        AchievementUserDao().updateAchievementUser(achievementUser)
        return
    }


    override fun deleteAchievementUser(achievementUser: AchievementUser) {
        AchievementUserDao().deleteAchievementUser(achievementUser)
        return
    }


}