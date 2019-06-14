package com.shine.aservice.util

import com.sun.mail.util.MailSSLSocketFactory
import java.io.UnsupportedEncodingException
import java.security.GeneralSecurityException
import java.time.LocalDateTime
import java.util.*
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.NoSuchProviderException
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object FeedbackMail {
    val serverPort = "465"
    val serverHost = "smtp.exmail.qq.com"
    val from = "Warbuild co., ltd."
    val subject = "博金彩玩家體驗反饋"

    val user = "robot@goldcolor-online.com"
    val password = "32101004Zz"

    @Throws(UnsupportedEncodingException::class, MessagingException::class, GeneralSecurityException::class)
    fun feedbackMail(email: String, Fmail: String, uid: Int, nick: String, content: String) {
        val current = LocalDateTime.now()

        val msg = "服务器发信时间为:${current}\t\n"
        val info = msg + "玩家ID：${uid}, 玩家昵稱：${nick}\n "
        val body = info + "玩家邮箱：${Fmail}\t 反馈内容：${content}"


        try {

            //body=""

            val sf = MailSSLSocketFactory()
            sf.isTrustAllHosts = true

            val pro = Properties()
            pro["signmail.smtp.host"] = serverHost
            pro["signmail.smtp.serverPort"] = serverPort

            pro["signmail.smtp.auth"] = "true"
            pro["signmail.smtp.ssl.enable"] = "true"
            pro["signmail.smtp.ssl.socketFactory"] = sf

            val session = Session.getDefaultInstance(pro, null)
            session.debug = false// 设置debug模式 在控制台看到交互信息
            var transport = session.getTransport("smtp")
            transport.connect(serverHost, user, password)
            val msg = MimeMessage(session)
            msg.sentDate = Date()
            val fromAddress = InternetAddress(user, from, "UTF-8")
            msg.setFrom(fromAddress)
            //val toAddress = arrayOfNulls<InternetAddress>(1)
            //msg.setRecipients(Message.RecipientType.TO, toAddress);
            msg.setRecipients(Message.RecipientType.TO, email)
            msg.setSubject(subject, "UTF-8")
            msg.setText(body, "UTF-8")
            msg.saveChanges()
            transport.sendMessage(msg, msg.allRecipients)
            transport.close()

        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }
}