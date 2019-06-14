package com.shine.dao

import com.shine.amodel.TopBufferTable
import org.apache.ibatis.annotations.Mapper

@Mapper
interface TopBufferTableMapper {

    fun  insertTop(topBufferTable: TopBufferTable)
    fun  deleteTop(topBufferTable: TopBufferTable)
}