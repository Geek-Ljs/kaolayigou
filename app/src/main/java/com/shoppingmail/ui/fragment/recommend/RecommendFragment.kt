package com.shoppingmail.ui.fragment.recommend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.adapter.HomeAdapter
import com.shoppingmail.R
import com.shoppingmail.RecommendActivity
import com.shoppingmail.ui.object_class.DataBean
import com.spring.usekotlin.BannerImageAdapter
import com.youth.banner.indicator.RectangleIndicator
import kotlinx.android.synthetic.main.fragment_recommend.*
import kotlinx.android.synthetic.main.fragment_recommend.view.*

class RecommendFragment() : Fragment() {
//    private lateinit var recommendViewModel: RecommendViewModel
//    private var imageUrls = listOf(
//        "http://600086.xyz:8887/picture/keyDrawing/keyDrawing_1.jpg",
//        "http://600086.xyz:8887/picture/keyDrawing/keyDrawing_2.jpg",
//        "http://600086.xyz:8887/picture/keyDrawing/keyDrawing_3.jpg",
//        "http://600086.xyz:8887/picture/keyDrawing/keyDrawing_4.jpg",
//    )



    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?
    ): View?{
//        recommendViewModel =
//            ViewModelProvider(this).get(RecommendViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_recommend, container, false)
//        val textView : TextView = root.findViewById(R.id.text_recommend)
//        recommendViewModel.text.observe(viewLifecycleOwner, {
//            textView.text = it
//        })
//        var adapter = BannerImageAdapter(imageUrls)
//        root.banner?.let {
//            it.addBannerLifecycleObserver(this)
//            it.setIndicator(RectangleIndicator(context))
//            it.setBannerRound(24f)
//            it.adapter = adapter
//        }
        val homeAdapter by lazy {
            HomeAdapter(context as AppCompatActivity)
        }

        //加载首页布局
        root.home_recyc!!.layoutManager = LinearLayoutManager(context)
        root.home_recyc!!.adapter = homeAdapter
        homeAdapter.setDatas(inithomeAdapter())

        root.main_search_edittext.setIconifiedByDefault(true)
//        root.main_search_edittext.onActionViewExpanded()
        return root
    }

    //初始首页布局数据
    fun inithomeAdapter() : MutableList<DataBean> {
        val home_list = mutableListOf<DataBean>()
        home_list.add(DataBean("banner", 1))
        home_list.add(DataBean("menu", 2))
        home_list.add(DataBean("advertise", 3))
        home_list.add(DataBean("goodslist", 4))
        return home_list
    }
}