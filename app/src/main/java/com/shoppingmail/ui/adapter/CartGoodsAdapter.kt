package com.shoppingmail.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shoppingmail.GoodsDetailActivity
import com.shoppingmail.R
import com.shoppingmail.ui.fragment.cart.CartFragment
import com.shoppingmail.ui.object_class.Cart_Goods
import kotlinx.android.synthetic.main.cart_goods_item.view.*


class CartGoodsAdapter(val cartgoodsList: MutableList<Cart_Goods>, var cartFragment: CartFragment) :
        RecyclerView.Adapter<CartGoodsAdapter.ViewHolder>() {

    private var IsSelectAll = false
    var selectGoodsPrice: Double = 0.0
    var selectGoodsCount: Int = 0

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val goodsName: TextView = view.findViewById(R.id.goods_name_text)
        val goodsImage: ImageView = view.findViewById(R.id.goods_image)
        val goodsPrice: TextView = view.findViewById(R.id.cart_text_price)
        val goodsNum: TextView = itemView.findViewById(R.id.goods_num_text)
        val goodsType: Button = itemView.findViewById(R.id.goods_type_btn)
        val goodsCheckBox : CheckBox = view.findViewById(R.id.goods_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cart_goods_item, parent, false)
        val viewHolder = ViewHolder(view)

        //商品数量增加点击事件
        viewHolder.itemView.goods_add_btn.setOnClickListener {
            val position = viewHolder.adapterPosition
            val goods = cartgoodsList[position]

            var num = goods.number
            num++
            goods.number = num
            viewHolder.itemView.goods_num_text.setText(num.toString())

            val checkbox = viewHolder.itemView.goods_checkbox
            if (checkbox.isChecked) {
                selectGoodsPrice += goods.productItemsPrice
            }
            //更新价格
            cartFragment.setTotalMoneyText(selectGoodsPrice.toString())
        }

        //商品数量减少点击事件
        viewHolder.itemView.goods_reduce_btn.setOnClickListener {
            val position = viewHolder.adapterPosition
            val goods = cartgoodsList[position]
            var num = goods.number
            if (num > 1) {
                num--
                goods.number = num
                viewHolder.itemView.goods_num_text.setText(num.toString())

                val checkbox = viewHolder.itemView.goods_checkbox
                if (checkbox.isChecked) {
                    selectGoodsPrice -= goods.productItemsPrice
                }
            }
            //更新价格
            cartFragment.setTotalMoneyText(selectGoodsPrice.toString())
        }

        //选中
        viewHolder.itemView.goods_checkbox.setOnClickListener {
            val position = viewHolder.adapterPosition
            val goods = cartgoodsList[position]
            val checkbox = viewHolder.itemView.goods_checkbox

            if (checkbox.isChecked) {
                itemCalculation(goods.productItemsPrice, goods.number, "add")
                goods.isSelect = true
                selectGoodsCount++
//                Toast.makeText(parent.context, "已选${text}", Toast.LENGTH_SHORT).show()
            }else{
                itemCalculation(goods.productItemsPrice, goods.number, "reduce")
                goods.isSelect = false
                selectGoodsCount--
//                Toast.makeText(parent.context, "未选${totalprice}", Toast.LENGTH_SHORT).show()
            }

            if (whetherAllSelect()) {
                cartFragment.setAllSelectCheckBox(true)
                selectGoodsCount = cartgoodsList.size
            } else {
                cartFragment.setAllSelectCheckBox(false)
            }

            //更新价格
            cartFragment.setTotalMoneyText(selectGoodsPrice.toString())
            //更新结算商品数量
            cartFragment.setSubmitBtnText(selectGoodsCount.toString())
        }

        viewHolder.goodsImage.setOnClickListener {
            val position = viewHolder.adapterPosition
            val cartGoodsToDetail = cartgoodsList[position]
            val intent = Intent(parent.context, GoodsDetailActivity::class.java)
            intent.putExtra("items_id", cartGoodsToDetail.productItemsId)
            intent.putExtra("items_name", cartGoodsToDetail.productItemsName)
            intent.putExtra("items_title", cartGoodsToDetail.productItemsTitle)
            intent.putExtra("items_price", cartGoodsToDetail.productItemsPrice)
            intent.putExtra("items_detailImage", cartGoodsToDetail.productItemsDetailImage)
            parent.context.startActivity(intent)
        }

        viewHolder.goodsName.setOnClickListener {
            val position = viewHolder.adapterPosition
            val cartGoodsToDetail = cartgoodsList[position]
            val intent = Intent(parent.context, GoodsDetailActivity::class.java)
            intent.putExtra("items_id", cartGoodsToDetail.productItemsId)
            intent.putExtra("items_name", cartGoodsToDetail.productItemsName)
            intent.putExtra("items_title", cartGoodsToDetail.productItemsTitle)
            intent.putExtra("items_price", cartGoodsToDetail.productItemsPrice)
            intent.putExtra("items_detailImage", cartGoodsToDetail.productItemsDetailImage)
            parent.context.startActivity(intent)
        }

        return viewHolder
    }

    //这一部分待修改
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val goods = cartgoodsList[position]

        holder.goodsName.text = goods.productItemsName
        Glide.with(holder.goodsImage)
                .load(goods.productItemsDetailImage)
                .into(holder.goodsImage)
        holder.goodsPrice.text = goods.productItemsPrice.toString()
        holder.goodsNum.text = goods.number.toString()
        holder.goodsType.text = goods.type
//        holder.cartgoodsImage.setImageResource(goods.imageId)
//        holder.cartgoodsName.text = goods.name

        //判断全选
        if (IsSelectAll) {
            goods.isSelect = true
        } else {
            goods.isSelect = false
            selectGoodsPrice = 0.0
        }
        holder.goodsCheckBox.isChecked = goods.isSelect
    }

    override fun getItemCount() = cartgoodsList.size

    //设置全选
    fun setIsSelectAll(isSelectAll: Boolean) {
        if (isSelectAll) {
            totalCalculation()
        } else {
            selectGoodsPrice = 0.0
        }
        this.IsSelectAll = isSelectAll

    }

    //单个item计算
    fun itemCalculation(goodsPrice:Double, goodsNum:Int, sympol:String) {
        if (sympol.equals("add")) {
//            selectGoodsPrice += goodsPrice * goodsNum
            selectGoodsPrice += String.format("%.2f", (goodsPrice * goodsNum)).toDouble()
        }
        else if (sympol.equals("reduce")) {
            if (selectGoodsPrice > 0) {
//                selectGoodsPrice -= goodsPrice * goodsNum
                selectGoodsPrice -= String.format("%.2f", (goodsPrice * goodsNum)).toDouble()
            }
        }
    }

    //全选计算
    fun totalCalculation() {
        selectGoodsPrice = 0.0
        for (itemgoods in cartgoodsList) {
            itemCalculation(itemgoods.productItemsPrice, itemgoods.number, "add")
        }
    }

    //判断选中单个item是否全部item被选中
    fun whetherAllSelect():Boolean {
        for (itemGoods in cartgoodsList) {
            if (!itemGoods.isSelect) {
                IsSelectAll = false
                break
            } else {
                IsSelectAll = true
            }
        }
        return IsSelectAll
    }
}