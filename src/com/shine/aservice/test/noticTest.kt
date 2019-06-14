package com.shine.aservice.test

import com.alibaba.fastjson.JSON
import com.shine.agent.SSF
import com.shine.amodel.Gamble
import com.shine.amodel.RecordLog
import com.shine.aservice.gamble.GambleService
import com.shine.aservice.gamble.GambleService.insertGamble
import com.shine.dao.NoticeMapper
import org.json.JSONArray
import org.junit.Test
import java.util.*
import java.util.concurrent.Executors

class noticTest {

    var session = SSF.sqlSessionFactory.openSession()
    var noticeMapper = session.getMapper(NoticeMapper::class.java)
    val serviceScheduled = Executors.newCachedThreadPool()


    @Test
    fun test_1() {
        var sortSurvival = ArrayList<Int>()        //正序玩家


        sortSurvival.add(1)
        sortSurvival.add(2)
        sortSurvival.add(3)
        sortSurvival.add(4)


        sortSurvival.remove(1)

        sortSurvival.add(sortSurvival.size, 1)

        sortSurvival.forEach {
            println(it)
        }
    }



}