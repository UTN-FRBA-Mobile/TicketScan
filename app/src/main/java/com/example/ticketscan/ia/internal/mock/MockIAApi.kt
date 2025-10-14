package com.example.ticketscan.ia.internal.mock

import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.ia.internal.IAApi
import okhttp3.MultipartBody
import java.util.UUID

class MockIAApi(
    private val repositoryViewModel: RepositoryViewModel
) : IAApi {
    override suspend fun analyzeImage(image: MultipartBody.Part): List<TicketItem> {
        return getMockItems()
    }

    override suspend fun analyzeAudio(audio: MultipartBody.Part): List<TicketItem> {
        return getMockItems()
    }

    override suspend fun analyzeItems(items: Map<String, Double>): List<TicketItem> {
        return getMockItems()
    }

    private suspend fun getMockItems(): List<TicketItem> {
        val categories = repositoryViewModel.getAllCategories()
        return listOf(
            TicketItem(
                id = UUID.randomUUID(),
                name = "Pan",
                category = categories.find { it.name == "Alimentación" }!!,
                quantity = 1,
                isIntUnit = true,
                price = 100.0
            ),
            TicketItem(
                id = UUID.randomUUID(),
                name = "Heladera",
                category = categories.find { it.name == "Hogar" }!!,
                quantity = 1,
                isIntUnit = true,
                price = 20000.0
            )
        )
    }
}