package com.example.listapp.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.listapp.R

class NestedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
}