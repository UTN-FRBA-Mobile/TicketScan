package com.example.ticketscan.ia.internal

import com.example.ticketscan.ia.internal.dto.TicketDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface IAApi {
    @Multipart
    @POST("api/analyze/image")
    suspend fun analyzeImage(@Part image: MultipartBody.Part): TicketDto

    @Multipart
    @POST("api/analyze/audio")
    suspend fun analyzeAudio(@Part audio: MultipartBody.Part): TicketDto

    @POST("api/analyze/text")
    suspend fun analyzeText(@Body items: Map<String, Double>): TicketDto
}
