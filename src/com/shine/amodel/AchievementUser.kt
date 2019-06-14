package com.shine.amodel

data class AchievementUser(

        var auid: Int = 0,                   //用户成就id
        var uid:Int=0,                       // 用户id
        var atid:Int=0,                      // 成就id
        var atype:String="",                 // 成就完成类型
        var aname:String ="",
        var teps:Int = 0 ,                   // 成就完成度
        var isCreated: Int?=null,              // 是否领取
        var isfulfill:Int?=null,                 //是否完成
        var time: Int = 0,                   // 达成时间
        var createDate: String ="",          // 创建时间
        var createUser: String = "",          // 创建人
        var triggerType:String="",
        var nextTeps:Int=0                   //达成奖励的数值

)