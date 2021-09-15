package com.shoppingmail.ui.adapter.viewholder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.shoppingmail.R
import kotlinx.android.synthetic.main.item_carousel.view.*

class CarouselAdapter(context: Context) : Adapter<CarouselAdapter.CarouselViewHolder>() {

    private var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val itemView = inflater.inflate(R.layout.item_carousel, parent, false)
        return CarouselViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val imgRes = when {
            position % 4 == 0 -> R.drawable.banner5
            position % 4 == 1 -> R.drawable.banner_one
            position % 4 == 2 -> R.drawable.banner6
            else -> R.drawable.banner_four
        }
        holder.itemView.carousel_img.setImageResource(imgRes)
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    inner class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
}