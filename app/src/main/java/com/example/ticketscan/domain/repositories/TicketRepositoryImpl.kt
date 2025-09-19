package com.example.ticketscan.domain.repositories

import com.example.ticketscan.dto.CategoryDto
import com.example.ticketscan.dto.ItemDto
import com.example.ticketscan.dto.toDomain
import com.example.ticketscan.ui.screens.Category

class TicketRepositoryImpl : TicketRepository {
    override suspend fun createTicket(): List<Category> {
        val dtoList = listOf(
            CategoryDto(
                id = "1",
                title = "Alimentos",
                elements = listOf(
                    ItemDto("pan", "Pan", 7.0F),
                    ItemDto("leche", "Leche", 5.0F),
                    ItemDto("arroz", "Arroz", 3.0F)
                )
            ),
            CategoryDto(
                id = "2",
                title = "Limpieza",
                elements = listOf(
                    ItemDto("jabon", "Jabón", 2.0F),
                    ItemDto("detergente", "Detergente", 1.0F)
                )
            )
        )

        return dtoList.map { it.toDomain() }
    }
}