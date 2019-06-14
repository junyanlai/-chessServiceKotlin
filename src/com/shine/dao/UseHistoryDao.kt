package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.UseHistory

class UseHistoryDao : UseHistoryMapper {

    val session = getSessionFactory().openSession()
    val useHistoryMapper = session.getMapper(UseHistoryMapper::class.java)

    override fun insertUseHistory(userHistory: UseHistory) {
        val r = useHistoryMapper.insertUseHistory(userHistory)
        session.commit()
        session.close()
        return r
    }

    override fun selectUserHistory(userHistory: UseHistory): List<UseHistory> {
        val list = useHistoryMapper.selectUserHistory(userHistory)
        session.close()
        return list
    }
}