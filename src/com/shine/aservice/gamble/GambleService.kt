package com.shine.aservice.gamble

import com.shine.amodel.Gamble
import com.shine.amodel.HangRecord
import com.shine.dao.GambleDao
import java.io.File
import java.nio.charset.Charset

object GambleService : IGambleService {

    override fun queryAllRoom(type: Int): ArrayList<Gamble> {
        return GambleDao().queryAllRoom(type)
    }

    override fun queryRid(rid: Int): Gamble {
        return GambleDao().queryRid(rid)
    }

    override fun updateOccupy(rid: Int, occupy: Int): Int {
        val r = GambleDao().updateOccupy(rid, occupy)
        return r
    }

    override fun updateXcslData(robot: Gamble): Int {
        val r = GambleDao().updateXcslData(robot)
        return r
    }

    override fun insertHangRecord(hangRecord: HangRecord): Int {
        val r = GambleDao().insertHangRecord(hangRecord)
        return r
    }

    override fun queryUid(uid: Int): List<HangRecord> {
        return GambleDao().queryUid(uid)
    }

    override fun insertGamble(gamble: Gamble): Int {
        val r = GambleDao().insertGamble(gamble)
        return r
    }

    override fun deleteHangRecord(id: Int): Int {
        val r = GambleDao().deleteHangRecord(id)
        return r
    }

    override fun updateHangRecordSign(id: Int): Int {
        val r = GambleDao().updateHangRecordSign(id)
        return r
    }

    override fun modifYieldRate(rate: Double): Int {
        val file = File("./Rate.csv")
        file.writeText(rate.toString(), Charset.forName("UTF-8"))

        if (file.readLines(Charset.forName("UTF-8"))[0] == rate.toString()) {
            return 1
        }
        return 0
    }

    override fun queryOccupyRecord(): List<HangRecord> {
        return GambleDao().queryOccupy()
    }
}