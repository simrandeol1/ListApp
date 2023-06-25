package com.example.listapp.ViewHolder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.listapp.R

class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleTextView: TextView = itemView.findViewById(R.id.txtView)
    val imageView: ImageView = itemView.findViewById(R.id.arrow)
}
