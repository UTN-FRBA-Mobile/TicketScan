package com.example.ticketscan.ia.internal

import com.example.ticketscan.domain.AnalizedItem
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface IAApi {
    @Multipart
    @POST("analyze/image")
    suspend fun analizeImage(@Part image: MultipartBody.Part): List<AnalizedItem>

    @Multipart
    @POST("analyze/audio")
    suspend fun analizeAudio(@Part audio: MultipartBody.Part): List<AnalizedItem>

    @POST("analyze/items")
    suspend fun analizeItems(@Body items: Map<String, Double>): List<AnalizedItem>
}

