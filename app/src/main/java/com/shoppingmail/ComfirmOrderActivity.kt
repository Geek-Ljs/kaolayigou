package com.shoppingmail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.shoppingmail.logic.dao.DatabaseOperations
import com.shoppingmail.ui.adapter.OrderComfirmAdapter
import com.shoppingmail.ui.object_class.OrderComfirm
import kotlinx.android.synthetic.main.activity_comfirm_order.*
import kotlinx.android.synthetic.main.activity_payment.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class ComfirmOrderActivity : AppCompatActivity() {

    private lateinit var cn: Connection
    private lateinit var ps: PreparedStatement
    private lateinit var rs: ResultSet

    private var itemList = ArrayList<OrderComfirm>()

    private lateinit var layoutManager: LinearLayoutManager

    private var receiverAddressId:Long = 0
    private var receiverName:String = ""
    private var receiverTelephony:String = ""
    private var receiverAddress:String = ""

    private var receiverAddressId_addr:Long = 0
    private var receiverName_addr:String = ""
    private var receiverTelephony_addr:String = ""
    private var receiverAddress_addr:String = ""
//    private var items_detail_Image:String = ""
//    private var items_detail_name:String = ""
    private var items_detail_price:Double = 0.00
    private var receiverItemId : Long = 0

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
                orderSubmitSuccessful -> {
                    showOrderSubmitSuccessful()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comfirm_order)
        supportActionBar?.hide()
        try {
            itemList = intent.getSerializableExtra("itemList_data") as ArrayList<OrderComfirm>
        }catch (e:Exception){
            e.printStackTrace()
        }
        layoutManager = LinearLayoutManager(this)
        order_comfirm_recyclerView.layoutManager = layoutManager
        val adapter = OrderComfirmAdapter(itemList)
        order_comfirm_recyclerView.adapter = adapter

        //总价
        moneyTextView2.text = itemList[0].itemPrice.toString()



        //点击submit_to_address跳转到选择地址
        submit_to_address.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java)
            intent.putExtra("userList_data_to_addr", itemList)
            startActivity(intent)
            finish()
        }

        receiverAddressId_addr = intent.getLongExtra("address_id", 0)
        receiverName_addr = intent.getStringExtra("address_user_name").toString()
        receiverTelephony_addr = intent.getStringExtra("address_user_telephone").toString()
        receiverAddress_addr = intent.getStringExtra("address_user_addr").toString()

        //显示地址
        if (receiverAddressId_addr.toInt() != 0){
            submit_adress_name.text = receiverName_addr
            submit_adress_phone.text = receiverTelephony_addr
            submit_adress_adress.text = receiverAddress_addr
            submitButton.setOnClickListener {
                insertOrderData(receiverAddressId_addr)
            }
        }else{
            showAddress()
            submitButton.setOnClickListener {
                if (receiverAddressId.toInt() != 0)
                    insertOrderData(receiverAddressId)
                else{
                    AlertDialog.Builder(this).apply {
                        setTitle("错误提示")
                        setMessage("请选择地址")
                    }.show()
                }
            }
        }

        order_cancel.setOnClickListener {
            finish()
        }


    }

    private fun insertOrderData(receiver_addressId:Long){
        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        val user_id = prefs.getLong("present_user_id", 0)
        val sql:String = "Insert Into orders(order_orders_member_id, order_orders_address_id, order_orders_total_amount, order_orders_flying_amount, order_orders_paid_amount, order_orders_payment_type, order_orders_status, order_orders_item_id) " +
                "Values (?, ?, ?, ?, ?, ?, ?, ?)"
        try {
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setLong(1, user_id)
                //这一个字段先设置成一个默认地址
//                ps.setLong(2, receiverAddressId.toLong())
                ps.setLong(2, receiver_addressId)
                ps.setDouble(3, moneyTextView2.text.toString().toDouble())
                ps.setDouble(4, 0.00)
                ps.setDouble(5, moneyTextView2.text.toString().toDouble())
                ps.setString(6, "微信支付")
                ps.setInt(7, 1)
                ps.setLong(8, itemList[0].itemId)
                ps.executeUpdate()
                val msg = Message()
                msg.what = orderSubmitSuccessful
                handler.sendMessage(msg)
                cn.close()
                ps.close()
            }.start()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }



    private fun showOrderSubmitSuccessful(){
//        AlertDialog.Builder(this).apply {
//            setTitle("订单提交状况")
//            setMessage("下单成功")
//        }.show()
        val intent = Intent(this, PaymentActivity::class.java)
        startActivity(intent)
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
                rs.last()
                if (rs.row>0) {
                    println("这一步能够执行！")
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
        submit_adress_name.text = receiverName
        submit_adress_phone.text = receiverTelephony
        submit_adress_adress.text = receiverAddress
    }

    private fun closeConnect() {
        cn.close()
        ps.close()
        rs.close()
    }
}