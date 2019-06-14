package com.shine.amodel

data class AchievementTemp(

        var atid: Int = 0,                   //成就模板id
        var name:String="",                  // 成就名称
        var commodityId:Int=0,                       // 成就奖励商品id
        var addExp:Double=0.000,              //积分加成
        var addGold:Double=0.000,             //金币加成
        var addOnline:Double=0.000,           //在线奖励加成
        var addDaily:Double=0.000,            //日常任务加成
        var addDiscount:Double=0.000,         //商城购买折扣
        var addLevel:Int=0,              //加成类型
        var type:String = "" ,               // 成就类型
        var goodsType:String="",             //奖励商品类型
        var triggerType:String="",          //触发类型
        var award:Int = 0,                   // 成就奖励数值
        var teps: Int = 0,                   // 成就达成数值
        var detail:String="",                // 成就描述
        var state:String = "" ,              // 成就状态
        var createDate: String ="",          // 创建时间
        var createUser: Int = 0             // 创建人

)

