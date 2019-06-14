package com.shine.aservice.eamil

import com.shine.amodel.*
import org.omg.PortableInterceptor.INACTIVE

interface IEmailService {

    fun sendMail(mail: Mail): Int
    fun delMail(id: Int): Int
    fun updataMail(mail: Mail): Int
    fun delAttachmentinfo(id: Int): Int
    fun addAttachmentinfo(attachmentinfo: Attachmentinfo): Int
    fun queryAttachmentinfo(id: Int): Attachmentinfo
    fun updateAttachmentinfo(mid: Int): Int
    fun getAllOwnMail(id: Int): List<Mail>
    fun checkOneMail(mid: Int): Mail
    fun getGoodsInfo(gid: Int): Goods

    fun rechargeCount(uid:Int):List<Recharge>

}