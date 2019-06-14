package com.shine.amodel

data class BuildingFormwork(
        var id: Int = 0,
        var name: String = "",      //建筑名称
        var diamonds: Int = 0,      //达成等级需要的钻石
        var gold: Long = 0,         //达成等级需要的金币
        var level: Int = 0,         //建筑等级
        var reward: Double = 0.00,   //建筑达到等级开放的奖励
        var insufficient: Int = 0,  //人数要求
        var integral: Long = 0,     //升级军团所需要的积分
        var type: String = "",      //模板类型（建筑物区分）
        var supBuildLevel: Int = 0,  //上级建筑物等级
        var rations:Int=0,           //订单 奖励军粮
        var outDate:String="" ,      //订单到期时间 单位：小时
        var nextReward:Double=0.00  //下级加成
)