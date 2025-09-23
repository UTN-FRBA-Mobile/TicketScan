package com.example.ticketscan.ia.internal

import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.ia.internal.IAService
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

    override suspend fun analizeTicketImage(image: File): List<TicketItem> {
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), image)
        val body = MultipartBody.Part.createFormData("image", image.name, requestFile)
        return api.analizeImage(body)
    }

    override suspend fun analizeTicketAudio(audio: File): List<TicketItem> {
        val requestFile = RequestBody.create("audio/*".toMediaTypeOrNull(), audio)
        val body = MultipartBody.Part.createFormData("audio", audio.name, requestFile)
        return api.analizeAudio(body)
    }

    override suspend fun analizeTicketItems(items: Map<String, Double>): List<TicketItem> {
        return api.analizeItems(items)
    }
}
