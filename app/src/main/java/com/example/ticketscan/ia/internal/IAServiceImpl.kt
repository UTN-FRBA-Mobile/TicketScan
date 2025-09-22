package com.example.ticketscan.ia.internal

import android.content.Context
import com.example.ticketscan.data.SQLiteService
import com.example.ticketscan.domain.AnalizedItem
import com.example.ticketscan.domain.Ticket
import com.example.ticketscan.ia.IAService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class IAServiceImpl(private val context: Context, baseUrl: String) : IAService {
    private var api: IAApi
    private val db: SQLiteService = SQLiteService(context)

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(IAApi::class.java)
    }

    constructor(context: Context, api: IAApi) : this(context, "") {
        this.api = api
    }

    override suspend fun analizeTicketImage(image: File): Ticket {
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), image)
        val body = MultipartBody.Part.createFormData("image", image.name, requestFile)
        val items = api.analizeImage(body)
        val creationDate = System.currentTimeMillis()
        val ticketId = db.insertTicket(creationDate)
        items.forEach {
            db.insertAnalizedItem(it.copy(ticketId = ticketId))
        }
        return db.getTicketWithItems(ticketId)!!
    }

    override suspend fun analizeTicketAudio(audio: File): Ticket {
        val requestFile = RequestBody.create("audio/*".toMediaTypeOrNull(), audio)
        val body = MultipartBody.Part.createFormData("audio", audio.name, requestFile)
        val items = api.analizeAudio(body)
        val creationDate = System.currentTimeMillis()
        val ticketId = db.insertTicket(creationDate)
        items.forEach {
            db.insertAnalizedItem(it.copy(ticketId = ticketId))
        }
        return db.getTicketWithItems(ticketId)!!
    }

    override suspend fun analizeTicketItems(items: Map<String, Double>): Ticket {
        val analizedItems = api.analizeItems(items)
        val creationDate = System.currentTimeMillis()
        val ticketId = db.insertTicket(creationDate)
        analizedItems.forEach {
            db.insertAnalizedItem(it.copy(ticketId = ticketId))
        }
        return db.getTicketWithItems(ticketId)!!
    }
}
