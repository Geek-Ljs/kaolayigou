package com.shoppingmail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.shoppingmail.logic.dao.DatabaseOperations
import com.shoppingmail.ui.adapter.OrderComfirmAdapter
import com.shoppingmail.ui.adapter.OrderComfirmCartAdapter
import com.shoppingmail.ui.object_class.OrderComfirm
import com.shoppingmail.ui.object_class.OrderComfirmCart
import kotlinx.android.synthetic.main.activity_comfirm_order.*
import kotlinx.android.synthetic.main.activity_comfirm_order.order_comfirm_recyclerView
import kotlinx.android.synthetic.main.activity_order_comfirm_to_cart.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class OrderComfirmToCartActivity : AppCompatActivity() {

    private lateinit var cn: Connection
    private lateinit var ps: PreparedStatement
    private lateinit var rs: ResultSet

    private var receiverAddressId:Long = 0
    private var receiverName:String = ""
    private var receiverTelephony:String = ""
    private var receiverAddress:String = ""

    private var itemList = ArrayList<OrderComfirmCart>()
    private var totalPrice:Double = 0.0
    private lateinit var layoutManager: LinearLayoutManager

    //异步处理操作
    var handerShowAddressUnsuccess = 3
    var handerShowAddressSuccess = 2
    val orderSubmitSuccessful = 1
    val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what){
                handerShowAddressSuccess -> {
                    showAddressUI()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_comfirm_to_cart)
        supportActionBar?.hide()

        //这里传不过来
        itemList = intent.getSerializableExtra("itemList_data") as ArrayList<OrderComfirmCart>

        println("长度" + itemList.size)
        totalPrice = intent.getDoubleExtra("order_reciver_total_price", 0.0)
        layoutManager = LinearLayoutManager(this)
        order_comfirm_cart_recyclerView.layoutManager = layoutManager
        val adapter = OrderComfirmCartAdapter(itemList)
        order_comfirm_cart_recyclerView.adapter = adapter

        order_comfirm_cart_money.text = totalPrice.toString()

        //点击返回按钮
        order_comfirm_cart_to_address.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java)
            intent.putExtra("userList_data_to_addr", itemList)
            startActivity(intent)
            finish()
        }

        order_cart_cancel.setOnClickListener {
            finish()
        }
        showAddress()

        //支付判断
        submit_button.setOnClickListener {
            if (receiverAddressId.toInt() == 0){
                AlertDialog.Builder(this).apply {
                    setTitle("错误提示")
                    setMessage("请选择地址")
                }.show()
            }else{
                val intent = Intent(this, PaymentActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showAddress(){
        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        val user_id = prefs.getLong("present_user_id", 0)
        try {
            val sql:String = "Select addresses_id, addresses_receiver_name, addresses_receiver_telephone, addresses_receiver_address From addresses " +
                    "Where addresses_user_id = ?"
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setLong(1, user_id)
                rs = ps.executeQuery()
                if (!rs.wasNull()){
                    rs.first()
                    receiverAddressId = rs.getLong(1)
                    receiverName = rs.getString(2)
                    receiverTelephony = rs.getString(3)
                    receiverAddress = rs.getString(4)
                    val msg = Message()
                    msg.what = handerShowAddressSuccess
                    handler.sendMessage(msg)
                }
                closeConnect()
            }.start()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun showAddressUI(){
        order_comfirm_cart_adress_name.text = receiverName
        order_comfirm_cart_adress_phone.text = receiverTelephony
        order_comfirm_cart_adress_adress.text = receiverAddress
    }

    private fun closeConnect() {
        cn.close()
        ps.close()
        rs.close()
    }
}