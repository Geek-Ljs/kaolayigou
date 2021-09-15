package com.shoppingmail

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.shoppingmail.logic.dao.DatabaseOperations
import com.shoppingmail.ui.adapter.OrderAdapter
import com.shoppingmail.ui.object_class.Cart_Goods
import com.shoppingmail.ui.object_class.Order
import kotlinx.android.synthetic.main.activity_order.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class OrderActivity : AppCompatActivity() {

    private lateinit var cn: Connection
    private lateinit var ps: PreparedStatement
    private lateinit var rs: ResultSet

    private lateinit var layoutManager: LinearLayoutManager

    private val orderList = ArrayList<Order>()
    var showOrderHandler = 1
    val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what){
                showOrderHandler -> {
                    orderList.reverse()
                    showOrder()
                }
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        supportActionBar?.hide()

        order_back.setOnClickListener {
            finish()
        }

        initOrder()
        layoutManager = LinearLayoutManager(this, )
        order_recyclerview.layoutManager = layoutManager
    }

    private fun initOrder() {
        val sql:String = "Select product_items_detailImage, product_items_name, product_items_price From items " +
                "Where product_items_id In (Select order_orders_item_id From orders Where order_orders_member_id = ? Order By order_orders_id Desc)"
        val prefs:SharedPreferences? = getSharedPreferences("data", Context.MODE_PRIVATE)
        val user_id = prefs?.getLong("present_user_id", 0)
//        orderList.add(Order("商品图片路径", "商品名", 0.00))
        try{
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setLong(1, user_id!!)
                rs = ps.executeQuery()
                while (rs.next()){
                    orderList.add(Order(rs.getString(1), rs.getString(2), rs.getDouble(3)))
                }
                val msg = Message()

                msg.what = showOrderHandler
                handler.sendMessage(msg)
                closeConnect()
            }.start()
        }catch (e:Exception){
            e.printStackTrace()
        }

    }


    private fun showOrder(){
        val adapter = OrderAdapter(this, orderList)
        order_recyclerview.adapter = adapter
    }


    private fun closeConnect() {
        cn.close()
        ps.close()
        rs.close()
    }
}