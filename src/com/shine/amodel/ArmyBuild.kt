package com.shine.amodel

data class ArmyBuild(
        var id: Int = 0,
        var name: String = "",      //建筑名称
        var armyId: Int = 0,        //军团id
        var buildLevel: Int = 0,    //建筑物等级
        var centreId: Int = 0,      //军团中心
        var numberCoin: Long = 0,   //当前金币数量
        var maxNumberCoin: Long = 0,//满级数量
        var numberExp: Long = 0,    //军团积分数值
        var maxNumberExp: Long = 0, //满级积分数值
        var numberGem: Int = 0,     //钻石数值
        var maxNumberGem: Int = 0,  //升级钻石数量
        var type: String = "",      //建筑类型  （商店：shop，军团中心：center，军团作坊：workshop,科技树：techtree，军团战：warfare）
        var count: Int = 0          //相关数量(商店：道具数量，作坊：订单数量：军团战：出战次数：)
)