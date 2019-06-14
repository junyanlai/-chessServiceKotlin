package com.shine.aservice.user

import com.shine.amodel.User

interface IUserService {

    fun userAdd(user: User): Int                 //emailAdd

    fun userAddF(user: User): Int                //fastAdd

    fun userCheckE(uid: Map<String, Any>): Int   //email

    //fun userCheckF(uid: Map<String, Any>):Int   //deviceid switchHallin

    fun userGetByEmail(email: String): User       //user get by email

    fun userGetByDevice(device: String): User       //user get by email

    fun getEmailCount(email: String): Int         //use for sign check

    fun getNickCount(nick: String): Int           //use for sign check

    fun getDeviceCount(nick: String): Int         //use for device check

    fun userHashUpd(user: User): Int   //do update hash

    fun userNameGetByE(email: String): String    //get name by email
    fun userIdGetUser(id: Int): User    //根据ID，查找玩家记录
    fun countUser(): Int                    //查询一共有多少条记录
    fun queryRandomUser(index: Int, bar: Int): List<User> //从指定的行数查找指定的


    //根据id查询用户信息
    fun getUserMsgByUID(uid: Int): User

    //修改用户信息
    fun updateUserMsgByUID(user: User): Int

    fun deleteUser(user: User)

    fun updateUserGem(gem: Long, uid: Int): Int
    fun updateUserTittle(tittle: String, uid: Int): Int
    fun updateUserCoin(coin: Long, uid: Int): Int
    fun updatAvatar(uid: Int, avatar: String): Int
    fun getSystUser(): String
    fun updatSex(uid: Int, sex: Int): Int
    fun updateUserName(name: String, uid: Int): Int
}