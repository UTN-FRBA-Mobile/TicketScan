package com.example.ticketscan.ia.internal

import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.ia.internal.mapper.TicketMapper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class IAServiceImpl : IAService {
    private val api: IAApi

    constructor(baseUrl: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(IAApi::class.java)
    }

    constructor(api: IAApi) {
        this.api = api
    }

    override suspend fun analyzeTicketImage(image: File): Ticket {
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), image)
        val body = MultipartBody.Part.createFormData("image", image.name, requestFile)
        val ticketDto = api.analyzeImage(body)
        return TicketMapper.toDomain(ticketDto)
    }

    override suspend fun analyzeTicketAudio(audio: File): Ticket {
        val requestFile = RequestBody.create("audio/*".toMediaTypeOrNull(), audio)
        val body = MultipartBody.Part.createFormData("audio", audio.name, requestFile)
        val ticketDto = api.analyzeAudio(body)
        return TicketMapper.toDomain(ticketDto)
    }

    override suspend fun analyzeTicketText(items: Map<String, Double>): Ticket {
        val ticketDto = api.analyzeText(items)
        return TicketMapper.toDomain(ticketDto)
    }
}
