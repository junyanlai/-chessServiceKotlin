package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.Gamble
import com.shine.amodel.HangRecord

class GambleDao : GambleMapper {

    val session = getSessionFactory().openSession()
    val gambleMapper = session.getMapper(GambleMapper::class.java)

    override fun queryAllRoom(type: Int): ArrayList<Gamble> {
        val list = gambleMapper.queryAllRoom(type)
        session.close()
        return list
    }

    override fun queryRid(rid: Int): Gamble {
        val obj = gambleMapper.queryRid(rid)
        session.close()
        return obj
    }

    override fun updateOccupy(rid: Int, occupy: Int): Int {
        val r = gambleMapper.updateOccupy(rid, occupy)
        session.commit()
        session.close()
        return r
    }

    override fun updateLhjRoom(robot: Gamble): Int {
        val r = gambleMapper.updateLhjRoom(robot)
        session.commit()
        session.close()
        return r
    }

    override fun updateXcslData(robot: Gamble): Int {
        val r = gambleMapper.updateXcslData(robot)
        session.commit()
        session.close()
        return r
    }

    override fun insertGamble(gamble: Gamble): Int {
        val r = gambleMapper.insertGamble(gamble)
        session.commit()
        session.close()
        return r
    }

    override fun insertHangRecord(hangRecord: HangRecord): Int {
        val r = gambleMapper.insertHangRecord(hangRecord)
        session.commit()
        session.close()
        return r
    }

    override fun queryUid(uid: Int): List<HangRecord> {
        val list = gambleMapper.queryUid(uid)
        session.close()
        return list
    }

    override fun deleteHangRecord(id: Int): Int {
        val r = gambleMapper.deleteHangRecord(id)
        session.commit()
        session.close()
        return r
    }

    override fun updateHangRecordSign(id: Int): Int {
        val r = gambleMapper.updateHangRecordSign(id)
        session.commit()
        session.close()
        return r
    }

    override fun queryOccupy(): List<HangRecord> {
        val list = gambleMapper.queryOccupy()
        session.close()
        return list

    }
}