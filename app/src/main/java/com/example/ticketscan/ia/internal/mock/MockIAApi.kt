package com.example.ticketscan.ia.internal.mock

import androidx.compose.ui.graphics.Color
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.ia.internal.IAApi
import okhttp3.MultipartBody
import java.util.UUID

class MockIAApi : IAApi {
    override suspend fun analizeImage(image: MultipartBody.Part): List<TicketItem> {
        return listOf(
            TicketItem(
                id = UUID.randomUUID(),
                name = "Pan",
                category = Category(UUID.nameUUIDFromBytes("Alimentos".toByteArray()), "Alimentos", Color.Blue),
                quantity = 1,
                isIntUnit = true,
                price = 100.0
            ),
            TicketItem(
                id = UUID.randomUUID(),
                name = "Leche",
                category = Category(UUID.nameUUIDFromBytes("Lácteos".toByteArray()), "Lácteos", Color.Blue),
                quantity = 1,
                isIntUnit = true,
                price = 200.0
            )
        )
    }

    override suspend fun analizeAudio(audio: MultipartBody.Part): List<TicketItem> {
        return listOf(
            TicketItem(
                id = UUID.randomUUID(),
                name = "AudioPan",
                category = Category(UUID.nameUUIDFromBytes("Alimentos".toByteArray()), "Alimentos", Color.Blue),
                quantity = 1,
                isIntUnit = true,
                price = 120.0
            ),
            TicketItem(
                id = UUID.randomUUID(),
                name = "AudioLeche",
                category = Category(UUID.nameUUIDFromBytes("Lácteos".toByteArray()), "Lácteos", Color.Blue),
                quantity = 1,
                isIntUnit = true,
                price = 220.0
            )
        )
    }

    override suspend fun analizeItems(items: Map<String, Double>): List<TicketItem> {
        return items.map { (nombre, precio) ->
            TicketItem(
                id = UUID.randomUUID(),
                name = nombre,
                category = Category(UUID.nameUUIDFromBytes("MockCategoria".toByteArray()), "MockCategoria", Color.Blue),
                quantity = 1,
                isIntUnit = true,
                price = precio
            )
        }
    }
}