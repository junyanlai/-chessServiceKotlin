package com.shine.aservice.shop

import com.shine.amodel.Recharge

interface IRechargeService {




    fun selectRecharge(uid:Int):List<Recharge>

    fun selectRechargeOne(recharge: Recharge): Recharge

    fun selectRechargeOneForCard(recharge: Recharge):Recharge

    fun insertRecharge(recharge: Recharge)

}