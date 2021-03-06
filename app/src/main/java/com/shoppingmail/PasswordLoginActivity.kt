package com.shoppingmail

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.shoppingmail.logic.dao.DatabaseOperations
import com.shoppingmail.logic.dao.RandomNumber
import com.shoppingmail.logic.network.SendEmail
import kotlinx.android.synthetic.main.activity_password_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.change_password_dialog.*
import kotlinx.android.synthetic.main.change_password_dialog.view.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlinx.android.synthetic.main.activity_password_login.main_logo as main_logo1


class PasswordLoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var cn: Connection
    private lateinit var ps: PreparedStatement
    private lateinit var rs: ResultSet

    private lateinit var passwordLoginAccount:String
    private lateinit var passwordLoginPaasword:String
    var changePasswordDialogLayout : LinearLayout? = null
    private var verificationCode: Long = 0

    private var id:Long = 0
    private var nickname:String = ""

    val judgePasswordLoginAccountFormat = 1
    val judgePasswordLoginAccountExists = 2
    val judgePaaswordLoginAccountNoExists = 3
    val judgePasswordLoginPasswordRight = 4
    val judgePasswordLoginPasswordNoRight = 5
    val jump = 6
    val judgePasswordLoginUpdateData = 8
    val judgeForgetPasswordEmailRight = 9
    val judgeForgetPasswordEmailNoRight = 10
    val judgeNewPasswordFormat = 11



    private lateinit var dialog: AlertDialog.Builder

    val handler = object : Handler(){
        override fun handleMessage(msg: Message) {
            when (msg.what){
                judgePasswordLoginAccountFormat -> {
                    //??????????????????????????????????????????????????????
                    checkPasswordLoginAccountExists()
                }

                judgePasswordLoginAccountExists -> {
                    //?????????????????????????????????????????????
                    checkPasswordLoginPassword()
                }

                judgePaaswordLoginAccountNoExists -> {
                    //???????????????????????????????????????
                    password_login_account.setError("?????????????????????")
                }

                judgePasswordLoginPasswordRight -> {
                    //????????????????????????
                    getUserId()
                }

                judgePasswordLoginUpdateData -> {
                    //????????????????????????
                    UpdateUserStatus()
                }

                jump -> {
                    jumpToRecommendActivity()
                }

                judgePasswordLoginPasswordNoRight -> {
                    password_login_password.setError("???????????????")
                }

                //?????????????????????
                judgeForgetPasswordEmailRight -> {
                    //??????????????????????????????????????????????????????????????????
                    sendVerificationCode(changePasswordDialogLayout!!.forgetPasswordEmail.text.toString())

                }

                judgeForgetPasswordEmailNoRight -> {
                    changePasswordDialogLayout!!.forgetPasswordEmail.setError("??????????????????/????????????")
                }

                judgeNewPasswordFormat -> {
                    //?????????????????????????????????????????????
                    updateNewPassword()
                }

            }
        }

    }


    //????????????
    companion object {
        //????????????????????????????????????????????????
        fun actionTomyself(context: Context, data1:Long, data2:String){
            val intent = Intent(context, RecommendActivity::class.java)
            intent.putExtra("user_id", data1)
            intent.putExtra("user_nikcname", data2)
            context.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_login)
        supportActionBar?.hide()

        password_login_btn.setOnClickListener {
            passwordLoginAccount = password_login_account.text.toString()
            passwordLoginPaasword = password_login_password.text.toString()

            checkPasswordLoginAccountFormat()
        }

        main_logo.setOnClickListener {
            val intent = Intent(this, RecommendActivity::class.java)
            startActivity(intent)
        }

        password_login_return.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        password_login_forget_password.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        dialog = AlertDialog.Builder(this)
        when(view?.id){
            R.id.password_login_forget_password -> {
                //?????????????????????
                changePasswordDialogLayout = LayoutInflater.from(this).inflate(R.layout.change_password_dialog, null) as LinearLayout?
                dialog.setTitle("????????????")
                    .setView(changePasswordDialogLayout)
                    .setNegativeButton("??????", changePasswordCancel())
                    .setPositiveButton("??????", changePasswordClick())
                    .create()
                    .show()

                changePasswordDialogLayout!!.forgetPasswordgetVerifyCode.setOnClickListener {
                    //??????????????????
                    checkForgetPasswordEmail()
                }
            }
        }
    }

    //?????????????????????????????????????????????
    internal inner class changePasswordClick() : DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface, which: Int) {
            if (judgeVerificationCode()){
                if (judgeNewPassword()){
                    val msg = Message()
                    msg.what = judgeNewPasswordFormat
                    handler.sendMessage(msg)
                }else{
                    changePasswordDialogLayout!!.forgetPasswordNewPassword.setError("????????????????????????")
                }
            }else{
                changePasswordDialogLayout!!.forgetPasswordInputVerifyCode.setError("???????????????")
            }
        }
    }


    internal inner class changePasswordCancel() : DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface, which: Int) {
            dialog.cancel()
        }
    }


    private fun checkPasswordLoginAccountFormat() {
        var flag = 0
        if (passwordLoginAccount.length<4 || passwordLoginAccount.length>16){
            flag = 1
        }
        else{
            for (i in passwordLoginAccount){
                if (!i.isLetterOrDigit()){
                    flag = 1
                    break
                }
            }
        }
        if (flag == 1){
            password_login_account.setError("????????????????????????!")
        }
        else {
            try {
                Thread {
                    val msg = Message()
                    msg.what = judgePasswordLoginAccountFormat
                    handler.sendMessage(msg)
                }.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun checkPasswordLoginAccountExists() {
        var rowCount:Int = 0
        try {
            val sql:String = "Select user_members_nickname From members Where user_members_nickname = ?"
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setString(1, passwordLoginAccount)
                rs = ps.executeQuery()
                rs.last()
                rowCount = rs.row
                if (rowCount <= 0){
                    val msg = Message()
                    msg.what = judgePaaswordLoginAccountNoExists
                    handler.sendMessage(msg)
                }else{
                    val msg = Message()
                    msg.what = judgePasswordLoginAccountExists
                    handler.sendMessage(msg)
                }
                closeConnect()
            }.start()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    private fun checkPasswordLoginPassword() {
        try {
            val sql:String = "Select AES_DECRYPT(user_members_password, ?) From members Where user_members_nickname = ?"
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setString(1, DatabaseOperations().returnKey())
                ps.setString(2, passwordLoginAccount)
                rs = ps.executeQuery()
                rs.last()

                if (rs.getString(1) != passwordLoginPaasword){
                    val msg = Message()
                    msg.what = judgePasswordLoginPasswordNoRight
                    handler.sendMessage(msg)
                }else{
                    val msg = Message()
                    msg.what = judgePasswordLoginPasswordRight
                    handler.sendMessage(msg)
                }
                closeConnect()
            }.start()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun jumpToRecommendActivity() {
//        PasswordLoginActivity.actionTomyself(this, id, nickname)
//        val intent = Intent(this, RecommendActivity::class.java)
//        startActivity(intent)
        val editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
        editor.putLong("present_user_id", id)
        editor.putString("present_user_nickname", nickname)
        editor.apply()
        val intent = Intent(this, RecommendActivity::class.java)
        startActivity(intent)
    }


    //??????????????????????????????????????????
    private fun checkForgetPasswordEmail() {
        try{
            val sql:String = "Select user_members_email From members Where user_members_email = ?"
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setString(1, changePasswordDialogLayout!!.forgetPasswordEmail.text.toString())
                rs = ps.executeQuery()
                rs.last()
                val rowCount = rs.row
//                println("?????????" + rowCount)
                closeConnect()
                val msg = Message()
                if (rowCount > 0){
                    msg.what = judgeForgetPasswordEmailRight
                }else{
                    msg.what = judgeForgetPasswordEmailNoRight
                }
                handler.sendMessage(msg)
            }.start()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun sendVerificationCode(email: String) {
        try {
            object : Thread() {
                override fun run() {
                    super.run()
                    try {
                        val rn = RandomNumber()
                        verificationCode = rn.getRandomNumber()
                        val se = SendEmail(email)
                        se.sendHtmlEmail(verificationCode)
                        println("???????????????")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //???????????????
    private fun judgeVerificationCode(): Boolean {
        if (changePasswordDialogLayout!!.forgetPasswordInputVerifyCode.text.toString().toLong()
            ==verificationCode){
            return true
        }
        return false
    }

    //???????????????
    private fun judgeNewPassword(): Boolean {
        if (changePasswordDialogLayout!!.forgetPasswordNewPassword.text.toString().length<6
            || changePasswordDialogLayout!!.forgetPasswordNewPassword.text.toString().length>16){
            return false
        }else{
            for (i in changePasswordDialogLayout!!.forgetPasswordNewPassword.text.toString()){
                if (!i.isLetterOrDigit()){
                    return false
                }
            }
        }
        return true
    }

    //???????????????
    private fun updateNewPassword(){
        try {
            val sql:String = "Update members Set user_members_password = AES_ENCRYPT(?, ?) Where user_members_email = ?"
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setString(1, changePasswordDialogLayout!!.forgetPasswordNewPassword.text.toString())
                ps.setString(2, DatabaseOperations().returnKey())
                ps.setString(3, changePasswordDialogLayout!!.forgetPasswordEmail.text.toString())
                ps.executeUpdate()
                println("???????????????")
                Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show()
                closeConnect()
            }.start()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun getUserId(){
        try{
            val sql:String = "Select user_members_id From members Where user_members_nickname = ?"
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setString(1, passwordLoginAccount!!)
                rs = ps.executeQuery()
                while (rs.next()){
                    id = rs.getLong(1)
                }
                nickname = passwordLoginAccount
                val msg = Message()
                msg.what = judgePasswordLoginUpdateData
                handler.sendMessage(msg)
                closeConnect()
            }.start()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    //????????????????????????
    private fun UpdateUserStatus(){
        try{
            val sql:String = "Update members Set user_members_status = 1 Where user_members_nickname = ?"
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setString(1, passwordLoginAccount!!)
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


    private fun closeConnect() {
        cn.close()
        ps.close()
        rs.close()
    }
}
