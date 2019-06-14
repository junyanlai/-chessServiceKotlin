package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.Friend
import com.shine.amodel.User

class FriendDao : FriendMapper {
    val session = getSessionFactory().openSession()
    val friendMapper = session.getMapper(FriendMapper::class.java)

    override fun queryOwnFriend(uid: Int): List<Friend> {
        val list = friendMapper.queryOwnFriend(uid)
        session.close()
        return list
    }

    override fun queryNotAgreeFriend(status: Int, fid: Int): List<Long> {
        val list = friendMapper.queryNotAgreeFriend(status, fid)
        session.close()
        return list
    }

    override fun queryNotApplyFriend(fid: Int): List<Long> {
        val list = friendMapper.queryNotApplyFriend(fid)
        session.close()
        return list
    }

    override fun insertByFriend(friend: Friend): Int {
        val r = friendMapper.insertByFriend(friend)
        session.commit()
        session.close()
        return r
    }

    override fun delByFrined(id: Int): Int {
        val r = friendMapper.delByFrined(id)
        session.commit()
        session.close()
        return r
    }

    override fun updateByFrined(id: Int, user: User) {
        val r = friendMapper.updateByFrined(id, user)
        session.commit()
        session.close()
        return r
    }

    override fun addByFrined(uid: Int, fid: Int): Int {
        val r = friendMapper.addByFrined(uid, fid)
        session.commit()
        session.close()
        return r
    }

    override fun delFriend(uid: Int, fid: Int): Int {
        val r = friendMapper.delFriend(uid, fid)
        session.commit()
        session.close()
        return r
    }

    override fun queryStatus(uid: Int): String {
        val r = friendMapper.queryStatus(uid)
        session.close()
        return r
    }

    override fun repeatCheck(uid: Int, fid: Int): Int {
        val r = friendMapper.repeatCheck(uid, fid)
        session.close()
        return r
    }
}