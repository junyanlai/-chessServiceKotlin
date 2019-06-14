package com.shine.aservice.army

import com.shine.agent.Agent
import com.shine.amodel.ArmyOrder
import com.shine.amodel.Goods

interface IArmyOrderService {

    //用户id查询所有订单
    fun selectArmyOrder(uid:Int):List<ArmyOrder>
    //单个订单查询
    fun selectArmyOrderOne(armyOrder:ArmyOrder):ArmyOrder
    //存入订单
    fun insertArmyOrder(armyOrder:ArmyOrder):Int
    //修改订单信息
    fun updateArmyOrder(armyOrder:ArmyOrder):Int
    //删除订单
    fun deleteArmyOrder(armyOrder:ArmyOrder)

}