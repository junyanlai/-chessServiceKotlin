package com.shine.aservice.recharge

import com.shine.amodel.TopBufferTable

interface ITopBufferTableService {

    fun  insertTop(topBufferTable: TopBufferTable)
    fun  deleteTop(topBufferTable: TopBufferTable)

}