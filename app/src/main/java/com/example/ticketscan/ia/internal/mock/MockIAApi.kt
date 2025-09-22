package com.example.ticketscan.ia.internal.mock

import com.example.ticketscan.domain.AnalizedItem
import com.example.ticketscan.ia.internal.IAApi
import okhttp3.MultipartBody

class MockIAApi : IAApi {
    override suspend fun analizeImage(image: MultipartBody.Part): List<AnalizedItem> {
        return listOf(
            AnalizedItem(id = 0, name = "Pan", price = 100.0, category = "Alimentos", ticketId = 0),
            AnalizedItem(id = 0, name = "Leche", price = 200.0, category = "Lácteos", ticketId = 0)
        )
    }

    override suspend fun analizeAudio(audio: MultipartBody.Part): List<AnalizedItem> {
        return listOf(
            AnalizedItem(id = 0, name = "AudioPan", price = 120.0, category = "Alimentos", ticketId = 0),
            AnalizedItem(id = 0, name = "AudioLeche", price = 220.0, category = "Lácteos", ticketId = 0)
        )
    }

    override suspend fun analizeItems(items: Map<String, Double>): List<AnalizedItem> {
        return items.map { (nombre, precio) ->
            AnalizedItem(id = 0, name = nombre, price = precio, category = "MockCategoria", ticketId = 0)
        }
    }
}