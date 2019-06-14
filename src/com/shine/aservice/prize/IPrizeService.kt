package com.shine.aservice.prize

import com.shine.amodel.Prize

interface IPrizeService {
    //获取奖励信息
    fun selectPrizeByTempId(tempId:Int): List<Prize>

    //添加成就奖励信息
    fun insertPrize(prize: Prize):Int

    //修改奖励信息
    fun updatePrize(prize: Prize):Int

    //删除成就奖励
    fun deletePrize(prize: Prize):Int
}