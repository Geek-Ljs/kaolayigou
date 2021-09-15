package com.shoppingmail

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast
import com.shoppingmail.logic.dao.DatabaseOperations
import kotlinx.android.synthetic.main.activity_add_address.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class AddAddressActivity : AppCompatActivity() {

    private lateinit var cn: Connection
    private lateinit var ps: PreparedStatement
    private lateinit var rs: ResultSet

    private val REGEX_MOBILE:String = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$"

    //异步处理
    val storeAddressSuccessful = 1

    val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what){
                storeAddressSuccessful -> {
                    JumbToAddreessActivity()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)
        supportActionBar?.hide()

        //返回键
        add_address_back.setOnClickListener {
            finish()
        }

        //点击保存对这些信息进行验证，信息满足要求则插入数据库并跳转到地址信息页

        add_address_store.setOnClickListener {
            if (add_address_name.text.isEmpty()){
                add_address_name.setError("收件人不能为空！")
            }else{
                if (add_address_telephone.text.isEmpty()){
                    add_address_telephone.setError("手机号码不能为空！")
                }else if (Regex(REGEX_MOBILE).matches(add_address_telephone.text.toString()) == false){
                    add_address_telephone.setError("手机号码不合法！")
                }else{
                    if (add_address_addr.text.isEmpty()){
                        add_address_addr.setError("详细地址不能为空！")
                    }else{
                        insertAddress(add_address_name.text.toString(), add_address_telephone.text.toString(), add_address_addr.text.toString())
                    }
                }
            }
        }
    }

    private fun insertAddress(name:String, telephone:String, addr:String) {
        val prefs: SharedPreferences? = getSharedPreferences("data", Context.MODE_PRIVATE)
        val user_id = prefs?.getLong("present_user_id", 0)
        if (user_id?.toInt() != 0) {
            try {
                val sql:String = "Insert Into addresses(addresses_user_id, addresses_receiver_name, addresses_receiver_telephone, addresses_receiver_address) " +
                        "Values (?, ?, ?, ?) "
                Thread{
                    cn = DatabaseOperations().mysqlConnect()!!
                    ps = cn.prepareStatement(sql)
                    ps.setLong(1, user_id!!)
                    ps.setString(2, name)
                    ps.setString(3, telephone)
                    ps.setString(4, addr)
                    ps.executeUpdate()
                    val msg = Message()
                    msg.what = storeAddressSuccessful
                    handler.sendMessage(msg)
                    cn.close()
                    ps.close()
                }.start()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }


    private fun JumbToAddreessActivity(){
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, AddressActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun closeConnect() {
        cn.close()
        ps.close()
        rs.close()
    }
}