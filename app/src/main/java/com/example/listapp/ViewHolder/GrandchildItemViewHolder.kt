package com.example.listapp.ViewHolder

import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.listapp.R

class GrandchildItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val nameTextView: EditText = itemView.findViewById(R.id.grandchildItemNameTextView)
}
