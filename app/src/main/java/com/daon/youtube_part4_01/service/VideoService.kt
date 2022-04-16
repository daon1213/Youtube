package com.daon.youtube_part4_01.service

import com.daon.youtube_part4_01.dto.VideoDto
import retrofit2.Call
import retrofit2.http.GET

interface VideoService {

    @GET("/v3/9d72ee8c-7945-4494-b907-0481d260bccd")
    fun listVideos(): Call<VideoDto>
}