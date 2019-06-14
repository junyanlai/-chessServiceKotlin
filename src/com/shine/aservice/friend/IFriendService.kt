package com.shine.aservice.friend

import com.shine.amodel.Friend
import com.shine.amodel.User
import java.util.*

interface IFriendService {
    fun lookUpFriend(name: String): List<User>

    fun queryOwnFriend(uid: Int): HashSet<Int>

    fun queryNotAgreeFriend(fid: Int): List<Long>

    fun queryNotApplyFriend(uid: Int): List<Long>

    fun delFriend(uid: Int, fid: Int): Int

    fun queryStatus(uid: Int): Int

    fun repeatCheck(uid: Int, fid: Int): Int

    fun updateFriend(uid: Int, fid: Int): Int

    fun insertFreined(friend: Friend): Int
}