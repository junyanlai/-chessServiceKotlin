package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.TopBufferTable

class TopBufferTableDao : TopBufferTableMapper {
    val session = getSessionFactory().openSession()

    val topBufferTableMapper = session.getMapper(TopBufferTableMapper::class.java)

    override fun insertTop(topBufferTable: TopBufferTable) {
        topBufferTableMapper.insertTop(topBufferTable)
        session.commit()
        session.close()
    }

    override fun deleteTop(topBufferTable: TopBufferTable) {
        topBufferTableMapper.deleteTop(topBufferTable)
        session.commit()
        session.close()
    }


}
