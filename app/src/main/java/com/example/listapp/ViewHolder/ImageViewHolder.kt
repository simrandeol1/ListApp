package com.example.listapp.ViewHolder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.listapp.R

class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val viewPager: ViewPager2 = itemView.findViewById(R.id.view_pager)
    val title: TextView = itemView.findViewById(R.id.txtView)
}