package com.shine.dao

import com.shine.amodel.AchievementUser
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

@Mapper
interface AchievementUserMapper {


    fun selectAchievementUser(achievementUser: AchievementUser): List<AchievementUser>

    fun selectAchievementUserOne(achievementUser: AchievementUser): AchievementUser

    fun insertAchievementUser(achievementUser: AchievementUser)

    fun updateAchievementUser(achievementUser: AchievementUser)

    fun deleteAchievementUser(achievementUser: AchievementUser)

    //查询成就完成数量
    fun getAchievementCount(achievementUser: AchievementUser): List<AchievementUser>

    fun selectAccomplishAchievementUser(achievementUser: AchievementUser): List<Int>

    fun selectAuserMsg(achievementUser: AchievementUser): List<AchievementUser>

    fun selectOnline(achievementUser: AchievementUser): AchievementUser
}