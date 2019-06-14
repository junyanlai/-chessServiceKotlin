package com.shine.amodel
data  class Goods(
        var gid:Int=0,              //   商品id
        var name:String="",         //   商品名称
        var type:String="",         //   商品类型(军团/商城)
        var otherType:String="",    //   商品附属类型
        var num:Int?=null,          //   商品数量
        var fashion:Int?=null,      //   商品魅力值
        var detail:String="",       //   商品描述
        var status:Int=1,           //   商品状态
        var price:Int=0,            //   商品价格
        var currency:String="",     //   商品货币购买种类 gem钻石  coin 金币
        var outDate:Int=0,          //   商品有效期
        var createDate:String?=null,//   商品创建时间
        var createUser:Int?=null,   //   商品创建人
        var buffType:String ="",    //   buf类型/金币经验 或者魅力值
        var attribute:Double =0.0,      //   道具属性值
        var commodityId:Int=0,      //   商品编码id
        var outDateType:String=""   //   商品有效期类型  时间/次数
        ,var goodsSex:Int=0

)