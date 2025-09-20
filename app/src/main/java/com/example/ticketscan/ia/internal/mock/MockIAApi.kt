package com.example.ticketscan.ia.internal.mock

import com.example.ticketscan.ia.AnalizedItem
import com.example.ticketscan.ia.internal.IAApi
import okhttp3.MultipartBody

class MockIAApi : IAApi {
    override suspend fun analizeImage(image: MultipartBody.Part): List<AnalizedItem> {
        return listOf(
            AnalizedItem("Pan", 100.0, "Alimentos"),
            AnalizedItem("Leche", 200.0, "Lácteos")
        )
    }

    override suspend fun analizeAudio(audio: MultipartBody.Part): List<AnalizedItem> {
        return listOf(
            AnalizedItem("AudioPan", 120.0, "Alimentos"),
            AnalizedItem("AudioLeche", 220.0, "Lácteos")
        )
    }

    override suspend fun analizeItems(items: Map<String, Double>): List<AnalizedItem> {
        return items.map { (nombre, precio) ->
            AnalizedItem(nombre, precio, "MockCategoria")
        }
    }
}