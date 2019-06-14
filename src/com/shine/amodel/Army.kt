package com.shine.amodel

data class Army(
        var id: Int = 0,
        var adminid: Int = 0,               //管理员ID
        var level: Int = 0,                //军团等级
        var num: Int = 20,                 //军团上限人数
        var icon: String = "110",          //军团图标，不能为空。联系前端确定一个初始值
        var name: String = "",             //军团名称
        var announcement: String = "",     //军团宣言
        var armyTitle: String = "0",       //军团称号，有初始化称号
        var donate: Long = 0,               //捐赠的金币
        var donategem: Long = 0,            //捐赠的钻石
        var competcoin: Long = 0,           //军团赛获得金币
        var competgem: Long = 0,            //军团赛获得钻石
        var grain: Long = 100,              //军粮
        var createDate: String = "",        //军团创建日期
        var currentNumberPeople: Int = 0,   //军团当前人数
        var armyJob: Int = 0                   //军团职位
)

data class userRanking(
        val uid: Int = 1000,
        val avatar: String,
        val nick: String,
        val coin: Long,
        val expFashion: Int = 0)

data class userCoin(
        val avatar: String,
        val nick: String)

data class armyExp(
        val armyId: Int,
        val numberExp: Int)

data class armyByNmae(
        val icon: String,
        val name: String)