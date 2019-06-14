package com.shine.aservice.user

import com.shine.amodel.User
import com.shine.dao.UserDao

object UserService : IUserService {
    override fun updateUserTittle(tittle: String, uid: Int): Int {
        val r = UserDao().updateUserTittle(tittle, uid)
        return r
    }

    override fun userAdd(user: User): Int {
        val r = UserDao().userAdd(user)
        return r
    }

    override fun deleteUser(user: User) {
        UserDao().deleteUser(user)
    }

    override fun userAddF(user: User): Int {
        val r = UserDao().userAddF(user)
        return r
    }

    override fun userCheckE(map: Map<String, Any>): Int {
        val r = UserDao().userCheckE(map)
        return r
    }

    override fun userGetByEmail(email: String): User {
        var user = UserDao().userInfoGetByEmail(email)
        return user
    }

    override fun userGetByDevice(device: String): User {
        var user = UserDao().userInfoGetByDevice(device)
        return user
    }

    override fun getEmailCount(email: String): Int {
        val r = UserDao().getEmailCount(email)
        return r
    }

    override fun getNickCount(nick: String): Int {
        val r = UserDao().getNickCount(nick)
        return r
    }

    override fun getDeviceCount(device: String): Int {
        val r = UserDao().getDeviceCount(device)
        return r
    }

    override fun userHashUpd(user: User): Int {
        val r = UserDao().userHashUpd(user)
        return r
    }

    override fun userNameGetByE(email: String): String {
        val str = UserDao().userNameGetByE(email)
        return str
    }

    override fun userIdGetUser(id: Int): User {
        val user = UserDao().queryById(id)
        return user
    }

    override fun countUser(): Int {
        val c = UserDao().countUser()
        return c
    }

    override fun queryRandomUser(index: Int, bar: Int): List<User> {
        val list = UserDao().queryRandomUser(index, bar)
        return list
    }


    override fun getUserMsgByUID(uid: Int): User {
        return UserDao().getUserMsgByUID(uid)
    }

    override fun updateUserMsgByUID(user: User): Int {
        var r = UserDao().updateUserMsgByUID(user)
        return r
    }

    override fun updateUserGem(gem: Long, uid: Int): Int {
        var r = UserDao().updateUserGem(gem, uid)
        return r
    }

    override fun updateUserCoin(coin: Long, uid: Int): Int {
        var r = 0
        if (coin > 0) {
            r = UserDao().updateUserGold(coin, uid)
        } else {
            r = UserDao().updateUserGold(0, uid)        //如果更新的金币为负数，就直接设置为0
        }
        return r
    }

    override fun updatAvatar(uid: Int, avatar: String): Int {
        var r = UserDao().updatAvatar(uid, avatar)
        return r
    }

    override fun getSystUser(): String {
        return UserDao().getSystUser()
    }

    override fun updatSex(uid: Int, sex: Int): Int {
        return UserDao().updatSex(uid, sex)
    }

    override fun updateUserName(name: String, uid: Int): Int {
        return UserDao().updateUserName(name, uid)
    }


    /**
     * 赠送礼物，更新金币或钻石
     * 1: 失败  2：成功
     */
    fun reduceCoinOrGem(uid: Int, type: String, monery: Long): Int {
        val user = getUserMsgByUID(uid)

        when (type) {
            "coin" -> {
                if (user.coin < monery) {
                    return 1
                } else {
                    if (updateUserCoin(user.coin - monery, uid) > 0) {
                        return 2
                    }
                }
            }
            "gem" -> {
                if (user.gem < monery) {
                    return 1
                } else {
                    if (updateUserGem(user.gem - monery, uid) > 0) {
                        return 2
                    }
                }

            }
        }
        return 1
    }
}