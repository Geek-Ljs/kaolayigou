package com.shoppingmail.ui.fragment.cart

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shoppingmail.*
import com.shoppingmail.logic.dao.DatabaseOperations
import com.shoppingmail.ui.adapter.CartGoodsAdapter
import com.shoppingmail.ui.fragment.message.MessageViewModel
import com.shoppingmail.ui.object_class.Cart_Goods
import com.shoppingmail.ui.object_class.OrderComfirmCart
import kotlinx.android.synthetic.main.cart_goods_item.view.*
import kotlinx.android.synthetic.main.fragment_cart.view.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class CartFragment : Fragment(), View.OnClickListener {

    private lateinit var cn: Connection
    private lateinit var ps: PreparedStatement
    private lateinit var rs: ResultSet

    private lateinit var cartViewModel: CartViewModel
    private lateinit var layoutManager: LinearLayoutManager
    private var recycleView: RecyclerView? = null

    private var itemList = ArrayList<OrderComfirmCart>()

    private lateinit var cartGoodsAdapter: CartGoodsAdapter
    private val cartgoodsList = mutableListOf<Cart_Goods>()
    private lateinit var allSelectCheckBox: CheckBox
    private lateinit var totalMoneyText: TextView
    private lateinit var submitButton: Button
    private lateinit var editText: TextView

    //记录被选中商品的价格
    private var selectGoodsPrice = 0.0
    //记录编辑按钮状态
    private var isEditText = false

    //这一部分是要从CartFragment传输到ComfirmOrderActivity的数据
    private var items_detail_Image:String = ""
    private var items_detail_name:String = ""
    private var receiverItemId : Long = 0

//    private var presentItemId:Long = 0

    //测试地址
    private

    //异步操作，购物车的展现
    val showCartGoods = 1
    //删除购物车商品更新数据
    val updateCartGoods = 2
    val handler = object : Handler(){
        override fun handleMessage(msg: Message) {
            when(msg.what){
                showCartGoods -> {
                    cartgoodsList.reverse()
                    cartGoodsAdapter = CartGoodsAdapter(cartgoodsList, this@CartFragment)
                    recycleView?.adapter = cartGoodsAdapter
                }
                updateCartGoods -> {
                    setTotalMoneyText("0")
                    setAllSelectCheckBox(false)
//                    cartGoodsAdapter.notifyDataSetChanged()
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        cartViewModel =
//            ViewModelProvider(this).get(CartViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_cart, container, false)

        //加载商品列表
        initCartGoods()
        layoutManager = LinearLayoutManager(context)
        recycleView = root.findViewById(R.id.cart_recyclerview)
        recycleView?.layoutManager = layoutManager

//        val title = (activity as RecommendActivity).showTitle()
//        println(title)

        totalMoneyText = root.findViewById(R.id.total_money_text)
//        val totalText:TextView = root.findViewById(R.id.totalTextView)
//        totalText.setOnClickListener(this)
        allSelectCheckBox = root.findViewById(R.id.allselect_checkbox)
        allSelectCheckBox.setOnClickListener(this)
        submitButton = root.findViewById(R.id.submitButton)
        submitButton.setOnClickListener(this)
        editText = root.findViewById(R.id.edit_text)
        editText.setOnClickListener(this)

        //订单结算
//        root.submitButton.setOnClickListener {
//            var totalMoney:Double = 0.0
//            totalMoney = totalMoneyText.text.toString().toDouble()
//            if (totalMoney<=0){
//                AlertDialog.Builder(activity as AppCompatActivity).apply {
//                    setTitle("错误提示")
//                    setMessage("请选择商品！")
//                }.show()
//            }else{
//                //这一步可以跳转
//                val intent = Intent(activity, ComfirmOrderActivity::class.java)
//                intent.putExtra("order_reciver_gooos_image", items_detail_Image)
//                intent.putExtra("order_reciver_goods_name", items_detail_name)
//                intent.putExtra("order_receiver_item_id", receiverItemId)
//                intent.putExtra("order_reciver_goods_price", totalMoney)
//                startActivity(intent)
//            }
//        }
        return root

//        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    //初始recyView数据
    private fun initCartGoods() {
        val prefs: SharedPreferences? = activity?.getSharedPreferences("data", Context.MODE_PRIVATE)
        val user_id = prefs?.getLong("present_user_id", 0)
        val sql:String = "Select product_items_id, product_items_name, product_items_title, product_items_price, product_items_detailImage From items Where product_items_id In " +
                "(Select cart_carts_item_id From carts Where cart_carts_member_id = ? Order By cart_carts_id Desc)"
        try{
            Thread{
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                ps.setLong(1, user_id!!)
                rs = ps.executeQuery()
                while (rs.next()){
                    cartgoodsList.add(Cart_Goods(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getString(5)))
                }
                val msg = Message()
                msg.what = showCartGoods
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

    override fun onClick(v: View) {
        when(v.id) {
            //全选点击事件
            R.id.allselect_checkbox -> {
                if (allSelectCheckBox.isChecked) {
                    cartGoodsAdapter.setIsSelectAll(true)
                    cartGoodsAdapter.selectGoodsCount = cartGoodsAdapter.cartgoodsList.size
                }else {
                    cartGoodsAdapter.setIsSelectAll(false)
                    cartGoodsAdapter.selectGoodsCount = 0
                }
                //刷新recyclerview界面数据
                cartGoodsAdapter.notifyDataSetChanged()

//                calculation()
                //更新价格
//                setTotalMoneyText(selectGoodsPrice.toString())
                setTotalMoneyText(cartGoodsAdapter.selectGoodsPrice.toString())

                //更新结算商品数量
                setSubmitBtnText(cartGoodsAdapter.selectGoodsCount.toString())
            }

            //结算按钮点击事件
            R.id.submitButton -> {
                if(isEditText) {
                    //删除逻辑
                    for (i in 0..cartgoodsList.size-1) {
                        if (cartgoodsList[i].isSelect) {
//                            Toast.makeText(v.context, "${cartgoodsList[i].productItemsId}" +
//                                    "${cartgoodsList[i].productItemsName}"
//                                    +"已删除", Toast.LENGTH_SHORT).show()
                            val prefs: SharedPreferences? = activity?.getSharedPreferences("data", Context.MODE_PRIVATE)
                            val user_id = prefs?.getLong("present_user_id", 0)
                            val sql = "Delete From carts Where cart_carts_member_id = ? And cart_carts_item_id = ?"
                            try{
                                Thread{
                                    cn = DatabaseOperations().mysqlConnect()!!
                                    ps = cn.prepareStatement(sql)
                                    ps.setLong(1, user_id!!)
                                    ps.setLong(2, cartgoodsList[i].productItemsId)
                                    ps.executeUpdate()

                                    val msg = Message()
                                    msg.what = updateCartGoods
                                    handler.sendMessage(msg)
                                    closeConnect()
                                    cartgoodsList.removeAt(i)
                                    cartGoodsAdapter.notifyDataSetChanged()
                                }.start()

                            }catch (e:Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                } else {
                    var totalMoney:Double = 0.0
                    totalMoney = totalMoneyText.text.toString().toDouble()
                    if (totalMoney<=0){
                        AlertDialog.Builder(activity as AppCompatActivity).apply {
                            setTitle("错误提示")
                            setMessage("请选择商品！")
                        }.show()
                    } else {
                        //这一步可以跳转
                        val intent = Intent(activity, OrderComfirmToCartActivity::class.java)
//                        intent.putExtra("order_reciver_gooos_image", items_detail_Image)
//                        intent.putExtra("order_reciver_goods_name", items_detail_name)
//                        intent.putExtra("order_receiver_item_id", receiverItemId)
                        test()
                        println("长度" + itemList.size)
                        intent.putExtra("itemList_data", itemList)
                        intent.putExtra("order_reciver_total_price", totalMoney)
                        startActivity(intent)
                        itemList.clear()
                    }
                }
            }

            //编辑按钮点击事件
            R.id.edit_text -> {
                isEditText = !isEditText
                if (isEditText) {
                    editText.setText("完成")
                } else {
                    editText.setText("管理")
                }
                //更新结算商品数量
                setSubmitBtnText(cartGoodsAdapter.selectGoodsCount.toString())
            }

        }
    }

    fun calculation(){
//        var totalPrice = 0.0
//        var str: String
        selectGoodsPrice = 0.0
        for (itemGoods in cartGoodsAdapter.cartgoodsList) {
            if (!itemGoods.isSelect) {
//                items_detail_Image = itemGoods.productItemsDetailImage
//                items_detail_name= itemGoods.productItemsName
//                receiverItemId = itemGoods.productItemsId
//                selectGoodsPrice += itemGoods.productItemsPrice * itemGoods.number
//                str = String.format("%.2f", (itemGoods.productItemsPrice * itemGoods.number))
//                println(itemGoods.productItemsId.toString())
//                println(itemGoods.productItemsDetailImage.toString())
//                println(itemGoods.productItemsName.toString())
//                println(itemGoods.productItemsPrice.toString())
//                println(itemGoods.productItemsTitle)
//                println(itemGoods.number.toString())
                selectGoodsPrice += String.format("%.2f", (itemGoods.productItemsPrice * itemGoods.number)).toDouble()

//                itemList.add(
//                    OrderComfirmCart(itemGoods.productItemsId, itemGoods.productItemsDetailImage,
//                    itemGoods.productItemsName, itemGoods.productItemsPrice, itemGoods.productItemsTitle, itemGoods.number)
//                )
            }
        }
//        return totalPrice
    }

    //设置总价
    fun setTotalMoneyText(totalPrice: String) {
        totalMoneyText.setText(totalPrice)
    }

    //设置全选checkbox状态
    fun setAllSelectCheckBox(isAllSelect: Boolean) {
        allSelectCheckBox.isChecked = isAllSelect
    }

    //设置结算Button商品数量
    fun setSubmitBtnText(goodsNum: String) {
        if (isEditText) {
            submitButton.text = if (goodsNum.equals("0")) "删除" else "删除(${goodsNum})"
        } else {
            submitButton.text = if (goodsNum.equals("0")) "结算" else "结算(${goodsNum})"
        }
    }

    fun test(){
        for (itemGoods in cartGoodsAdapter.cartgoodsList) {
            if (itemGoods.isSelect) {
                itemList.add(
                        OrderComfirmCart(itemGoods.productItemsId, itemGoods.productItemsDetailImage,
                                itemGoods.productItemsName, itemGoods.productItemsPrice, itemGoods.productItemsTitle, itemGoods.number)
                )
            }
        }
    }

}
