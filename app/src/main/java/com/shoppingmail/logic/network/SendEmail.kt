package com.shoppingmail.logic.network

import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 *   author ： Jason
 *   time    ： 2021/3/11
 */
class SendEmail(toEmail: String) {
    //邮件发送协议
    private val PROTOCOL:String = "smtp"

    //SMTP邮件服务器
    private val HOST:String = "smtp.163.com"

    //SMTP邮件服务器默认端口
    private val PORT:String = "25"

    //是否要求身份验证
    private val IS_AUTH:String = "true"

    //是否启用调试模式（启用调试模式可打印客户端与服务端交互过程时一问一答的响应消息）
    private val IS_ENABLED_DEBUG_MOD:String = "true"

    //发件人
    private val from:String = "linjs_jason@163.com" //我自己注册的邮箱

    //授权密码
    //    private String password = "TYQEMFIYPVNSMQYY";
    //收件人
    private var to:String = "" //收件人，为EditText填入的邮箱地址

    //初始化连接邮件服务器的会话消息
    private var props: Properties? = null

    init {
        to = toEmail
        props = Properties()
        props!!.setProperty("mail.transport.protocol", PROTOCOL)
        props!!.setProperty("mail.smtp.host", HOST)
        props!!.setProperty("mail.smtp.port", PORT)
        props!!.setProperty("mail.smtp.auth", IS_AUTH)
        props!!.setProperty("mail.debug", IS_ENABLED_DEBUG_MOD)
    }

    /**
     * 发送简单的文本验证码邮件，Num为验证码
     */
    fun sendTextEmail(num: Long){
        //创建Session实例对象
        val session:Session = Session.getDefaultInstance(props)

        //创建MimeMessage实例对象
        val message:MimeMessage = MimeMessage(session)
        //设置发件人
        message.setFrom(InternetAddress(from))
        //设置邮件主题
        message.subject = "验证码"
        //设置收件人
        message.setRecipient(Message.RecipientType.TO, InternetAddress(to))
        //设置发送时间
        message.sentDate = Date()
        //设置纯文本内容为邮件正文
        message.setText("使用什么发送！")
        //保存并生成最终的邮件内容
        message.saveChanges()

        //获得Transport实例对象
        val transport = session.transport
        //打开连接
        transport.connect("linjs_jason", "TYQEMFIYPVNSMQYY") //邮箱名字和刚才设置的授权密码
        //将message对像传递给transport对象，将邮件发送出去
        transport.sendMessage(message, message.allRecipients)
        //关闭连接
        transport.close()
    }

    /**
     * 发送简单的html验证，num为验证码
     *
     */
    fun sendHtmlEmail(num: Long) {
        //创建Session实例对象
        val session:Session = Session.getInstance(props, MyAuthenticator())

        //创建MimeMessage实例对象
        val message:MimeMessage = MimeMessage(session)
        //设置邮件主题
        message.subject = "考拉易购登录验证码"
        //设置发送人
        message.setFrom(InternetAddress(from))
        //设置发送时间
        message.sentDate = Date()
        //设置收件人
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
        //设置html内容为邮件正文，指定MIME类型为text/html类型，并将指定字符编码为gbk
        message.setContent(
            "<span style='color:blue;'>您本次的验证码为：</span><span style='color:red;'>$num</span>",
            "text/html;charset=gbk"
        )
        //保存并生成最终的邮件内容
        message.saveChanges()
        //发送邮件
        Transport.send(message)
    }

    /**
     * 向邮件服务器提交认证信息
     */
    internal class MyAuthenticator : Authenticator {
        //邮箱名字
        private var username = "linjs_jason"

        //邮箱授权密码
        private var password = "TYQEMFIYPVNSMQYY"

        constructor() : super() {}

        constructor(username: String, password: String) : super() {
            this.username = username
            this.password = password
        }

        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(username, password)
        }
    }
}