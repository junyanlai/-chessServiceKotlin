package com.shine.amodel


data class ArmyOrder(
        var id: Int = 0,
        var buildId:Int=0,
        var uid: Int = 0,               //用户id
        var name: String = "",          //订单名称
        var time: Long=0L,              //订单预计到期时间
        var armyId: Int = 0,            //军团id
        var orderLevel: Int = 0,        //订单等级
        var getProvisions: Int = 0,     //收获军粮
        var getGold: Int = 0,           //收获金币
        var status:Int   =0,            //订单状态  1完成 2进行中 0 未开始
        var fullGold:Int=0,             //升级金币
        var outDate:Int=0
)