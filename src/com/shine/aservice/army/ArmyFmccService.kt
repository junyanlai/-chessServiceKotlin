package com.shine.aservice.army

import com.shine.amodel.ArmyFmcc
import com.shine.dao.ArmyFmccDao

object ArmyFmccService: IArmyFmccService {
    

    override fun selectArmyTemp(armyFmcc: ArmyFmcc): Int {
        return ArmyFmccDao().selectArmyTemp(armyFmcc)
    }
    override fun updateTreeGrade(armyFmcc: ArmyFmcc) {
        ArmyFmccDao().updateTreeGrade(armyFmcc)
    }

    override fun selectArmyFmcc(armyFmcc:ArmyFmcc): List<ArmyFmcc> {
          return ArmyFmccDao().selectArmyFmcc(armyFmcc)
    }

    override fun selectArmyFmccOne(armyFmcc: ArmyFmcc): ArmyFmcc {
        return ArmyFmccDao().selectArmyFmccOne(armyFmcc)
    }

    override fun insertArmyFmcc(armyFmcc: ArmyFmcc):Int {
        var r =ArmyFmccDao().insertArmyFmcc(armyFmcc)
        return r
    }

    override fun updateArmyFmcc(armyFmcc: ArmyFmcc):Int {
        var r=ArmyFmccDao().updateArmyFmcc(armyFmcc)
        return r
    }

    override fun deleteArmyFmcc(armyFmcc: ArmyFmcc) {
        ArmyFmccDao().deleteArmyFmcc(armyFmcc)
    }





}