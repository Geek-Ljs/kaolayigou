package com.shoppingmail

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.shoppingmail.logic.dao.DatabaseOperations
import com.shoppingmail.ui.adapter.AddressAdapter
import com.shoppingmail.ui.object_class.Address
import com.shoppingmail.ui.object_class.Order
import com.shoppingmail.ui.object_class.OrderComfirm
import kotlinx.android.synthetic.main.activity_address.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class AddressActivity : AppCompatActivity() {
    private lateinit var cn: Connection
    private lateinit var ps: PreparedStatement
    private lateinit var rs: ResultSet

    private var itemList = ArrayList<OrderComfirm>()
    private val addressList = ArrayList<Address>()
    private lateinit var layoutManager: LinearLayoutManager
    //异步操作获取当前用户的地址
    val showAddressHandler = 1

    val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what){
                showAddressHandler -> {
                    addressList.reverse()
                    showAddress()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)
        supportActionBar?.hide()



        initAddress()
        layoutManager = LinearLayoutManager(this)
        try {
            itemList = intent.getSerializableExtra("userList_data_to_addr") as ArrayList<OrderComfirm>
        }catch (e:Exception){
            e.printStackTrace()
        }


        add_address.setOnClickListener {
            val prefs: SharedPreferences? = getSharedPreferences("data", Context.MODE_PRIVATE)
            val user_id = prefs?.getLong("present_user_id", 0)
            if (user_id?.toInt() == 0){
                AlertDialog.Builder(this).apply {
                    setTitle("错误提示！")
                    setMessage("当前用户未登录")
                }.show()
            }else{
                val intent = Intent(this, AddAddressActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        address_back.setOnClickListener {
            finish()
        }
    }



    private fun initAddress() {
//        addressList.add(Address("林先生", "13417684704", "广东省佛山市南海区狮山镇阳光在线广场"))
//        addressList.add(Address("林先生", "13417684704", "广东省佛山市南海区狮山镇阳光在线广场"))
        val prefs: SharedPreferences? = getSharedPreferences("data", Context.MODE_PRIVATE)
        val user_id = prefs?.getLong("present_user_id", 0)
//        orderList.add(Order("商品图片路径", "商品名", 0.00))
        if (user_id?.toInt() != 0) {
            try {
                val sql:String = "Select addresses_id, addresses_receiver_name, addresses_receiver_telephone, addresses_receiver_address From addresses Where addresses_user_id = ?"
                Thread {
                    cn = DatabaseOperations().mysqlConnect()!!
                    ps = cn.prepareStatement(sql)
                    ps.setLong(1, user_id!!)
                    rs = ps.executeQuery()
                    while (rs.next()) {
                        addressList.add(Address(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4)))
                    }
                    val msg = Message()
                    msg.what = showAddressHandler
                    handler.sendMessage(msg)
                    closeConnect()
                }.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showAddress(){
        address_recyclerView.layoutManager = layoutManager
        val adapter = AddressAdapter(addressList, itemList, this)
//        val adapter = AddressAdapter(addressList, this)
        address_recyclerView.adapter = adapter
    }

    private fun closeConnect() {
        cn.close()
        ps.close()
        rs.close()
    }
}