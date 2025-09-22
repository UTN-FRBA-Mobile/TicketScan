package com.example.ticketscan.domain

// Este archivo debe ser adaptado para SQLDelight o eliminado si solo usas los modelos generados por SQLDelight.

data class Ticket(
    val id: Long = 0,
    val creationDate: Long, // Fecha en formato timestamp
    val items: List<AnalizedItem> = emptyList()
)
