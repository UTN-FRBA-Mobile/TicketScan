package com.example.ticketscan.ia.internal.mapper

import androidx.compose.ui.graphics.Color
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.Store
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.domain.model.TicketOrigin
import com.example.ticketscan.ia.internal.dto.CategoryDto
import com.example.ticketscan.ia.internal.dto.StoreDto
import com.example.ticketscan.ia.internal.dto.TicketDto
import com.example.ticketscan.ia.internal.dto.TicketItemDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object TicketMapper {
    
    fun toDomain(dto: TicketDto): Ticket {
        return Ticket(
            id = UUID.fromString(dto.id),
            date = parseDate(dto.date),
            store = dto.store?.let { toDomain(it) },
            origin = parseOrigin(dto.origin),
            items = dto.items.map { toDomain(it) },
            total = dto.total
        )
    }
    
    private fun toDomain(dto: StoreDto): Store {
        return Store(
            id = UUID.fromString(dto.id),
            name = dto.name,
            cuit = dto.cuit,
            location = dto.location
        )
    }
    
    private fun toDomain(dto: CategoryDto): Category {
        return Category(
            id = UUID.fromString(dto.id),
            name = dto.name,
            color = parseColor(dto.color)
        )
    }
    
    private fun toDomain(dto: TicketItemDto): TicketItem {
        return TicketItem(
            id = UUID.fromString(dto.id),
            name = dto.name,
            category = toDomain(dto.category),
            quantity = dto.quantity,
            isIntUnit = dto.isIntUnit,
            price = dto.price
        )
    }
    
    private fun parseDate(dateString: String): Date {
        return try {
            // Try ISO 8601 format first (e.g., "2024-01-15T10:30:00Z")
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            isoFormat.parse(dateString) ?: Date()
        } catch (e: Exception) {
            try {
                val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                isoFormat.parse(dateString) ?: Date()
            } catch (e: Exception) {
                Date()
            }
        }
    }
    
    private fun parseColor(colorString: String): Color {
        return try {
            val hex = colorString.removePrefix("#")
            val colorLong = hex.toLong(16)
            
            if (hex.length == 6) {
                // RGB format, add full alpha
                Color(0xFF000000 or colorLong)
            } else {
                // ARGB format
                Color(colorLong)
            }
        } catch (e: Exception) {
            Color.Gray
        }
    }
    
    private fun parseOrigin(origin: String): TicketOrigin {
        return try {
            TicketOrigin.valueOf(origin.uppercase())
        } catch (e: Exception) {
            TicketOrigin.MEDIA
        }
    }
}
