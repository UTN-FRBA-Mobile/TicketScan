package com.example.ticketscan.domain.repositories.ticketitem

import com.example.ticketscan.domain.model.TicketItem
import java.util.UUID
import android.database.sqlite.SQLiteDatabase

interface TicketItemRepository {
    suspend fun getItemsByTicketId(ticketId: UUID): List<TicketItem>
    suspend fun insertItem(item: TicketItem, ticketId: UUID, db: SQLiteDatabase? = null): Boolean
    suspend fun updateItem(item: TicketItem, db: SQLiteDatabase? = null): Boolean
    suspend fun deleteItem(id: UUID): Boolean
}
