package com.shine.aservice.shop

import com.shine.amodel.Recharge
import com.shine.dao.RechargeDao

object RechargeService : IRechargeService {

    override fun selectRechargeOneForCard(recharge: Recharge): Recharge {
        return RechargeDao().selectRechargeOneForCard(recharge)
    }

    override fun selectRecharge(uid: Int): List<Recharge> {
        return RechargeDao().selectRecharge(uid)
    }

    override fun selectRechargeOne(recharge: Recharge): Recharge {
        return RechargeDao().selectRechargeOne(recharge)
    }

    override fun insertRecharge(recharge: Recharge) {
        RechargeDao().insertRecharge(recharge)
    }
}