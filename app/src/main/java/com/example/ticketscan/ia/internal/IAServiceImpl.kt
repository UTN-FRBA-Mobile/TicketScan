package com.example.ticketscan.ia.internal

import com.example.ticketscan.domain.model.Category
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

    override suspend fun analyzeTicketImage(image: File, categories: List<Category>): Ticket {
        return try {
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), image)
            val body = MultipartBody.Part.createFormData("image", image.name, requestFile)
            val ticketDto = api.analyzeImage(body)
            TicketMapper.toDomain(ticketDto, categories)
        } catch (e: Exception) {
            throw handleException(e, "imagen")
        }
    }

    override suspend fun analyzeTicketAudio(audio: File, categories: List<Category>): Ticket {
        return try {
            val requestFile = RequestBody.create("audio/*".toMediaTypeOrNull(), audio)
            val body = MultipartBody.Part.createFormData("audio", audio.name, requestFile)
            val ticketDto = api.analyzeAudio(body)
            TicketMapper.toDomain(ticketDto, categories)
        } catch (e: Exception) {
            throw handleException(e, "audio")
        }
    }

    override suspend fun analyzeTicketText(items: Map<String, Double>, categories: List<Category>): Ticket {
        return try {
            val ticketDto = api.analyzeText(items)
            TicketMapper.toDomain(ticketDto, categories)
        } catch (e: Exception) {
            throw handleException(e, "texto")
        }
    }

    private fun handleException(e: Exception, source: String): Exception {
        return Exception("Ocurrió un error en la conexión con el servidor")
    }
}
