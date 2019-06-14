package com.shine.dao

import com.shine.amodel.AchievementTemp
import com.shine.amodel.AchievementUser
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

@Mapper
interface AchievementTempMapper {


    fun selectAchievementTemp(achievementTemp: AchievementTemp):List<AchievementTemp>
    //查询所有一级成就
    fun selectAllTempMsg(achievementTemp: AchievementTemp):List<AchievementTemp>

    fun selectAchievementTempOne(achievementTemp:AchievementTemp):AchievementTemp
//查询称号等级
    fun selectAchievementTempByTeps(achievementTemp:AchievementTemp):AchievementTemp
    fun selectAtempByteps(achievementTemp:AchievementTemp):AchievementTemp
    fun selectAchievementTempByTepsAll(achievementTemp:AchievementTemp):List<AchievementTemp>

    fun insertAchievementTemp(achievementTemp:AchievementTemp)

    fun updateAchievementTemp(achievementTemp:AchievementTemp)

    fun deleteAchievementTemp(achievementTemp:AchievementTemp)

    fun selectUnAchievementTemp(finishList: List<AchievementUser>):List<AchievementTemp>

    @Select("""select * from achievement_temp where atid<=#{atid} and triggerType=(select triggerType from achievement_temp where atid=#{atid} )""")
    fun selectAllTitleByAtid(@Param("atid") atid:Int):List<AchievementTemp>
}