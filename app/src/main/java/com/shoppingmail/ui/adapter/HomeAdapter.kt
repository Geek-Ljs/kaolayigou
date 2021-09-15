package com.example.test.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

import com.example.test.viewholder.AdvertiseViewHodler
import com.example.test.viewholder.BannerViewHolder
import com.example.test.viewholder.GoodsListViewHolder
import com.example.test.viewholder.MenuViewHolder
import com.shoppingmail.R
import com.shoppingmail.ui.object_class.DataBean

class HomeAdapter(context:AppCompatActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var liquidates = mutableListOf<DataBean>()

    private val context: AppCompatActivity = context

    private val inflater = LayoutInflater.from(context)

    //标识布局类型
    companion object {
        //轮播图
        private  const val VIEW_HOME_BANNER = 1

        //菜单
        private const val VIEW_HOME_MENU = 2

        //菜单下的广告
        private const val VIEW_HOME_ADVERTISE = 3

        //商品流
        private const val VIEW_HOME_GOODS_LIST = 4
    }

    /**
     * 这个方法重要
     * 说明 根据数据源中bean中 type标识返回当前布局类型
     */
    override fun getItemViewType(position: Int): Int = when (liquidates[position].Item_Type) {
            1 -> { VIEW_HOME_BANNER }
            2 -> { VIEW_HOME_MENU }
            3 -> { VIEW_HOME_ADVERTISE }
            4 -> { VIEW_HOME_GOODS_LIST }
            else -> -1

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_HOME_BANNER -> {
                //轮播图
//                val itemView = inflater.inflate(R.layout.banner_item_layout, parent, false)
                val itemView = inflater.inflate(R.layout.home_carousel_item, parent, false)
                BannerViewHolder(itemView, context)
//                BannerViewHolder((LayoutInflater.from(parent.context).inflate(R.layout.banner_item_layout, parent, false)))
            }
            VIEW_HOME_MENU -> {
                val itemView = inflater.inflate(R.layout.menu_item_layout, parent, false)
                MenuViewHolder(itemView, context)
//                MenuViewHolder((LayoutInflater.from(parent.context).inflate(R.layout.menu_item_layout, parent, false)))
            }
            VIEW_HOME_ADVERTISE -> {
                val itemView = inflater.inflate(R.layout.advertise_item_layout, parent, false)
                AdvertiseViewHodler(itemView, context)
//                AdvertiseViewHodler((LayoutInflater.from(parent.context).inflate(R.layout.advertise_item_layout, parent, false)))
            }
            else -> {
                val itemView = inflater.inflate(R.layout.home_goods_list, parent, false)
                GoodsListViewHolder(itemView, context)
//                GoodsViewHolder((LayoutInflater.from(parent.context).inflate(R.layout.home_goods_list, parent, false)))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BannerViewHolder) {
            holder.invalidate()
        }

        val item = liquidates[position]
        when (holder.itemViewType) {
            VIEW_HOME_BANNER -> {
                val oneViewHolder = holder as BannerViewHolder
            }

            VIEW_HOME_MENU -> {
//                val twoViewHodler = holder as MenuViewHolder
            }

            VIEW_HOME_ADVERTISE -> {
//                val threeViewHolder = holder as AdvertiseViewHodler
            }

            else -> {
//                val fourViewHolder = holder as GoodsViewHolder
            }
        }

    }

    override fun getItemCount(): Int {
        return liquidates.size
    }

    fun setDatas(lists: MutableList<DataBean>) {
        this.liquidates = lists
        notifyDataSetChanged()
    }

}