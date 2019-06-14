package com.shine.aservice.achievement

import com.shine.amodel.AchievementTemp
import com.shine.amodel.AchievementUser

interface IAchievementTempService {




    fun selectAchievementTemp(achievementTemp: AchievementTemp):List<AchievementTemp>

    fun selectAchievementTempOne(achievementTemp: AchievementTemp): AchievementTemp

    //查询所有一级成就
    fun selectAllTempMsg(achievementTemp: AchievementTemp):List<AchievementTemp>
    fun selectAchievementTempByTeps(achievementTemp:AchievementTemp):AchievementTemp

    fun selectAtempByteps(achievementTemp:AchievementTemp):AchievementTemp
    fun selectAchievementTempByTepsAll(achievementTemp:AchievementTemp):List<AchievementTemp>

    fun insertAchievementTemp(achievementTemp: AchievementTemp)

    fun updateAchievementTemp(achievementTemp:AchievementTemp)

    fun deleteAchievementTemp(achievementTemp: AchievementTemp)

    fun selectUnAchievementTemp(finishList: List<AchievementUser>):List<AchievementTemp>

    fun selectAllTitleByAtid(expLevelId:Int,levelId:Int):List<AchievementTemp>
}