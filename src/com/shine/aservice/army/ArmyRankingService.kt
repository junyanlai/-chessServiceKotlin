package com.shine.aservice.army


import com.shine.amodel.ArmyRanking
import com.shine.dao.ArmyAdminDao


object ArmyRankingService : IArmyRankingService {

    override fun insertRanking(r: ArmyRanking): Int {
        val r = ArmyAdminDao().insertRanking(r)
        return r
    }

    override fun queryAll(): List<ArmyRanking> {
        return ArmyAdminDao().queryAll()
    }

    override fun updateRanking(id: Int, score: Long): Int {
        val r = ArmyAdminDao().updateRanking(id, score)
        return r
    }

    override fun queryByArmyId(id: Int): ArmyRanking {
        return ArmyAdminDao().queryByArmyId(id)
    }
}