package com.shine.aservice.achievement

import com.shine.amodel.AchievementTemp
import com.shine.amodel.AchievementUser
import com.shine.dao.AchievementTempDao

object AchievementTempService: IAchievementTempService {
    override fun selectAllTitleByAtid(vipLevelId:Int,explevelId:Int): List<AchievementTemp> {
        var allChengHao= mutableListOf<AchievementTemp>()
            allChengHao.addAll(AchievementTempDao().selectAllTitleByAtid(vipLevelId))
            allChengHao.addAll(AchievementTempDao().selectAllTitleByAtid(explevelId))
        return allChengHao
    }


    override fun selectAchievementTempByTepsAll(achievementTemp: AchievementTemp): List<AchievementTemp> {
        return AchievementTempDao().selectAchievementTempByTepsAll(achievementTemp)
    }
    override fun selectAllTempMsg(achievementTemp: AchievementTemp): List<AchievementTemp> {
      return AchievementTempDao().selectAllTempMsg(achievementTemp)
    }
    override fun selectAtempByteps(achievementTemp: AchievementTemp): AchievementTemp {
        return AchievementTempDao().selectAtempByteps(achievementTemp)
    }

    override fun selectAchievementTempByTeps(achievementTemp: AchievementTemp): AchievementTemp {
        return AchievementTempDao().selectAchievementTempByTeps(achievementTemp)
    }

    override fun selectUnAchievementTemp(finishList: List<AchievementUser>): List<AchievementTemp> {
        return AchievementTempDao().selectUnAchievementTemp(finishList)
    }

    override fun selectAchievementTemp(achievementTemp: AchievementTemp): List<AchievementTemp> {
        return AchievementTempDao().selectAchievementTemp(achievementTemp)
    }

    override fun selectAchievementTempOne(achievementTemp: AchievementTemp): AchievementTemp {
        return AchievementTempDao().selectAchievementTempOne(achievementTemp)
    }

    override fun insertAchievementTemp(achievementTemp: AchievementTemp) {
        AchievementTempDao().insertAchievementTemp(achievementTemp)
        return
    }
    override fun updateAchievementTemp(achievementTemp: AchievementTemp) {
        AchievementTempDao().updateAchievementTemp(achievementTemp)
    }
    override fun deleteAchievementTemp(achievementTemp: AchievementTemp) {
        AchievementTempDao().deleteAchievementTemp(achievementTemp)
        return
    }


}