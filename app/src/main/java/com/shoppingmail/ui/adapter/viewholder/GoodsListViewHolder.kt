package com.example.test.viewholder

import android.os.Handler
import android.os.Message
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.test.adapter.HomeGoodsAdapter
import com.shoppingmail.R
import com.shoppingmail.logic.dao.DatabaseOperations
import com.shoppingmail.ui.object_class.HomeGoods
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class GoodsListViewHolder(itemView: View, activity: AppCompatActivity) :
        RecyclerView.ViewHolder(itemView) {
    private lateinit var cn: Connection
    private lateinit var ps: PreparedStatement
    private lateinit var rs: ResultSet
    private val goods_recy: RecyclerView = itemView.findViewById(R.id.home_goods_recyc)
    private val home_goods_list = mutableListOf<HomeGoods>()

    //这里是一个子线程，用于获取数据库商品信息
    val displayHomeGoodsPiture = 1

    val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when(msg.what){
//                loadHomeGoods -> {
//                    inithome_goods_recyc()
//                }

                displayHomeGoodsPiture -> {
                    val adapter = HomeGoodsAdapter(activity, home_goods_list)
                    goods_recy.adapter = adapter
                }
            }
        }
    }

    init {
        goods_recy.layoutManager = GridLayoutManager(activity, 2)
        inithome_goods_recyc()
    }

    //在HomeGoods页中增加商品
    fun inithome_goods_recyc() {
        try {
            val sql:String = "Select product_items_id, product_items_thumbnail, product_items_name, product_items_price, product_items_sales, product_items_title, product_items_detailImage From items Where product_items_category_id = 1"
//            val sql:String = "Select product_items_thumbnail, product_items_name, product_items_price, product_items_sales From items Where product_items_category_id = 1"
            Thread(Runnable {
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
                rs = ps.executeQuery()
                while (rs.next()){
//                    println(rs.getString(1) + "  " + rs.getString(2) + "  " + rs.getDouble(3).toString() + "  " + rs.getInt(4).toString()+"销量")
                    home_goods_list.add(HomeGoods(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getInt(5).toString()+"销量", rs.getString(6), rs.getString(7)))
//                    home_goods_list.add(HomeGoods(rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getInt(4).toString()+"销量"))
                }
                val msg = Message()
                msg.what = displayHomeGoodsPiture
                handler.sendMessage(msg)
                closeConnect()
            }).start()
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