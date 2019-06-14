package com.shine.aservice.army

import com.shine.amodel.ArmyOrder
import com.shine.dao.ArmyAdminDao
import com.shine.dao.ArmyOrderDao
import java.text.SimpleDateFormat
import java.util.*


object ArmyOrderService: IArmyOrderService {

    override fun selectArmyOrder(uid: Int): List<ArmyOrder> {
        return ArmyOrderDao().selectArmyOrder(uid)
    }

    override fun selectArmyOrderOne(armyOrder: ArmyOrder): ArmyOrder {
        return ArmyOrderDao().selectArmyOrderOne(armyOrder)
    }
    override fun insertArmyOrder(armyOrder: ArmyOrder): Int {
      var temp= ArmyAdminDao().getBuildingFormwork(type="myOrder",level=1)
        armyOrder.orderLevel=1
        armyOrder.getGold=temp.reward.toInt()
        armyOrder.getProvisions=temp.rations
        armyOrder.status=0
        armyOrder.name=temp.name
        armyOrder.fullGold=temp.gold.toInt()
//        armyOrder.time=get_closing_time(temp.outDate.toInt())
        armyOrder.outDate=temp.outDate.toInt()
        var r=ArmyOrderDao().insertArmyOrder(armyOrder)
        return r
    }

    override fun updateArmyOrder(armyOrder: ArmyOrder): Int {
        var temp=ArmyAdminDao().getBuildingFormwork(type="myOrder",level=armyOrder.orderLevel)
        if(temp ==null) return 0
        armyOrder.getGold=temp.reward.toInt()
        armyOrder.getProvisions=temp.rations
        armyOrder.name=temp.name
        armyOrder.time=get_closing_time(temp.outDate.toInt())
        armyOrder.fullGold=temp.gold.toInt()
        var r=ArmyOrderDao().updateArmyOrder(armyOrder)
        return r
    }

    override fun deleteArmyOrder(armyOrder: ArmyOrder) {
        ArmyOrderDao().deleteArmyOrder(armyOrder)
    }
    //获取预计到期时间
    fun get_closing_time(outDate:Int) :Long{
        var sp= SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val calendar = Calendar.getInstance()
        calendar.setTime(Date())
        calendar.add(Calendar.HOUR_OF_DAY,outDate)
        return calendar.time.time
    }

}