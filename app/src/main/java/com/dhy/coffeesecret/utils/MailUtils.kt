package com.dhy.coffeesecret.utils

import java.io.File
import java.io.IOException
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

/**
 * Created by CoDeleven on 17-8-21.
 */
@Throws(javax.mail.MessagingException::class, IOException::class)
fun sendMessageByLogPath(path:String?){
    val pro = Properties()

    pro.setProperty("mail.debug", "true")
    pro.setProperty("mail.smtp.auth", "true")
    pro.setProperty("mail.host", "smtp.qq.com")
    pro.setProperty("mail.transport.protocol", "smtp")
    pro.setProperty("mail.smtp.starttls.enable", "true")

    val session: Session = Session.getInstance(pro)
    val msg  = MimeMessage(session)
    msg.setFrom(InternetAddress("codeleven@vip.qq.com"))
    msg.setRecipients(Message.RecipientType.TO, "codeleven@vip.qq.com")
    msg.subject = "设置内容"
    msg.sentDate = Date()
    // setText内部会改变MimeMultipart，所以要么放在setMultipart之前，要么不设置
//        msg.setText("设置内容")

    val multipart = MimeMultipart("mixed")

    msg.setContent(multipart)

    val logAttach = MimeBodyPart()
    val contentAttach = MimeBodyPart()

    multipart.addBodyPart(logAttach)
    multipart.addBodyPart(contentAttach)

    val ds1 = FileDataSource(File(path))
    val dh1 = DataHandler(ds1)
    logAttach.dataHandler = dh1
    logAttach.fileName = "CoffeeSecret_log.rar"

    contentAttach.setText("咖啡机日志")

    msg.saveChanges()

    val transport: Transport = session.transport
    transport.connect("codeleven@vip.qq.com", "zymvvrvxhiwwbfej")

    transport.sendMessage(msg, arrayOf(InternetAddress("codeleven@vip.qq.com")))

}