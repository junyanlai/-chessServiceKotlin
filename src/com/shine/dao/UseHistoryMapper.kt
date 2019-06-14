package com.shine.dao

import com.shine.amodel.*
import org.apache.ibatis.annotations.*
import java.util.ArrayList

@Mapper
interface UseHistoryMapper {

    fun  insertUseHistory(userHistory: UseHistory)
    fun  selectUserHistory(userHistory: UseHistory):List<UseHistory>
}