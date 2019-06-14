package com.shine.amodel

data class Recharge(

        var rid: Int = 0,                //用户成就id
        var uid:Int=0,                   // 用户id
        var money:Int=0,                 // 成就id
        var accruingAmounts:Int=0 ,      //累计金额
        var time:String="",              // 充值时间
        var device:String="",            //充值设备号
        var tradeSeq:String="",           //厂商交易序列号
        var facTradeSeq:String="",       //游戏交易序列号
        var payMentType:String="",       //付费方式
        var currency:String="",         //币别
        var myCardTradeNo:String="",    //交易方式类型
        var myCardType:String="",        //通路代码
        var promoCode:String="",       //活动代码
        var serialld:String=""         //连续扣款序号
        ,var customerId:String=""         //充值会员号
)


