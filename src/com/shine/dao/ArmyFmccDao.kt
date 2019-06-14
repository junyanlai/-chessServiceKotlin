package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.ArmyFmcc


class ArmyFmccDao : ArmyFmccMapper {
    val session = getSessionFactory().openSession()
    val armyFmccMapper = session.getMapper(ArmyFmccMapper::class.java)

    override fun selectArmyFmcc(armyFmcc: ArmyFmcc): List<ArmyFmcc> {
        val list = armyFmccMapper.selectArmyFmcc(armyFmcc)
        session.close()
        return list
    }

    override fun selectArmyFmccOne(armyFmcc: ArmyFmcc): ArmyFmcc {
        val obj = armyFmccMapper.selectArmyFmccOne(armyFmcc)
        session.close()
        return obj
    }

    override fun insertArmyFmcc(armyFmcc: ArmyFmcc): Int {
        val r = armyFmccMapper.insertArmyFmcc(armyFmcc)
        session.commit()
        session.close()
        return r
    }

    override fun updateArmyFmcc(armyFmcc: ArmyFmcc): Int {
        val r = armyFmccMapper.updateArmyFmcc(armyFmcc)
        session.commit()
        session.close()
        return r
    }

    override fun deleteArmyFmcc(armyFmcc: ArmyFmcc) {
        val r = armyFmccMapper.deleteArmyFmcc(armyFmcc)
        session.commit()
        session.close()
        return r
    }

    override fun updateTreeGrade(armyFmcc: ArmyFmcc) {
        val r = armyFmccMapper.updateTreeGrade(armyFmcc)
        session.commit()
        session.close()
        return r
    }

    override fun selectArmyTemp(armyFmcc: ArmyFmcc): Int {
        val r = armyFmccMapper.selectArmyTemp(armyFmcc)
        session.close()
        return r
    }
}