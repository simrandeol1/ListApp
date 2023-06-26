package com.example.listapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.listapp.R

class GalleryAdapter(private val context: Context, private val fileArray: MutableList<String>, private val type: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(type){
            "IMAGE"->{
                val imageView = layoutInflater.inflate(R.layout.gallery_item_layout, parent, false)
                ImageViewHolder(imageView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(type){
            "IMAGE"->{
                Glide.with(context).load(fileArray[position]).into((holder as ImageViewHolder).imageView)
            }
        }
    }

    override fun getItemCount(): Int {
        return fileArray.size
    }

    class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}