package com.example.test.viewholder

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.shoppingmail.R
import com.shoppingmail.kaolahaigou

class AdvertiseViewHodler(itemView: View, val activity: AppCompatActivity) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {

    lateinit var ad_left: ImageView


    override fun onClick(v: View) {
        when(v.id) {
            R.id.ad_left_image -> {
//                val intent = Intent(itemView.context, kaolahaigou::class.java)
//                startActivity(intent)
                val intent = Intent(activity, kaolahaigou::class.java)
                activity.startActivity(intent)
            }
        }
    }

    init {
        ad_left = itemView.findViewById(R.id.ad_left_image)
        ad_left.setOnClickListener(this)
    }

}