package com.example.ticketscan.ia.internal

import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.ia.internal.mapper.TicketMapper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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
        return try {
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), image)
            val body = MultipartBody.Part.createFormData("image", image.name, requestFile)
            val ticketDto = api.analyzeImage(body)
            TicketMapper.toDomain(ticketDto)
        } catch (e: Exception) {
            throw handleException(e, "imagen")
        }
    }

    override suspend fun analyzeTicketAudio(audio: File): Ticket {
        return try {
            val requestFile = RequestBody.create("audio/*".toMediaTypeOrNull(), audio)
            val body = MultipartBody.Part.createFormData("audio", audio.name, requestFile)
            val ticketDto = api.analyzeAudio(body)
            TicketMapper.toDomain(ticketDto)
        } catch (e: Exception) {
            throw handleException(e, "audio")
        }
    }

    override suspend fun analyzeTicketText(items: Map<String, Double>): Ticket {
        return try {
            val ticketDto = api.analyzeText(items)
            TicketMapper.toDomain(ticketDto)
        } catch (e: Exception) {
            throw handleException(e, "texto")
        }
    }

    private fun handleException(e: Exception, source: String): Exception {
        return when (e) {
            is UnknownHostException -> {
                Exception("No se pudo conectar al servidor. Verifica que el servidor mock esté ejecutándose.")
            }
            is SocketTimeoutException -> {
                Exception("El servidor tardó demasiado en responder. Verifica tu conexión o reinicia el servidor.")
            }
            is IOException -> {
                Exception("Error de conexión. Asegúrate de estar en la misma red que el servidor mock.")
            }
            is HttpException -> {
                when (e.code()) {
                    404 -> Exception("Endpoint no encontrado. Verifica la configuración del servidor.")
                    500 -> Exception("Error del servidor al procesar la $source.")
                    else -> Exception("Error del servidor (código ${e.code()})")
                }
            }
            else -> {
                Exception("Error inesperado al analizar la $source: ${e.message}")
            }
        }
    }
}
