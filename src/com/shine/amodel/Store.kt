package com.shine.amodel

data class Store(
        var sid:Int=0,
        var uid:Int=0 ,         //   用户id
        var commodityId:Int=0 ,          //   商品id
        var name:String="",
        var detail:String ="",
        var goodsCount:Int?=null,   //   商品数量
        var goodsState:Int=0 ,  //商品状态
        var goodsType:String ="",    //物品类型
        var priority:Int?=null       //作用于勋章物品  排级   123   0 无等级

)