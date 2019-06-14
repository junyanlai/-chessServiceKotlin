package com.shine.aservice.army

import com.shine.amodel.ArmyUser

interface IArmyUserService {
    //根据军团id 查询所有成员
    fun selectArmyUser(armyId:Int):List<ArmyUser>
    //查询一个人员
    fun selectArmyUserOne(armyUser: ArmyUser): ArmyUser
    //存入军团人员
    fun insertArmyUser(armyUser: ArmyUser)
    //修改人员
    fun updateArmyUser(armyUser: ArmyUser)
    //删除人员
    fun deleteArmyUser(armyUser: ArmyUser)

    fun selectArmyUserCount(armyUser: ArmyUser):Int


}