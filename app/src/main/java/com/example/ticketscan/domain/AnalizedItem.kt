package com.example.ticketscan.domain

// Este archivo debe ser adaptado para SQLDelight o eliminado si solo usas los modelos generados por SQLDelight.

data class AnalizedItem(
    val id: Long = 0,
    val name: String,
    val price: Double,
    val category: String,
    val ticketId: Long // Relación con Ticket
)