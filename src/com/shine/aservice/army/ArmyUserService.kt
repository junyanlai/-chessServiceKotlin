package com.shine.aservice.army

import com.shine.amodel.ArmyUser
import com.shine.dao.ArmyUserDao

object ArmyUserService : IArmyUserService {

    override fun selectArmyUser(armyId: Int): List<ArmyUser> {
        return ArmyUserDao().selectArmyUser(armyId)
    }

    override fun selectArmyUserOne(armyUser: ArmyUser): ArmyUser {
        return ArmyUserDao().selectArmyUserOne(armyUser)
    }

    override fun insertArmyUser(armyUser: ArmyUser) {
        ArmyUserDao().insertArmyUser(armyUser)
    }

    override fun updateArmyUser(armyUser: ArmyUser) {
        ArmyUserDao().updateArmyUser(armyUser)
    }

    override fun deleteArmyUser(armyUser: ArmyUser) {
        ArmyUserDao().deleteArmyUser(armyUser)
    }

    override fun selectArmyUserCount(armyUser: ArmyUser): Int {
        return ArmyUserDao().selectArmyUserCount(armyUser)
    }


}