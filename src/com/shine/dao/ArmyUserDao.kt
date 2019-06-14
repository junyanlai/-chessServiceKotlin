package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.ArmyUser

class ArmyUserDao : ArmyUserMapper {

    val session = getSessionFactory().openSession()
    val armyUserMapper = session.getMapper(ArmyUserMapper::class.java)

    override fun selectArmyUser(armyId: Int): List<ArmyUser> {
        val list = armyUserMapper.selectArmyUser(armyId)
        session.close()
        return list
    }

    override fun selectArmyUserOne(armyUser: ArmyUser): ArmyUser {
        val obj = armyUserMapper.selectArmyUserOne(armyUser)
        session.close()
        return obj
    }

    override fun insertArmyUser(armyUser: ArmyUser) {
        val r = armyUserMapper.insertArmyUser(armyUser)
        session.commit()
        session.close()
        return r
    }

    override fun updateArmyUser(armyUser: ArmyUser) {
        val r = armyUserMapper.updateArmyUser(armyUser)
        session.commit()
        session.close()
        return r
    }

    override fun deleteArmyUser(armyUser: ArmyUser) {
        val r = armyUserMapper.deleteArmyUser(armyUser)
        session.commit()
        session.close()
        return r
    }

    override fun selectArmyUserCount(armyUser: ArmyUser): Int {
        val r = armyUserMapper.selectArmyUserCount(armyUser)
        session.close()
        return r
    }
}