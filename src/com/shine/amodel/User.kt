package com.shine.amodel

data class User(

        var uid: Int = 0,             // 用户id 如果是机器人则为1
        var cid: Int = 0,         //频道id

        var avatar: String = "",      //头像
        var hash: String = "",      //密码
        var nick: String = "",      //昵称
        var name: String = "",      //实名？
        var tittle: String = "",       //称号
        var line: Int = 0,           //在线？ 已弃用

        var email: String? = "",    //邮箱
        var phone: String? = "",    ///手机
        var accountType: String = "",   //邮箱账户类型google,fb,yahoo
        var accountGG: String? = "",    //邮箱
        var accountFB: String? = "",    //邮箱
        var accountYH: String? = "",    //邮箱
        var device: String = "",        //设备id   device id
        var hardware: String = "",      //设备类型 device type

        var sex: Int = 0,               //性别 0：无性别 1：男 2：女
        var level: Int = 16,         //等级
        var vipLevel: Int = 0,      //vip等级
        var exp: Long = 0,          ///经验
        var expFashion: Long? = null,   //魅力值
        var medal0: Int? = null,        //累计交易金额
        var medal1: Int = 0,        //是否初始化成就表
        var medal2: Int = 0,        //每月1日清空月累计签到记录(参数类型 int类型的 年月日：201891)

        var coin: Long = 0,         //金币
        var gem: Long = 0,          //钻石
        var bank: Long = 0,         //银行

        var aid: Int = 0,           //军团id  Army idv /.
        var rid: Int = 0,           //房间id  (弱引用)room id  /

        var registerTime: String = "",
        var loginTime: String = "",
        var birthday: String = "",

        var signTimes: Int? = null,            //.
        var lastSignDate: String = "",     //.
        var signCount: Int? = null,       //签到次数
        var packSize: Int = 99,            //背包大小

        var gameCoin: Long = 0
) {
    var dice: IntArray? = null
}

