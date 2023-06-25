package com.example.listapp.ViewHolder

import android.view.View
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.listapp.R

class VideoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val videoView: VideoView = itemView.findViewById(R.id.videoView)
    val frameLayout: FrameLayout = itemView.findViewById(R.id.frameLayout)
}