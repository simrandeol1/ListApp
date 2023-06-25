package com.example.listapp.ViewHolder

import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.listapp.R

class ParentItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleTextView: TextView = itemView.findViewById(R.id.parentItemTitleTextView)
    val radioBtn: RadioButton = itemView.findViewById(R.id.radioBtn)
    val imageView: ImageView = itemView.findViewById(R.id.arrow)
}