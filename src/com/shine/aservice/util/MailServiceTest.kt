package com.shine.aservice.util

import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import com.sun.mail.util.MailSSLSocketFactory
import java.io.UnsupportedEncodingException
import java.security.GeneralSecurityException
import java.time.LocalDateTime

object MailServiceTest {

    /*val serverPort = "465"
    val serverHost = "60.248.164.220"
    val from = "Warbuild co., ltd."
    val subject="博金彩官方驗證碼"

    val user = "robot@warbuild.com"
    val password ="32101004"*/

    val serverPort = "465"
    val serverHost = "smtp.exmail.qq.com"
    val from = "Warbuild co., ltd."
    val subject="博金彩官方驗證碼"

    val user = "robot@goldcolor-online.com"
    val password ="32101004Zz"

    @Throws(UnsupportedEncodingException::class, MessagingException::class, GeneralSecurityException::class)
    fun sendCodeEmail(email: String):Int {

        val current = LocalDateTime.now()

        val msg="当前为延迟测试邮件\n" +
                "服务器发信时间为:${current}\t  请对比邮箱收信时间"
        val body=msg+"歡迎注冊博金彩棋牌游戲平臺，您的驗證碼是：\n\t\t\t "
        val code=Random().nextInt(9000000)+1000000

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
            session.debug = true// 设置debug模式 在控制台看到交互信息
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
            msg.setText(body+code, "UTF-8")
            msg.saveChanges()
            transport.sendMessage(msg, msg.allRecipients)
            transport.close()

        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        } catch (e: MessagingException) {
            e.printStackTrace()
        }finally {
            return code
        }
    }
}