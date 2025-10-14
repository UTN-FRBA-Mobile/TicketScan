package com.example.ticketscan.ia.internal

import com.example.ticketscan.domain.model.TicketItem
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface IAApi {
    @Multipart
    @POST("analyze/image")
    suspend fun analyzeImage(@Part image: MultipartBody.Part): List<TicketItem>

    @Multipart
    @POST("analyze/audio")
    suspend fun analyzeAudio(@Part audio: MultipartBody.Part): List<TicketItem>

    @POST("analyze/items")
    suspend fun analyzeItems(@Body items: Map<String, Double>): List<TicketItem>
}
