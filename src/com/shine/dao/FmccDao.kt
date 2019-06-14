package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.Fmcc


class FmccDao : FmccMapper {

    val session = getSessionFactory().openSession()
    val fmccMapper = session.getMapper(FmccMapper::class.java)

    override fun selectFmccByUid(fmcc: Fmcc): List<Fmcc> {
        val list = fmccMapper.selectFmccByUid(fmcc)
        session.close()
        return list
    }

    override fun selectFmccByType(type: String): List<Fmcc> {
        val list = fmccMapper.selectFmccByType(type)
        session.close()
        return list
    }

    override fun selectFmcc(fmcc: Fmcc): List<Fmcc> {
        val list = fmccMapper.selectFmcc(fmcc)
        session.close()
        return list
    }

    override fun selectFmccOne(fmcc: Fmcc): Fmcc {
        val obj = fmccMapper.selectFmccOne(fmcc)
        session.close()
        return obj
    }

    override fun insertFmcc(fmcc: Fmcc): Int {
        val r = fmccMapper.insertFmcc(fmcc)
        session.commit()
        session.close()
        return r
    }

    override fun deleteFmcc(fmcc: Fmcc): Int {
        val r = fmccMapper.deleteFmcc(fmcc)
        session.commit()
        session.close()
        return r
    }

    override fun updateFmcc(fmcc: Fmcc): Int {
        val r = fmccMapper.updateFmcc(fmcc)
        session.commit()
        session.close()
        return r
    }
}