package com.shoppingmail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.shoppingmail.logic.dao.DatabaseOperations
import com.shoppingmail.ui.object_class.OrderComfirm
import kotlinx.android.synthetic.main.activity_goods_detail.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class GoodsDetailActivity : AppCompatActivity() {
    private lateinit var cn: Connection
    private lateinit var ps: PreparedStatement
    private lateinit var rs: ResultSet

    private var addressId = 0
    private var items_detail_id:Long = 0
    private var items_detail_Image:String? = null
    private var items_detail_price:Double = 0.00
    private var items_detail_name:String? = null
    private var items_detail_title:String? = null
    private val itemList = ArrayList<OrderComfirm>()

    //存储对应地址

    private lateinit var cartAddressData:ArrayList<String>

    //异步处理，更新地址UI
    val handerShowAddress = 1
    val addGoodsToCartSucessful = 2
    val existDatabase = 3
    val noExistDatabase = 4
    val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what){
                handerShowAddress -> {
                    adapterShowAddress()
//                    println("addressData的长度" + addressData.size.toString())
                }

                addGoodsToCartSucessful -> {
                    ShowDialogOfSuccessful()
                }

                existDatabase -> {
                    ShowDialogOfUnsuccessful()
                }

                noExistDatabase -> {
                    //加入购物车
                    insertGoodsToCarts()
                }


            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goods_detail)
        supportActionBar?.hide()

        items_detail_id = intent.getLongExtra("items_id",0)     //当前商品ID
        items_detail_Image = intent.getStringExtra("items_detailImage")     //当前商品详细图
        items_detail_price = intent.getDoubleExtra("items_price", 0.00) //当前商品价格
        items_detail_name = intent.getStringExtra("items_name")             //当前商品名
        items_detail_title = intent.getStringExtra("items_title")           //当前商品的标题

        showDetailUI()
        showAddress()

        cartAddressData = arrayListOf<String>()

        details_icon_back.setOnClickListener {
            finish()
        }

        //这一步是用于加入购物车，这里有一步是当用户没有登录时，要直接跳转到登录页面
        details_account_addcarts.setOnClickListener {
            //待写登录状态的判断

            //加入购物车前的条件要求

            //将要加入购物车的商品等信息插入到数据库中

            //先判断是否已被加入购物车
            val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
            val userName:String = prefs.getString("present_user_nickname", "").toString()

            //用户是否已经登录
            if (userName != "")
                judgeWhetherExistDatabase()
            else{
                AlertDialog.Builder(this).apply {
                    setTitle("错误提示")
                    setMessage("当前未登录")
                }.show()
            }
        }


        //点击立即购买按钮,跳转到确认订单页
        details_account_purchase.setOnClickListener {
            val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
            val userName:String = prefs.getString("present_user_nickname", "").toString()

            //这个先判断用户是已经登录
            if (userName != "") {
//                //这一步要判断地址是否存在
//                val intent = Intent(this, ComfirmOrderActivity::class.java)
//                if (!details_delivery_address.selectedItem.toString().isEmpty()){
//                    var listAddress: List<String>? = details_delivery_address.selectedItem.toString().split("\n")
//                    val receiverAddress = listAddress!![0]
//                    val receiverName = listAddress!![1]
//                    val receiverTelephony = listAddress!![2]
//                    intent.putExtra("order_receiver_addreess_id", addressId)
//                    intent.putExtra("order_receiver_name", receiverName)
//                    intent.putExtra("order_receiver_telephone", receiverTelephony)
//                    intent.putExtra("order_receiver_address", receiverAddress!!)
//                }
                //这里没有错
//                println(items_detail_id.toString())
//                intent.putExtra("order_receiver_item_id", items_detail_id)
//                intent.putExtra("order_reciver_gooos_image", items_detail_Image!!)
//                intent.putExtra("order_reciver_goods_name", items_detail_name!!)
//                //这一部分待修正
//                intent.putExtra("order_reciver_goods_price", items_detail_price)

                //存入一个列表
                val intent = Intent(this, ComfirmOrderActivity::class.java)
                itemList.add(OrderComfirm(items_detail_id, items_detail_Image!!, items_detail_name!!, items_detail_price, items_detail_title!!))
                intent.putExtra("itemList_data", itemList)
                //启动ComfirmOrderActivity
                startActivity(intent)
                itemList.clear()
            }else{
                AlertDialog.Builder(this).apply {
                    setTitle("错误提示")
                    setMessage("当前未登录")
                }.show()
            }
        }
    }


    private fun showDetailUI() {
        Glide.with(this)
                .load(items_detail_Image)
                .into(details_view)
        details_price.text = "¥ " + items_detail_price.toString()
        details_information_title.text = items_detail_name
        details_information_introduce.text = items_detail_title
    }

    //显示地址
    private fun showAddress() {
        //这里需要传入当前用户的id
            //加载当前用户Id
//        val prefs: SharedPreferences? = getSharedPreferences("data", Context.MODE_PRIVATE)
//        val present_user_id = prefs?.getLong("present_user_id", 0)
        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        val user_id = prefs.getLong("present_user_id", 0)
        val sql:String = "Select addresses_receiver_address, addresses_receiver_name, addresses_receiver_telephone, addresses_id From addresses Where addresses_user_id = ?"
        try {
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setLong(1, user_id!!)
                rs = ps.executeQuery()
                while (rs.next()){
                    val addressString:String = rs.getString(1) + "\n" + rs.getString(2) + "\n" + rs.getString(3)
                    addressId = rs.getInt(4)
//                    println(addressString)
//                    addressData.add(rs.getString(1))
                    cartAddressData.add(addressString)
//                      addressData.add(Address(rs.getString(1), rs.getString(2), rs.getString(3)))
                }
                val msg = Message()
                msg.what = handerShowAddress
                handler.sendMessage(msg)
                closeConnect()
            }.start()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun adapterShowAddress(){
        val adapter = ArrayAdapter<String>(this, R.layout.address_list_item, cartAddressData!!)
        details_delivery_address.adapter = adapter
    }


    //点击 加入购物车按钮
    private fun insertGoodsToCarts() {
        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        val user_id = prefs.getLong("present_user_id", 0)
        try{
            val sql:String = "Insert Into carts(cart_carts_member_id, cart_carts_item_id, cart_carts_price) " +
                    " Values(?, ?, ?)"
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setLong(1, user_id)
                ps.setLong(2, items_detail_id)
                ps.setDouble(3, items_detail_price)
                ps.executeUpdate()
                val msg = Message()
                msg.what = addGoodsToCartSucessful
                handler.sendMessage(msg)
                cn.close()
                ps.close()
            }.start()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    //显示成功加入购物车的弹出框
    private fun ShowDialogOfSuccessful(){
        AlertDialog.Builder(this).apply {
            setTitle("购物车提示")
            setMessage("已成功加入购物车")
        }.show()
    }

    //显示加入购物车不成功弹出框
    private fun ShowDialogOfUnsuccessful(){
        AlertDialog.Builder(this).apply {
            setTitle("购物车提示")
            setMessage("购物车已存在该商品")
        }.show()
    }

    private fun judgeWhetherExistDatabase(){
        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        val user_id = prefs.getLong("present_user_id", 0)

        try{
            val sql:String = "Select * From carts Where cart_carts_member_id = ? And cart_carts_item_id = ?"
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setLong(1, user_id)
                ps.setLong(2, items_detail_id)
                rs = ps.executeQuery()
                rs.last()
                val rowCount = rs.row
//                println("行数：" + rowCount)
                closeConnect()
                val msg = Message()
                if (rowCount > 0){
                    msg.what = existDatabase
                }else{
                    msg.what = noExistDatabase
                }
                handler.sendMessage(msg)
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