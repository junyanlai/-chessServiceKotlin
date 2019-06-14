package com.shine.aservice.ranking

import com.shine.amodel.userRanking
import org.json.JSONArray

interface IRankingService {

    fun glamour(): JSONArray
    fun gold(): JSONArray
    fun armyGroup(): JSONArray
    //检测排行榜是否有30条数据，如果没有则增加到30条
    fun Testing(list: ArrayList<userRanking>)
}