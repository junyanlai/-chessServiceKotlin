package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.Recharge


class RechargeDao : RechargeMapper {

    val session = getSessionFactory().openSession()
    val rechargeMapper = session.getMapper(RechargeMapper::class.java)

    override fun selectRecharge(uid: Int): List<Recharge> {
        val list = rechargeMapper.selectRecharge(uid)
        session.close()
        return list
    }

    override fun selectRechargeOne(recharge: Recharge): Recharge {
        val obj = rechargeMapper.selectRechargeOne(recharge)
        session.close()
        return obj
    }

    override fun selectRechargeOneForCard(recharge: Recharge): Recharge {
        val obj = rechargeMapper.selectRechargeOneForCard(recharge)
        session.close()
        return obj
    }

    override fun insertRecharge(recharge: Recharge) {
        val r = rechargeMapper.insertRecharge(recharge)
        session.commit()
        session.close()
        return r
    }
}