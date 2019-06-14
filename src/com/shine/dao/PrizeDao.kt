package com.shine.dao

import com.shine.agent.SSF
import com.shine.amodel.Prize

class PrizeDao : PrizeMapper {

    val session = SSF.getSessionFactory().openSession()
    val prizeMapper = session.getMapper(PrizeMapper::class.java)

    override fun selectPrizeByTempId(tempId: Int): List<Prize> {
        val list = prizeMapper.selectPrizeByTempId(tempId)
        session.close()
        return list
    }

    override fun insertPrize(prize: Prize): Int {
        val r = prizeMapper.insertPrize(prize)
        session.commit()
        session.close()
        return r
    }

    override fun updatePrize(prize: Prize): Int {
        val r = prizeMapper.updatePrize(prize)
        session.commit()
        session.close()
        return r
    }

    override fun deletePrize(prize: Prize): Int {
        val r = prizeMapper.deletePrize(prize)
        session.commit()
        session.close()
        return r
    }
}