package com.shine.dao

import com.shine.amodel.User
import com.shine.amodel.userCoin
import com.shine.amodel.userRanking
import org.apache.ibatis.annotations.*

@Mapper
interface UserMapper {

    @Insert("""
        INSERT INTO user(avatar,hash,nick,email,accountType,hardware,registerTime)
        VALUES  (#{avatar},#{hash},#{nick},#{email},'email',#{hardware},now())
    """)
    fun userAdd(user: User): Int

    @Insert("""
        INSERT INTO user(email,avatar,nick,accountType,device,hardware,registerTime)
        VALUES  (#{email},#{avatar},#{nick},'fast',#{device},#{hardware},now())
    """)
    fun userAddF(user: User): Int

    @Select("""
        SELECT count(*) FROM user WHERE email=#{email} AND hash=#{hash}
    """)
    fun userCheckE(map: Map<String, Any>): Int

    @Select("""
        SELECT count(*) FROM user WHERE device=#{device} AND hash=#{hash}
    """)
    fun userCheckF(map: Map<String, Any>): Int

    @Select("""
        SELECT uid,cid,avatar,nick,tittle,
            sex,level,vipLevel,exp,expFashion,
            accountType,
            medal0,medal1,medal2,
            coin,gem,bank,
            aid,rid,name,
            signTimes,lastSignDate,signCount
        FROM user WHERE email=#{email}
    """)
    fun userInfoGetByEmail(email: String): User

    @Select("""
        SELECT uid,cid,avatar,nick,tittle,
            sex,level,vipLevel,exp,expFashion,
            medal0,medal1,medal2,
            coin,gem,bank,
            aid,rid,name,
            signTimes,lastSignDate,signCount
        FROM user WHERE device=#{device}
    """)
    fun userInfoGetByDevice(device: String): User

    @Update("""UPDATE user SET hash=#{hash} WHERE email=#{email} """)
    fun userHashUpd(user: User): Int

    @Select("""SELECT name FROM user WHERE email=#{email} """)
    fun userNameGetByE(email: String): String

    @Select("""SELECT count(*) FROM user WHERE email=#{email} """)
    fun getEmailCount(email: String): Int

    @Select("""SELECT count(*) FROM user WHERE nick=#{nick}""")
    fun getNickCount(nick: String): Int

    @Select("""SELECT count(*) FROM user WHERE device=#{device}""")
    fun getDeviceCount(device: String): Int

    @Select("""select * from user where nick like "%"#{nick}"%" """)
    fun queryByName(nick: String): List<User>


    @Select("""select  * from  user where uid=#{id}""")
    fun queryById(id: Int): User

    @Select("""select count(*)from user""")
    fun countUser(): Int

    @Select("""select * from user  limit #{index},#{bar}""")
    fun queryRandomUser(index: Int, bar: Int): List<User>

    @Select("""select uid,avatar,nick,coin,expFashion from user WHERE uid!=1000&&uid!=1001""")
    fun queryByAll(): ArrayList<userRanking>

    @Select("""
        SELECT * FROM user WHERE uid=#{uid}
    """)
    fun getUserMsgByUID(uid: Int): User

    @Select("""
        SELECT avatar,nick FROM user WHERE uid=#{uid}
    """)
    fun getAvatar(uid: Int): userCoin

    //修改用户信息
    fun updateUserMsgByUID(user: User): Int

    fun deleteUser(user: User)

    @Update("""
            update user set coin=#{coin} where uid=#{uid}""")
    fun updateUserGold(@Param("coin") coin: Long, @Param("uid") uid: Int): Int


    @Update("""
                update user set gem=#{gem} where uid=#{uid}""")
    fun updateUserGem(@Param("gem") gem: Long, @Param("uid") uid: Int): Int

    @Update("""
                update user set avatar=#{avatar} where uid=#{uid}""")
    fun updatAvatar(@Param("uid") uid: Int, @Param("avatar") avatar: String): Int

    @Update("""
                update user set sex=#{sex} where uid=#{uid}""")
    fun updatSex(@Param("uid") uid: Int, @Param("sex") sex: Int): Int

    @Update("""
                update user set tittle=#{tittle} where uid=#{uid}""")
    fun updateUserTittle(@Param("tittle") tittle: String, @Param("uid") uid: Int): Int

    @Update("""
                update user set name=#{name} where uid=#{uid}""")
    fun updateUserName(@Param("name") name: String, @Param("uid") uid: Int): Int

    @Select("""SELECT  count(*) from user""")
    fun getSystUser(): String

}