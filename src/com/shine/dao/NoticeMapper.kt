package com.shine.dao

import com.shine.amodel.Notice
import com.shine.amodel.RecordLog
import org.apache.ibatis.annotations.*


@Mapper
interface NoticeMapper {


    @Insert("""
         INSERT INTO notify(gameId, name,title,news, time)VALUES ( #{gameId},#{name},#{title},#{news},now())""")
    fun insertNotice(notice: Notice):Int

    @Update("""
     update notify set title=#{title},news=#{news},time=#{time}whereid=#{id}""")
    fun updateNotice(notice: Notice):Int

    @Delete("""delete from notify  where id =#{id}""")
    fun deleteNotice(notice: Notice):Int

    @Insert("""INSERT INTO record_log (uid,name,type,msg,date)VALUES(#{uid},#{name},#{type},#{msg},now())""")
    fun recordLog(noticeLog: RecordLog):Int


    @Select("""Select * from notify limit 0,5""")
    fun initNoticePanel(): List<Notice>
}