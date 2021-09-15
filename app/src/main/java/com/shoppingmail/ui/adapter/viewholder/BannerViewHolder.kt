package com.example.test.viewholder

import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.jama.carouselview.CarouselView
import com.shoppingmail.R
import com.shoppingmail.ui.adapter.viewholder.CarouselAdapter
import com.shoppingmail.ui.widget.CarouselConstraintLayout
import com.youth.banner.indicator.RectangleIndicator
import kotlinx.android.synthetic.main.home_carousel_item.view.*


class BannerViewHolder(itemView: View, activity: AppCompatActivity) :
        RecyclerView.ViewHolder(itemView) {

    private val viewPager = itemView.home_carousel_viewpager2

//    private val banner: CarouselView = itemView.findViewById(R.id.banner)

    //设置数据
//    val imageUrls = listOf(
//            (R.drawable.banner_one),
//            (R.drawable.banner_two),
//            (R.drawable.banner_three),
//            (R.drawable.banner_four),
//    )

    init {

        //第三种轮播图
        // 1. 设置ViewPager的Adapter
        val carouselViewPager = viewPager
        carouselViewPager.adapter = CarouselAdapter(activity)
        carouselViewPager.currentItem = 2000

        // 2. 指示器 & 轮播
        itemView.home_carousel_indicator.setViewPager2(carouselViewPager, 4)
        val carouselConstraintLayout = itemView as CarouselConstraintLayout
        carouselConstraintLayout.startCarousel(carouselViewPager)

        //加载轮播图（第二种）
//        banner.apply {
//            size = imageUrls.size
//            resource = R.layout.image_carousel_item
//            hideIndicator(true)
//            setCarouselViewListener { view, position ->
//                val imageView = view.findViewById<ImageView>(R.id.imageView)
//                imageView.setImageDrawable(resources.getDrawable(imageUrls[position]))
//            }
//            show()
//        }

        //加载轮播图（第一种）
//        val adapter = MyBannerImageAdapter(imageUrls)
//        banner?.let {
//            it.addBannerLifecycleObserver(this)
//            it.setIndicator(RectangleIndicator(activity))
//            it.setBannerRound(24f)
//            it.adapter = adapter
//        }
    }

    fun invalidate() {
        viewPager.currentItem = viewPager.currentItem
    }
}