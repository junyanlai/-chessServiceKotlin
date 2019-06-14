package com.shine.amodel

data class ArmyFmcc(


        var id: Int = 0,
        var name: String = "",          //加成名称
        var buildId: Int = 0,            //建筑id
        var addition: Double=0.000,           //加成
        var level: Int = 0,            //buff等级
        var type:String ="",            //buf类型（科技树/订单）
        var number: Int? =null,           //现有金币
        var fullgold: Long = 0  ,       //满级金币需求
        var nextAddition:Double=0.000  //下级加成信息

)