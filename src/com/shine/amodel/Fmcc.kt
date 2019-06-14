package com.shine.amodel

data class Fmcc(


        var commodityId: Int = 0,//商品id
        var uid:Int=0,  //用户id
        var fid:Int=0,  //主键
        var goodsType:String = "" ,  //商品类型,,,,,,
        var type: String = "",//buf类型
        var status: Int = 0,  //buf状态
        var outDate: Int?=null, //有效期
        var outDateType:String="", //时间计算类型 （hour . num）
        var createDate: String = "",//创建时间
        var createUser: Int = 0,//创建者
        var addition:Double=0.0,   //加成（倍数/魅力值）
        var closingTime:String=""  //预计到时时间

)