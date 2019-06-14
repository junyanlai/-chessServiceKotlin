package com.shine.aservice.shop

import com.shine.amodel.UseHistory

interface IUseHistoryService {

   //存入历史表
    fun  insertUseHistory(userHistory: UseHistory)
    //查询前五条礼物信息
    fun  selectUserHistory(userHistory: UseHistory):List<UseHistory>
}