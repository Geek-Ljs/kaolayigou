package com.shoppingmail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.shoppingmail.logic.dao.DatabaseOperations
import kotlinx.android.synthetic.main.activity_register.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class RegisterActivity : AppCompatActivity() {

    private lateinit var cn: Connection
    private lateinit var ps: PreparedStatement
    private lateinit var rs: ResultSet

    private lateinit var registerAccount:String
    private lateinit var registerBoundedEmail:String
    private lateinit var registerPassword:String


    val judgeNicknameFormat = 1
    val judgeNicknameNoExists = 2
    val judgeNicknameExists = 3
    val judgeEmailFormat = 4
    val judgeEmailNoExists = 5
    val judgeEmailExists = 6
    val judgePasswordFormat = 7
    val judgeIntentToLogin = 8


    val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            //在这里可以进行UI操作
            when (msg.what){
                judgeNicknameFormat -> {
                    //进入这里面代表用户名格式符合，接下来检测用户名是否已存在于数据库
                    checkRegisterAccountExist()
                }

                judgeNicknameNoExists -> {
                    //进入这里代表用户名不存在，接下来检测邮箱格式是否符合要求
                    checkEmailFormat()

                }

                judgeNicknameExists -> {
                    register_account.setError("用户名已存在！")
                }

                judgeEmailFormat -> {
                    //进入这里代表邮箱格式符合，接下来检测邮箱是否已存在于数据库中
                    println("这里执行成功！")
                    checkEmailExists()
                }

                judgeEmailNoExists -> {
                    //进入这里代表邮箱未被绑定，接下来检测密码格式是否符合要求
                    checkPasswordFormat()
                }

                judgeEmailExists -> {
                    register_bound_email.setError("邮箱已被绑定！")
                }

                judgePasswordFormat -> {
                    //进入这里代表密码没有错误，接下来将注册的信息插入到数据库中
                    insertRegisterData()
                }

                judgeIntentToLogin -> {
                    jumpToLogin()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        register.setOnClickListener {
            registerAccount = register_account.text!!.toString()
            registerBoundedEmail = register_bound_email!!.text.toString()
            registerPassword = register_password.text!!.toString()

            checkRegisterAccountFormat()
        }

        main_logo.setOnClickListener {
            val intent = Intent(this, RecommendActivity::class.java)
            startActivity(intent)
        }

        register_jump_to_login.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    //检测用户名格式
    fun checkRegisterAccountFormat(){
        var flag = 0
        if (registerAccount.length<4 || registerAccount.length>16){
            flag = 1
        }
        else{
            for (i in registerAccount){
                if (!i.isLetterOrDigit()){
                    flag = 1
                    break
                }
            }
        }
        if (flag == 1){
            register_account.setError("用户名格式有错误!")
        }
        else {
            try {
                Thread {
                    val msg = Message()
                    msg.what = judgeNicknameFormat
                    handler.sendMessage(msg)
                }.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //检测用户名是否已存在数据库
    fun checkRegisterAccountExist(){
        var rowCount:Int = 0
        try {
            val sql:String = "Select user_members_nickname From members Where user_members_nickname = ?"
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setString(1, registerAccount)
                rs = ps.executeQuery()
                rs.last()
                rowCount = rs.row
                if (rowCount <= 0){
                    val msg = Message()
                    msg.what = judgeNicknameNoExists
                    handler.sendMessage(msg)
                }else{
                    val msg = Message()
                    msg.what = judgeNicknameExists
                    handler.sendMessage(msg)
                }
                closeConnect()
            }.start()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    //邮箱格式错误
    fun checkEmailFormat(){
        var flag = 0
        if (Regex("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*").matches(registerBoundedEmail) == false){
            flag = 1
        }
        if (flag == 1){
            register_bound_email.setError("邮箱格式错误！")
        }
        else {
            try {
                Thread {
                    val msg = Message()
                    msg.what = judgeEmailFormat
                    handler.sendMessage(msg)
                }.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //邮箱是否已被绑定
    fun checkEmailExists(){
        println("进入这里")
        try{
            val sql:String = "Select user_members_email From members Where user_members_email = ?"
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setString(1, registerBoundedEmail)
                rs = ps.executeQuery()
                rs.last()
                val rowCount = rs.row
                println("rowCount为："+rowCount.toString())
                if (rowCount <= 0){
                    val msg = Message()
                    msg.what = judgeEmailNoExists
                    handler.sendMessage(msg)
                }else{
                    println("CheckEmailExistsElse这里")
                    val msg = Message()
                    msg.what = judgeEmailExists
                    handler.sendMessage(msg)
                }
                closeConnect()
            }.start()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    //判断密码格式
    fun checkPasswordFormat(){
        var flag = 0
        if (registerPassword.length<6 || registerPassword.length>16){
            flag = 1
        }else{
            for (i in registerPassword){
                if (!i.isLetterOrDigit()){
                    flag = 1
                    break
                }
            }
        }
        if (flag == 1){
            register_password.setError("密码格式错误！")
        }
        else {
            try {
                Thread {
                    val msg = Message()
                    msg.what = judgePasswordFormat
                    handler.sendMessage(msg)
                }.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun insertRegisterData(){
        try{
            val sql:String = "Insert Into members(user_members_nickname, user_members_remarkname, user_members_password, user_members_email) " +
                    " Values(?, ?, AES_ENCRYPT(?, ?), ?)"
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setString(1, registerAccount)
                ps.setString(2, registerAccount)
                ps.setString(3, registerPassword)
                ps.setString(4, DatabaseOperations().returnKey())
                ps.setString(5, registerBoundedEmail)
                ps.executeUpdate()
                val msg = Message()
                msg.what = judgeIntentToLogin
                handler.sendMessage(msg)
                closeConnect()
            }.start()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun jumpToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun closeConnect() {
        cn.close()
        ps.close()
        rs.close()
    }
}