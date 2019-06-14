package com.shine.aservice.achievement

import com.shine.amodel.AchievementUser

interface IAchievementUserService {


    fun selectAchievementUser(achievementUser: AchievementUser):List<AchievementUser>

    fun selectAchievementUserOne(achievementUser: AchievementUser): AchievementUser

    fun insertAchievementUser(achievementUser: AchievementUser): Unit

    fun updateAchievementUser(achievementUser: AchievementUser): Unit
    fun deleteAchievementUser(achievementUser: AchievementUser):Unit

   fun selectOnline(achievementUser:AchievementUser):AchievementUser

    fun getAchievementCount(achievementUser:AchievementUser):List<AchievementUser>
    fun selectAuserMsg(achievementUser:AchievementUser):List<AchievementUser>
}