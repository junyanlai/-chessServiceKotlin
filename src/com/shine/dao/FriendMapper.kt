package com.shine.dao

import com.shine.amodel.Friend
import com.shine.amodel.User
import org.apache.ibatis.annotations.*


@Mapper
interface FriendMapper {
    @Select(
            """select * from friend where status=1 and (uid=#{uid} or fid=#{uid})""")
    fun queryOwnFriend(uid: Int): List<Friend>

    @Select("""select uid  from friend where status=#{status} and fid=#{fid}""")
    fun queryNotAgreeFriend(status: Int = 0, fid: Int): List<Long>

    @Select("""select uid  from friend where fid=#{fid} and status=0""")
    fun queryNotApplyFriend(fid: Int): List<Long>

    @Insert("""
        insert into friend(uid,fid,times,status,aid) VALUES (#{uid},#{fid},now(),#{status},#{aid})""")
    fun insertByFriend(friend: Friend): Int

    @Insert("""
        delete from friend where id=#{id}""")
    fun delByFrined(id: Int): Int

    @Update("""update friend set uid=#{uid} where id=#{id}""")
    fun updateByFrined(id: Int, user: User)

    @Update("""update friend set status=1 where uid=#{uid} and fid=#{fid}""")
    fun addByFrined(@Param("uid") uid: Int, @Param("fid") fid: Int): Int

    @Update("""delete from friend where status=1 and (uid=#{uid} and fid=#{fid}) or (uid=#{fid} and fid=#{uid})""")
    fun delFriend(@Param("uid") uid: Int, @Param("fid") fid: Int): Int

    @Select("""select status from friend where uid=#{uid}""")
    fun queryStatus(uid: Int): String

    @Select("""SELECT count(*) FROM friend WHERE (uid=#{uid} AND fid=#{fid}) OR (uid=#{fid} AND fid=#{uid})""")
    fun repeatCheck(@Param("uid") uid: Int, @Param("fid") fid: Int): Int
}