package com.daon.youtube_part4_01

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.daon.youtube_part4_01.adapter.VideoAdapter
import com.daon.youtube_part4_01.databinding.FragmentPlayerBinding
import com.daon.youtube_part4_01.dto.VideoDto
import com.daon.youtube_part4_01.service.VideoService
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.firebase.firestore.EventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var binding : FragmentPlayerBinding? = null

    private lateinit var videoAdapter : VideoAdapter
    private var player : SimpleExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        initRecyclerView(fragmentPlayerBinding)
        initPlayer(fragmentPlayerBinding)
        initControlButton(fragmentPlayerBinding)
        getVideoList()
    }

    private fun initRecyclerView (fragmentPlayerBinding: FragmentPlayerBinding) {
        videoAdapter = VideoAdapter(callback = { source, title ->
            play(source, title)
        })
        fragmentPlayerBinding.fragmentRecyclerView.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initPlayer (fragmentPlayerBinding: FragmentPlayerBinding) {
        context?.let {
            player = SimpleExoPlayer.Builder(it).build()
        }
        fragmentPlayerBinding.playerView.player = player

        binding?.let {
            player?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)

                    if (isPlaying) {
                        it.bottomPlayerControlButton.setImageResource(R.drawable.pause_button)
                    } else {
                        it.bottomPlayerControlButton.setImageResource(R.drawable.play_button)
                    }
                }
            })
        }
    }

    private fun initControlButton (fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.bottomPlayerControlButton.setOnClickListener {
            val player = this.player ?: return@setOnClickListener

            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
    }

    private fun getVideoList () {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also {
            it.listVideos().enqueue(object : Callback<VideoDto> {
                override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                    if (response.isSuccessful.not()) {
                        Log.d("MainActivity", "response fail")
                        return
                    }

                    response.body()?.let { dto ->
                        Log.d("MainActivity", dto.toString())
                        videoAdapter.submitList(dto.videos)
                    }
                }
                override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                    // 예외 처리
                    Log.d("MainActivity", t.toString())
                }
            })
        }
    }

    fun play (source : String, title : String) {
        context?.let {
            val dataSourceFactory = DefaultDataSourceFactory(it)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(source)))
            player?.setMediaSource(mediaSource)
            player?.prepare()
            player?.play()
        }

        binding?.let {
            // end 상태로 이동
            it.playerMotionLayout.transitionToEnd()
            it.bottomTitleTextView.text = title
        }
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        player?.release()
    }
}