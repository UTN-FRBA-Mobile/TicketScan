package com.example.ticketscan.ia.internal.mock

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.ia.internal.IAApi
import com.example.ticketscan.ia.internal.dto.CategoryDto
import com.example.ticketscan.ia.internal.dto.StoreDto
import com.example.ticketscan.ia.internal.dto.TicketDto
import com.example.ticketscan.ia.internal.dto.TicketItemDto
import okhttp3.MultipartBody
import java.time.Instant
import java.util.UUID

class MockIAApi(
    private val repositoryViewModel: RepositoryViewModel
) : IAApi {
    override suspend fun analyzeImage(image: MultipartBody.Part): TicketDto {
        return getMockTicket("image")
    }

    override suspend fun analyzeAudio(audio: MultipartBody.Part): TicketDto {
        return getMockTicket("audio")
    }

    override suspend fun analyzeText(items: Map<String, Double>): TicketDto {
        return getMockTicket("text")
    }

    private suspend fun getMockTicket(source: String): TicketDto {
        val categories = repositoryViewModel.getAllCategories()
        
        val items = when (source) {
            "audio" -> listOf(
                TicketItemDto(
                    id = UUID.randomUUID().toString(),
                    name = "Jabón",
                    category = CategoryDto(
                        id = UUID.randomUUID().toString(),
                        name = "Hogar",
                        color = colorToHex(categories.find { it.name == "Hogar" }?.color ?: Color.Gray)
                    ),
                    quantity = 1,
                    isIntUnit = true,
                    price = 200.0
                ),
                TicketItemDto(
                    id = UUID.randomUUID().toString(),
                    name = "Shampoo",
                    category = CategoryDto(
                        id = UUID.randomUUID().toString(),
                        name = "Hogar",
                        color = colorToHex(categories.find { it.name == "Hogar" }?.color ?: Color.Gray)
                    ),
                    quantity = 1,
                    isIntUnit = true,
                    price = 500.0
                )
            )
            else -> listOf(
                TicketItemDto(
                    id = UUID.randomUUID().toString(),
                    name = "Pan",
                    category = CategoryDto(
                        id = UUID.randomUUID().toString(),
                        name = "Alimentación",
                        color = colorToHex(categories.find { it.name == "Alimentación" }?.color ?: Color.Gray)
                    ),
                    quantity = 1,
                    isIntUnit = true,
                    price = 100.0
                ),
                TicketItemDto(
                    id = UUID.randomUUID().toString(),
                    name = "Heladera",
                    category = CategoryDto(
                        id = UUID.randomUUID().toString(),
                        name = "Hogar",
                        color = colorToHex(categories.find { it.name == "Hogar" }?.color ?: Color.Gray)
                    ),
                    quantity = 1,
                    isIntUnit = true,
                    price = 20000.0
                )
            )
        }
        
        return TicketDto(
            id = UUID.randomUUID().toString(),
            date = Instant.now().toString(),
            store = StoreDto(
                id = UUID.randomUUID().toString(),
                name = "Supermercado Mock",
                cuit = 30123456789L,
                location = "Av. Mock 123"
            ),
            origin = "MEDIA",
            items = items,
            total = items.sumOf { it.price }
        )
    }
    
    private fun colorToHex(color: Color): String {
        val argb = color.toArgb()
        return String.format("#%08X", argb)
    }
}