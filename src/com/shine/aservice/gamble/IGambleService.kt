package com.shine.aservice.gamble

import com.shine.amodel.Gamble
import com.shine.amodel.HangRecord

interface IGambleService {

    fun queryAllRoom(type: Int): ArrayList<Gamble>

    fun queryRid(rid: Int): Gamble

    fun updateOccupy(rid: Int, occupy: Int): Int

    fun insertHangRecord(hangRecord: HangRecord): Int

    fun queryUid(uid: Int): List<HangRecord>

    fun deleteHangRecord(id: Int): Int

    fun updateHangRecordSign(id: Int): Int

    fun updateXcslData(robot: Gamble): Int

    fun modifYieldRate(rate: Double): Int

    fun insertGamble(gamble: Gamble): Int

    fun queryOccupyRecord(): List<HangRecord>

}