package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.ArmyOrder

class ArmyOrderDao : ArmyOrderMapper {
    val session = getSessionFactory().openSession()
    val armyOrderMapper = session.getMapper(ArmyOrderMapper::class.java)

    override fun selectArmyOrder(uid: Int): List<ArmyOrder> {
        val list = armyOrderMapper.selectArmyOrder(uid)
        session.close()
        return list
    }

    override fun selectArmyOrderOne(armyOrder: ArmyOrder): ArmyOrder {
        val obj = armyOrderMapper.selectArmyOrderOne(armyOrder)
        session.close()
        return obj
    }

    override fun insertArmyOrder(armyOrder: ArmyOrder): Int {
        val r = armyOrderMapper.insertArmyOrder(armyOrder)
        session.commit()
        session.close()
        return r
    }

    override fun updateArmyOrder(armyOrder: ArmyOrder): Int {
        val r = armyOrderMapper.updateArmyOrder(armyOrder)
        session.commit()
        session.close()
        return r
    }

    override fun deleteArmyOrder(armyOrder: ArmyOrder) {
        val r = armyOrderMapper.deleteArmyOrder(armyOrder)
        session.commit()
        session.close()
        return r
    }
}