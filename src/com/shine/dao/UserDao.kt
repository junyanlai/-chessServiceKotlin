package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.User
import com.shine.amodel.userCoin
import com.shine.amodel.userRanking
import com.shine.controller.aHall.user

class UserDao : UserMapper {


    val session = getSessionFactory().openSession()
    val userMapper = session.getMapper(UserMapper::class.java)

    override fun updateUserTittle(tittle: String, uid: Int): Int {
        val r = userMapper.updateUserTittle(tittle, uid)
        session.commit()
        session.close()
        return r
    }

    override fun userAdd(user: User): Int {
        val r = userMapper.userAdd(user)
        session.commit()
        session.close()
        return r
    }

    override fun userAddF(user: User): Int {
        val r = userMapper.userAddF(user)
        session.commit()
        session.close()
        return r
    }

    override fun userCheckE(map: Map<String, Any>): Int {
        val r = userMapper.userCheckE(map)
        session.close()
        return r
    }

    override fun userCheckF(map: Map<String, Any>): Int {
        val r = userMapper.userCheckF(map)
        session.close()
        return r
    }

    override fun userInfoGetByEmail(email: String): User {
        val user = userMapper.userInfoGetByEmail(email)
        session.close()
        return user
    }

    override fun userInfoGetByDevice(device: String): User {
        val user = userMapper.userInfoGetByDevice(device)
        session.close()
        return user
    }

    override fun userHashUpd(user: User): Int {
        val r = userMapper.userHashUpd(user)
        session.commit()
        session.close()
        return r
    }

    override fun userNameGetByE(email: String): String {
        val str = userMapper.userNameGetByE(email)
        session.close()
        return str
    }

    override fun getEmailCount(email: String): Int {
        val r = userMapper.getEmailCount(email)
        session.close()
        return r
    }

    override fun getNickCount(nick: String): Int {
        val r = userMapper.getNickCount(nick)
        session.close()
        return r
    }

    override fun getDeviceCount(device: String): Int {
        val r = userMapper.getDeviceCount(device)
        session.close()
        return r
    }

    override fun queryByName(nick: String): List<User> {
        val list = userMapper.queryByName(nick)
        session.close()
        return list
    }

    override fun queryById(id: Int): User {
        val user = userMapper.queryById(id)
        session.close()
        return user
    }

    override fun countUser(): Int {
        val r = userMapper.countUser()
        session.close()
        return r
    }

    override fun queryRandomUser(index: Int, bar: Int): List<User> {
        val list = userMapper.queryRandomUser(index, bar)
        session.close()
        return list
    }

    override fun queryByAll(): ArrayList<userRanking> {
        val list = userMapper.queryByAll()
        session.close()
        return list
    }

    override fun getUserMsgByUID(uid: Int): User {
        val user = userMapper.getUserMsgByUID(uid)
        session.close()
        return user
    }

    override fun getAvatar(uid: Int): userCoin {
        val r = userMapper.getAvatar(uid)
        session.close()
        return r
    }

    override fun updateUserMsgByUID(user: User): Int {
        val r = userMapper.updateUserMsgByUID(user)
        session.commit()
        session.close()
        return r
    }

    override fun deleteUser(user: User) {
        val r = userMapper.deleteUser(user)
        session.commit()
        session.close()
        return r
    }

    override fun updateUserGold(coin: Long, uid: Int): Int {
        val r = userMapper.updateUserGold(coin, uid)
        session.commit()
        session.close()
        return r
    }

    override fun updateUserGem(gem: Long, uid: Int): Int {
        val r = userMapper.updateUserGem(gem, uid)
        session.commit()
        session.close()
        return r
    }

    override fun updatAvatar(uid: Int, avatar: String): Int {
        val r = userMapper.updatAvatar(uid, avatar)
        session.commit()
        session.close()
        return r
    }

    override fun getSystUser(): String {
        val r = userMapper.getSystUser()
        session.close()
        return r
    }

    override fun updatSex(uid: Int, sex: Int): Int {
        val r = userMapper.updatSex(uid, sex)
        session.commit()
        session.close()
        return r
    }

    override fun updateUserName(name: String, uid: Int): Int {
        val r = userMapper.updateUserName(name, uid)
        session.commit()
        session.close()
        return r
    }
}