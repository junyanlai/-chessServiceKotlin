package com.shine.dao

import com.shine.amodel.Recharge
import org.apache.ibatis.annotations.Mapper

@Mapper
interface RechargeMapper {
//

    fun selectRecharge(uid:Int):List<Recharge>

    fun selectRechargeOne(recharge: Recharge):Recharge

    fun selectRechargeOneForCard(recharge: Recharge):Recharge

    fun insertRecharge(recharge: Recharge)

}