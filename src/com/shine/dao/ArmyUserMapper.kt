package com.shine.dao

import com.shine.amodel.ArmyUser
import com.shine.amodel.Fmcc
import org.apache.ibatis.annotations.Mapper

@Mapper
interface ArmyUserMapper {

    //根据军团id 查询所有成员
    fun selectArmyUser(armyId:Int):List<ArmyUser>
    //查询一个人员
    fun selectArmyUserOne(armyUser:ArmyUser):ArmyUser
    //存入军团人员
    fun insertArmyUser(armyUser:ArmyUser)
    //修改人员
    fun updateArmyUser(armyUser:ArmyUser)
    //删除人员
    fun deleteArmyUser(armyUser:ArmyUser)

    fun selectArmyUserCount(armyUser:ArmyUser):Int

}