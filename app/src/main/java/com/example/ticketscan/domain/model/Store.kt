package com.example.ticketscan.domain.model

import java.util.UUID

data class Store(
    val id: UUID,
    val name: String,
    val cuit: Long,
    val location: String,
)
