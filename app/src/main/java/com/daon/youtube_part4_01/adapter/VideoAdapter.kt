package com.daon.youtube_part4_01.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daon.youtube_part4_01.R
import com.daon.youtube_part4_01.model.VideoModel

class VideoAdapter(
    val callback : (String, String) -> Unit): ListAdapter<VideoModel, VideoAdapter.VideoViewHolder>(diffUtil){

    inner class VideoViewHolder(
        private val view : View
    ) : RecyclerView.ViewHolder(view) {

        fun bind (videoModel: VideoModel) {
            val thumbnailImageView = view.findViewById<ImageView>(R.id.thumbnailImageView)
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            val subtitleTextView = view.findViewById<TextView>(R.id.subtitleTextView)

            titleTextView.text = videoModel.title
            subtitleTextView.text = videoModel.subtitle
            Glide.with(thumbnailImageView.context)
                .load(videoModel.thumb)
                .into(thumbnailImageView)

            view.setOnClickListener {
                callback(videoModel.sources, videoModel.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return VideoViewHolder(
            inflater.inflate(R.layout.item_video, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<VideoModel>() {
            override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel)
                    = oldItem == newItem
            override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel)
                    = oldItem == newItem
        }
    }
}