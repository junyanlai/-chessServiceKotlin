package com.shine.aservice.recharge.impl


import com.shine.amodel.TopBufferTable
import com.shine.aservice.recharge.ITopBufferTableService
import com.shine.dao.TopBufferTableDao

object TopBufferTableServiceImpl : ITopBufferTableService {
    override fun insertTop(topBufferTable: TopBufferTable) {
       TopBufferTableDao().insertTop(topBufferTable)
    }

    override fun deleteTop(topBufferTable: TopBufferTable) {
       TopBufferTableDao().deleteTop(topBufferTable)
    }
}