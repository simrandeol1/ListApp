package com.example.listapp.ViewHolder

import android.view.View
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.listapp.R

class VideoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val videoView: VideoView = itemView.findViewById(R.id.videoView)
}