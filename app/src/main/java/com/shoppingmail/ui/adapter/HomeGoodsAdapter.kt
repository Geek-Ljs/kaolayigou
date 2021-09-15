package com.example.test.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.test.adapter.HomeGoodsAdapter.ViewHolder
import com.shoppingmail.GoodsDetailActivity
import com.shoppingmail.R
import com.shoppingmail.ui.object_class.HomeGoods


class HomeGoodsAdapter(val activity: AppCompatActivity, val homegoodsList: List<HomeGoods>) :
        RecyclerView.Adapter<ViewHolder>() {

    //跳转到商品详情页时需要传输的数据，还有一个地址，等下考虑
//    companion object{
//        fun actionStarGoodsDetailActivity(activity: AppCompatActivity, product_items_id:Long, product_items_name:String,
//                                          product_items_title:String, product_items_price:Double, product_items_detailImage:String){
//            val intent = Intent(activity, GoodsDetailActivity::class.java)
//            intent.putExtra("items_id", product_items_id)
//            intent.putExtra("items_name", product_items_name)
//            intent.putExtra("items_title", product_items_title)
//            intent.putExtra("items_price", product_items_price)
//            intent.putExtra("items_detailImage", product_items_detailImage)
////            activity.startActivity(intent)
//        }
//    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val goodsImage: ImageView = view.findViewById(R.id.goods_image)
        val goodsName: TextView = view.findViewById(R.id.goods_title)
        val goodsprice: TextView = view.findViewById(R.id.goods_price)
        val monthly_sales: TextView = view.findViewById(R.id.goods_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.goods_item_layout, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
//            actionStarGoodsDetailActivity(activity, homegoods.itemId, homegoods.name, homegoods.title, homegoods.price, homegoods.detailImage)
            val position = viewHolder.adapterPosition
            val homegoods = homegoodsList[position]
            val intent = Intent(activity, GoodsDetailActivity::class.java)
            intent.putExtra("items_id", homegoods.itemId)
            intent.putExtra("items_name", homegoods.name)
            intent.putExtra("items_title", homegoods.title)
            intent.putExtra("items_price", homegoods.price)
            intent.putExtra("items_detailImage", homegoods.detailImage)
            activity.startActivity(intent)
        }


        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val homegoods = homegoodsList[position]
//        hold = holder
        Glide.with(holder.goodsImage)
                .load(homegoods.imagePath)
                .into(holder.goodsImage)
//        holder.goodsImage.setImageURI(homeg)
        holder.goodsName.text = homegoods.name
        holder.goodsprice.text = homegoods.price.toString()
        holder.monthly_sales.text = homegoods.monthly_sales
    }



    override fun getItemCount() = homegoodsList.size

}
