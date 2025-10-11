package com.example.ticketscan.domain.model

import java.util.Date
import java.util.UUID

data class Ticket(
    val id: UUID,
    val date: Date,
    val store: Store?,
    val origin: TicketOrigin = TicketOrigin.MEDIA,
    val items: List<TicketItem>,
    val total: Double
)