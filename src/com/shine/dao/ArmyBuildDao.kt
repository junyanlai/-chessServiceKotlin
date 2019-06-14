package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.ArmyBuild

class ArmyBuildDao : ArmyBuildMapper {

    val session = getSessionFactory().openSession()
    val armyBuildMapper = session.getMapper(ArmyBuildMapper::class.java)

    override fun selectArmyBuildByArmyId(armyId: Int): List<ArmyBuild> {
        val list = armyBuildMapper.selectArmyBuildByArmyId(armyId)
        session.close()
        return list
    }

    override fun selectArmyBuildOne(armyBuild: ArmyBuild): ArmyBuild {
        val obj = armyBuildMapper.selectArmyBuildOne(armyBuild)
        session.close()
        return obj
    }

    override fun insertArmyBuild(armyBuild: ArmyBuild) {
        val r = armyBuildMapper.insertArmyBuild(armyBuild)
        session.commit()
        session.close()
        return r
    }

    override fun updateArmyBuild(armyBuild: ArmyBuild) {
        val r = armyBuildMapper.updateArmyBuild(armyBuild)
        session.commit()
        session.close()
        return r
    }

    override fun deleteArmyBuild(armyBuild: ArmyBuild) {
        val r = armyBuildMapper.deleteArmyBuild(armyBuild)
        session.commit()
        session.close()
        return r
    }

    override fun updateArmyBuildByTemp(armyBuild: ArmyBuild) {
        val r = armyBuildMapper.updateArmyBuildByTemp(armyBuild)
        session.commit()
        session.close()
        return r
    }
}