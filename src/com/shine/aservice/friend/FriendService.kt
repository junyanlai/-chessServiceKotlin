package com.shine.aservice.friend

import com.shine.amodel.Friend
import com.shine.amodel.User
import com.shine.dao.FriendDao
import com.shine.dao.UserDao
import kotlin.collections.HashSet

object FriendService : IFriendService {

    override fun lookUpFriend(name: String): List<User> {
        var list: List<User> =UserDao().queryByName(name)
        return list
    }

    override fun queryOwnFriend(uid: Int): HashSet<Int> {
        var set = HashSet<Int>()
        var list = FriendDao().queryOwnFriend(uid)

        for ((c, v) in list.withIndex()) {
            set.add(v.uid)
            set.add(v.fid)
        }
        set.remove(uid)
        return set
    }

    override fun queryNotAgreeFriend(fid: Int): List<Long> {
        return FriendDao().queryNotAgreeFriend(0, fid)
    }

    override fun queryNotApplyFriend(fid: Int): List<Long> {
        return FriendDao().queryNotApplyFriend(fid)
    }

    override fun delFriend(uid: Int, fid: Int): Int {
        var boo = FriendDao().delFriend(uid, fid)
        return boo
    }

    override fun queryStatus(uid: Int): Int {
        var status = FriendDao().queryStatus(uid) ?: 2
        return status.toString().toInt()
    }

    override fun repeatCheck(uid: Int, fid: Int): Int {
        val re = FriendDao().repeatCheck(uid, fid)
        return re
    }

    override fun updateFriend(uid: Int, fid: Int): Int {
        var re = FriendDao().addByFrined(uid, fid)
        return re
    }

    override fun insertFreined(friend: Friend): Int {
        var re = FriendDao().insertByFriend(friend)
        return re
    }
}