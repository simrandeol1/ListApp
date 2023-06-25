package com.example.listapp.ViewHolder

import android.view.View
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.example.listapp.R

class DropDownViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val spinner: Spinner = itemView.findViewById(R.id.spinner)
}
