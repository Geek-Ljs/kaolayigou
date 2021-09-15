package com.shoppingmail.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shoppingmail.R
import com.shoppingmail.ui.object_class.Cart_Goods
import com.shoppingmail.ui.object_class.Order

/**
 *   author ： Jason
 *   time    ： 2021/4/1
 */
class OrderAdapter(val activity: AppCompatActivity, val orderList: List<Order>) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderGoodsImage: ImageView = view.findViewById(R.id.order_image)
        val orderGoodsName: TextView = view.findViewById(R.id.order_goodsname)
        val orderGoodsPrice: TextView = view.findViewById(R.id.order_price)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_carts_item, parent, false)
        val viewHolder = ViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: OrderAdapter.ViewHolder, position: Int) {
        val order = orderList[position]
        Glide.with(holder.orderGoodsImage)
            .load(order.orderGoodsImagePath)
            .into(holder.orderGoodsImage)
        holder.orderGoodsName.text = order.orderGoodsName
        holder.orderGoodsPrice.text = order.orderPrice.toString()
    }

    override fun getItemCount() = orderList.size
}