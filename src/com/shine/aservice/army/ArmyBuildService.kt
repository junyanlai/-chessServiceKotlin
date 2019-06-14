package com.shine.aservice.army

import com.shine.amodel.ArmyBuild
import com.shine.dao.ArmyBuildDao


object ArmyBuildService: IArmyBuildService {

    override fun updateArmyBuildByTemp(armyBuild: ArmyBuild) {
        ArmyBuildDao().updateArmyBuildByTemp(armyBuild)
    }

    override fun selectArmyBuildByArmyId(armyId: Int): List<ArmyBuild> {
        return ArmyBuildDao().selectArmyBuildByArmyId(armyId)
    }

    override fun selectArmyBuildOne(armyBuild: ArmyBuild): ArmyBuild {
        return ArmyBuildDao().selectArmyBuildOne(armyBuild)
    }

    override fun insertArmyBuild(armyBuild: ArmyBuild) {
        ArmyBuildDao().insertArmyBuild(armyBuild)
    }

    override fun updateArmyBuild(armyBuild: ArmyBuild) {
        ArmyBuildDao().updateArmyBuild(armyBuild)
    }

    override fun deleteArmyBuild(armyBuild: ArmyBuild) {
        ArmyBuildDao().deleteArmyBuild(armyBuild)
    }
}