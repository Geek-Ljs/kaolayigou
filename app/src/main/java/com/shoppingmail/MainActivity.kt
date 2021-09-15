package com.shoppingmail

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.makeText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shoppingmail.logic.dao.DatabaseOperations
import com.shoppingmail.logic.dao.RandomNumber
import com.shoppingmail.logic.dao.close
import com.shoppingmail.logic.network.SendEmail
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_test_count_down.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlinx.android.synthetic.main.activity_register.main_logo as main_logo1

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var cn:Connection
    private lateinit var ps:PreparedStatement
    private lateinit var rs:ResultSet
    private var user_nickname:String = ""
    private var user_id:Long = 0

    //生成验证码
    private var verificationCode: Long = 0

    //邮箱
    private var email: String? = null


    val judgeEmailExist = 1
    val judgeEmailNotExist = 2
    val getUserData = 3
    val updateUserData = 4
    val jump= 6

    val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            //在这里可以进行UI操作
            when (msg.what){
                judgeEmailExist -> {
//                    发送验证码
                    sendVerificationCode(email!!)
                }
                judgeEmailNotExist -> {
                    email_login_account.setError("格式有误/邮箱不存在！")
                }

                getUserData -> {
                    //获取用户名
                    getUserIdAndNickname()
                }

                updateUserData -> {
                    updateLoginStatues()
                }

                jump -> {
                    jumpToRecommendActivity()
                }
            }
        }
    }

    //传输数据
//    companion object{
//        //这个函数只需要传输一个用户名过去
//       fun actionTomyself(context: Context, data1:Long, data2:String){
//            val intent = Intent(context, RecommendActivity::class.java)
//            intent.putExtra("user_id", data1)
//            intent.putExtra("user_nikcname", data2)
//            context.startActivity(intent)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
//        init()

//        test.setOnClickListener {
//            val intent = Intent(this, TestCountDown::class.java)
//            startActivity(intent)
//        }

        var dialog:Dialog = AlertDialog.Builder(this)
            .setTitle("登录失败")
            .setMessage("验证码或邮箱输入错误!")
            .setPositiveButton("注册", DialogInterface.OnClickListener { dialogInterface, i ->
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            })
            .setNegativeButton("取消", DialogInterface.OnClickListener { dialogInterface, i ->

            })
            .create()

        email_login_password_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        main_logo.setOnClickListener {
            val intent = Intent(this, RecommendActivity::class.java)
            startActivity(intent)
        }


        //这一部分用于实现注册于登录功能
        email_login_password_login.setOnClickListener {
            val intent = Intent(this, PasswordLoginActivity::class.java)
            startActivity(intent)
        }


        //这一部分我是为了测试底部菜单栏跳转而设的
        /**
         * 条件：需要判断邮箱是否满足满足邮箱正则表达式
         *       需要判断邮箱是否已经注册
         */
        email_login_btn.setOnClickListener {

            //账号密码输入正确，跳转到APP首页
            if (email == null){
                dialog.show()
            }
            else if (judgeVerificationCode()){

                //获取用户昵称及id
                val msg = Message()
                msg.what = getUserData
                handler.sendMessage(msg)
//                val intent = Intent(this, RecommendActivity::class.java)
//                startActivity(intent)
//                MainActivity.actionTomyself(this, user_id, user_nickname)
            }else{
                dialog.show()
            }
        }

        //点击获取验证码
        email_login_Verification_code.setOnClickListener(this)

    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.email_login_Verification_code -> {
                email = email_login_account.text!!.toString()
                checkEmailExists()
            }
        }
    }


    //发送验证码
    private fun sendVerificationCode(email: String) {
        email_login_Verification_code.startTimer()
        try {
            object : Thread() {
                override fun run() {
                    super.run()
                    try {
                        val rn = RandomNumber()
                        verificationCode = rn.getRandomNumber()
                        val se = SendEmail(email)
                        se.sendHtmlEmail(verificationCode)
                        println("发送成功！")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun judgeVerificationCode():Boolean {
        if (email_login_password!!.text.toString().toLong() == verificationCode) {
            return true
        }
        return false
    }


    //邮箱是否已被绑定
    fun checkEmailExists(){
        try{
            val sql:String = "Select user_members_email From members Where user_members_email = ?"
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setString(1, email!!)
                rs = ps.executeQuery()
                rs.last()
                val rowCount = rs.row
//                println("行数：" + rowCount)
                closeConnect()
                val msg = Message()
                if (rowCount > 0){
                    msg.what = judgeEmailExist
                }else{
                    msg.what = judgeEmailNotExist
                }
                handler.sendMessage(msg)
            }.start()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    //登录成功修改登录状态
    private fun updateLoginStatues() {
        try{
            val sql:String = "Update members Set user_members_status = 1 Where user_members_email = ?"
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setString(1, email!!)
                ps.executeUpdate()
                val msg = Message()
                msg.what = jump
                handler.sendMessage(msg)
                closeConnect()
            }.start()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    //获取用户和用户名
    private fun getUserIdAndNickname(){
        try{
            val sql:String = "Select user_members_id, user_members_nickname From members Where user_members_email = ?"
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setString(1, email!!)
                rs = ps.executeQuery()
                while (rs.next()){
                    user_id = rs.getLong(1)
                    user_nickname = rs.getString(2)
                }
                val msg = Message()
                msg.what = updateUserData
                handler.sendMessage(msg)
                closeConnect()
            }.start()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    //跳转到RecommendActivity
    private fun jumpToRecommendActivity(){
//        MainActivity.actionTomyself(this, user_id, user_nickname)
        val editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
        editor.putLong("present_user_id", user_id)
        editor.putString("present_user_nickname", user_nickname)
        editor.apply()
        val intent = Intent(this, RecommendActivity::class.java)
        startActivity(intent)
    }

    private fun closeConnect() {
        cn.close()
        ps.close()
        rs.close()
    }
}