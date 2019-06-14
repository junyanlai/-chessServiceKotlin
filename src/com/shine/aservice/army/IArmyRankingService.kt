package com.shine.aservice.army

import com.shine.amodel.ArmyRanking

interface IArmyRankingService {

    fun insertRanking(r: ArmyRanking): Int

    fun updateRanking(id: Int, score: Long): Int

    fun queryAll(): List<ArmyRanking>

    fun queryByArmyId(id: Int): ArmyRanking

}