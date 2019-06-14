package com.shine.aservice.eamil


import com.shine.amodel.Attachmentinfo
import com.shine.amodel.Goods
import com.shine.amodel.Mail
import com.shine.amodel.Recharge
import com.shine.dao.GoodsDao
import com.shine.dao.MailDao

object EmailService : IEmailService {
    override fun sendMail(mail: Mail): Int {
        var resultId = MailDao().sendPrivateMail(mail)
        return mail.id
    }

    override fun delMail(mailId: Int): Int {

        var list = MailDao().queryAttachmentinfo(mailId)
        list.forEach {
            delAttachmentinfo(it.id)
        }
        var reid = MailDao().delPrivateMail(mailId)
        return reid
    }

    override fun updataMail(mail: Mail): Int {
        var r = MailDao().updataPrivateMail(mail)
        return r
    }

    override fun delAttachmentinfo(id: Int): Int {
        var r = MailDao().delAttachmentinfo(id)
        return r
    }

    override fun queryAttachmentinfo(id: Int): Attachmentinfo {
        var list = MailDao().queryAttachmentinfo(id)
        if (list.size > 0) return list.get(0)
        return Attachmentinfo()
    }

    override fun addAttachmentinfo(attachmentinfo: Attachmentinfo): Int {
        var re = MailDao().addAttachmentinfo(attachmentinfo)
        return re
    }

    override fun updateAttachmentinfo(mid: Int): Int {
        var re = MailDao().updateAttachmentinfo(mid)
        return re
    }

    override fun getAllOwnMail(uid: Int): List<Mail> {
        return MailDao().getAllOwnMail(uid)
    }

    override fun checkOneMail(mid: Int): Mail {
        return MailDao().checkOneMail(mid)
    }

    override fun getGoodsInfo(gid: Int): Goods {
        return GoodsDao().goodsSelectOne(gid)
    }

    override fun rechargeCount(uid: Int): List<Recharge> {
        return MailDao().rechargeCount(uid)
    }
}