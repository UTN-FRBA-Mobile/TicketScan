package com.example.ticketscan.domain.model

import java.util.UUID

data class TicketItem(
    val id: UUID,
    val name: String,
    val category: String,
    val quantity: Int,
    val isIntUnit: Boolean,
    val price: Double,
)

