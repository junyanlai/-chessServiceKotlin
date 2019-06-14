package com.shine.dao

import com.shine.amodel.Attachmentinfo
import com.shine.amodel.Mail
import com.shine.amodel.Recharge
import org.apache.ibatis.annotations.*


@Mapper
interface MailMapper {
    @Insert("""
    insert into privatemail (sendId, sendName, receiveId, receiveName, sendDate, receiveDate, expireDate, message, status, attachmentinfo)
VALUES (#{sendId}, #{sendName}, #{receiveId},#{receiveName}, now(), now(), #{expireDate}, #{message}, 0, 0)""")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun sendPrivateMail(main: Mail): Int

    @Delete("""delete from privatemail where id=#{id}""")
    fun delPrivateMail(id: Int): Int

    @Update(
            """update privatemail set expireDate=#{expireDate},status=#{status} where id=#{id}"""
    )
    fun updataPrivateMail(mail: Mail): Int

    @Update(
            """update privatemail set attachmentinfo=1,status=1 where id=#{mid}"""
    )
    fun updateAttachmentinfo(mid: Int): Int

    @Insert("""insert into attachmentinfo(mailId,gId,count) VALUES (#{mailId},#{gId},#{count})""")
    fun addAttachmentinfo(attachmentinfo: Attachmentinfo): Int

    @Delete("""delete from attachmentinfo where id=#{id}""")
    fun delAttachmentinfo(id: Int): Int

    @Select("""select * from attachmentinfo where mailId=#{mId}""")
    fun queryAttachmentinfo(mId: Int): List<Attachmentinfo>

    @Select("""select * from privatemail where receiveId=#{uid} ORDER BY status ASC""")
    fun getAllOwnMail(uid: Int): List<Mail>

    @Select(""" select * from privatemail where id=#{mid}""")
    fun checkOneMail(mid: Int): Mail

    @Select("""SELECT * FROM recharge_record   WHERE uid=#{uid} AND payMentType='mail'""")
    fun rechargeCount(uid: Int): List<Recharge>
}