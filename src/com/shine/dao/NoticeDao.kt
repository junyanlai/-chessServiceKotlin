package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.Notice
import com.shine.amodel.RecordLog

class NoticeDao : NoticeMapper {

    val session = getSessionFactory().openSession()
    val noticeMapper = session.getMapper(NoticeMapper::class.java)

    override fun insertNotice(notice: Notice): Int {
        val r = noticeMapper.insertNotice(notice)
        session.commit()
        session.close()
        return r
    }

    override fun updateNotice(notice: Notice): Int {
        val r = noticeMapper.updateNotice(notice)
        session.commit()
        session.close()
        return r
    }

    override fun deleteNotice(notice: Notice): Int {
        val r = noticeMapper.deleteNotice(notice)
        session.commit()
        session.close()
        return r
    }

    override fun recordLog(noticeLog: RecordLog): Int {
        val r = noticeMapper.recordLog(noticeLog)
        session.commit()
        session.close()
        return r
    }

    override fun initNoticePanel(): List<Notice> {
        val list = noticeMapper.initNoticePanel()
        session.close()
        return list
    }
}