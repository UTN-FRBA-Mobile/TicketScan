package com.example.ticketscan.domain.repositories.ticketitem

import com.example.ticketscan.domain.model.TicketItem
import java.util.UUID

interface TicketItemRepository {
    suspend fun getItemsByTicketId(ticketId: UUID): List<TicketItem>
    suspend fun insertItem(item: TicketItem): Boolean
    suspend fun updateItem(item: TicketItem): Boolean
    suspend fun deleteItem(id: UUID): Boolean
}

