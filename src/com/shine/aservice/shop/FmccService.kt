package com.shine.aservice.shop

import com.shine.amodel.Fmcc
import com.shine.dao.FmccDao

object FmccService : IFmccService {
    override fun selectFmccByType(type: String): List<Fmcc> {
        return FmccDao().selectFmccByType(type)
    }

    override fun selectFmccByUid(fmcc: Fmcc): List<Fmcc> {
//        println(fmcc.uid)
        var r = mutableListOf<Fmcc>()
        try {
            r.addAll(FmccDao().selectFmccByUid(fmcc))
        } catch (ex: Exception) {
            throw Exception(ex.message)
        }
//        println(r)
        return r
    }

    override fun selectFmcc(fmcc: Fmcc): List<Fmcc> {
        return FmccDao().selectFmcc(fmcc)
    }

    override fun selectFmccOne(fmcc: Fmcc): Fmcc {
        return FmccDao().selectFmccOne(fmcc)
    }

    @Throws
    override fun insertFmcc(fmcc: Fmcc): Int {
        var r = 0
        r = FmccDao().insertFmcc(fmcc)
        return r
    }

    @Throws
    override fun deleteFmcc(fmcc: Fmcc): Int {
        var r = 0
        r = FmccDao().deleteFmcc(fmcc)
        return r
    }

    @Throws
    override fun updateFmcc(fmcc: Fmcc): Int {
        var r = 0
        r = FmccDao().updateFmcc(fmcc)
        return r
    }

}