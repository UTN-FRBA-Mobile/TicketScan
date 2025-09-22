package com.example.ticketscan.domain

data class Ticket(
    val id: Long = 0,
    val creationDate: Long, // Fecha en formato timestamp
    val items: List<AnalizedItem> = emptyList()
)
