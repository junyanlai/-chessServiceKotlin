package com.shine.aservice.shop

import com.shine.amodel.UseHistory
import com.shine.dao.UseHistoryDao

object UseHistoryService : IUseHistoryService {

    override fun insertUseHistory(userHistory: UseHistory) {
        UseHistoryDao().insertUseHistory(userHistory)
    }

    override fun selectUserHistory(userHistory: UseHistory): List<UseHistory> {
        return UseHistoryDao().selectUserHistory(userHistory)
    }


}