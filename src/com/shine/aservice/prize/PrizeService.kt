package com.shine.aservice.prize


import com.shine.amodel.Prize
import com.shine.dao.PrizeDao

object PrizeService:IPrizeService {
    override fun selectPrizeByTempId(tempId: Int): List<Prize> {
        return PrizeDao().selectPrizeByTempId(tempId)
    }

    override fun insertPrize(prize: Prize): Int {
        PrizeDao().insertPrize(prize)
        return 1
    }

    override fun updatePrize(prize: Prize): Int {
        PrizeDao().updatePrize(prize)
        return 1
    }

    override fun deletePrize(prize: Prize): Int {
        PrizeDao().deletePrize(prize)
        return 1
    }

}