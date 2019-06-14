package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.Attachmentinfo
import com.shine.amodel.Mail
import com.shine.amodel.Recharge

class MailDao : MailMapper {
    val session = getSessionFactory().openSession()
    val mailMapper = session.getMapper(MailMapper::class.java)

    override fun sendPrivateMail(main: Mail): Int {
        val r = mailMapper.sendPrivateMail(main)
        session.commit()
        session.close()
        return r
    }

    override fun delPrivateMail(id: Int): Int {
        val r = mailMapper.delPrivateMail(id)
        session.commit()
        session.close()
        return r
    }

    override fun updataPrivateMail(mail: Mail): Int {
        val r = mailMapper.updataPrivateMail(mail)
        session.commit()
        session.close()
        return r
    }

    override fun updateAttachmentinfo(mid: Int): Int {
        val r = mailMapper.updateAttachmentinfo(mid)
        session.commit()
        session.close()
        return r
    }

    override fun addAttachmentinfo(attachmentinfo: Attachmentinfo): Int {
        val r = mailMapper.addAttachmentinfo(attachmentinfo)
        session.commit()
        session.close()
        return r
    }

    override fun delAttachmentinfo(id: Int): Int {
        val r = mailMapper.delAttachmentinfo(id)
        session.commit()
        session.close()
        return r
    }

    override fun queryAttachmentinfo(mId: Int): List<Attachmentinfo> {
        val list = mailMapper.queryAttachmentinfo(mId)
        session.close()
        return list
    }

    override fun getAllOwnMail(uid: Int): List<Mail> {
        val list = mailMapper.getAllOwnMail(uid)
        session.close()
        return list
    }

    override fun checkOneMail(mid: Int): Mail {
        val mail = mailMapper.checkOneMail(mid)
        session.close()
        return mail
    }

    override fun rechargeCount(uid: Int): List<Recharge> {
        val list = mailMapper.rechargeCount(uid)
        session.close()
        return list
    }
}