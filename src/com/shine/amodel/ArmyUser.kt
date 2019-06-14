package com.shine.amodel

data class ArmyUser(


        var id: Int = 0,
        var uid: Int = 0,               //成员信息id
        var status: Int = 2,            //成员状态 1.进入军团。2.申请中
        var icon: String = "",          //用户头像
        var name: String = "",          //成员名称
        var armyId: Int = 0,            //军团id
        var armyJob: Int = 0,           //工会职位 0:成员 1：官员 2：军团长 -
        var donationCoin: Long = 0,     //捐赠金币数量
        var orderId: Int = 0,           //订单id 废弃
        var donationGem: Long = 0,      //捐赠钻石数量
        var inerTime: String = "",      //入团时间
        var getOrderTime:Long=0L        //订单获取时间
) {
    override fun toString(): String {
        return """{"id":${id},"uid":${uid},"status":${status},"name":${name},"armyId":${armyId},"armyJob":${armyJob},
            |"donationCoin":${donationCoin},"orderId":${orderId},"donationGem":${donationGem},"inerTime":${inerTime},"getOrderTime":${getOrderTime}}""".trimMargin()
    }
}


data class descCoin(val name: String = "", val icon: String, val armyJob: Int = 0, val donationCoin: Int = 0)