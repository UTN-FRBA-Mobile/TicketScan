package com.example.ticketscan.domain.model

import java.time.LocalDate
import java.util.UUID

data class Ticket(
    val id: UUID,
    val date: LocalDate,
    val origin: TicketOrigin = TicketOrigin.MEDIA,
    val store: Store?,
    val items: List<TicketItem>,
    val total: Double
)