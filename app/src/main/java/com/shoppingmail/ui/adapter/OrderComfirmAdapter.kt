package com.shoppingmail.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shoppingmail.GoodsDetailActivity
import com.shoppingmail.R
import com.shoppingmail.ui.object_class.OrderComfirm

/**
 *   author ： Jason
 *   time    ： 2021/4/6
 */
class OrderComfirmAdapter(val itemList:List<OrderComfirm>) :
        RecyclerView.Adapter<OrderComfirmAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val itemPathImage:ImageView = view.findViewById(R.id.submit_image)
        val itemName:TextView = view.findViewById(R.id.submit_goodsname)
        val itemPrice:TextView = view.findViewById(R.id.order_text_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_comfirm_item, parent, false)
        val viewHolder = ViewHolder(view)

        viewHolder.itemPathImage.setOnClickListener {
            val position = viewHolder.adapterPosition
            val item = itemList[position]
            val intent = Intent(parent.context, GoodsDetailActivity::class.java)
            intent.putExtra("items_id", item.itemId)
            intent.putExtra("items_name", item.itemName)
            intent.putExtra("items_title", item.itemTitle)
            intent.putExtra("items_price", item.itemPrice)
            intent.putExtra("items_detailImage", item.itemPathImage)
            parent.context.startActivity(intent)
        }

        viewHolder.itemName.setOnClickListener {
            val position = viewHolder.adapterPosition
            val item = itemList[position]
            val intent = Intent(parent.context, GoodsDetailActivity::class.java)
            intent.putExtra("items_id", item.itemId)
            intent.putExtra("items_name", item.itemName)
            intent.putExtra("items_title", item.itemTitle)
            intent.putExtra("items_price", item.itemPrice)
            intent.putExtra("items_detailImage", item.itemPathImage)
            parent.context.startActivity(intent)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        Glide.with(holder.itemPathImage)
            .load(item.itemPathImage)
            .into(holder.itemPathImage)
        holder.itemName.text = item.itemName
        holder.itemPrice.text = item.itemPrice.toString()
    }

    override fun getItemCount() = itemList.size

}