package com.example.ticketscan.domain.model

import java.util.UUID

data class Icon(
    val id: UUID,
    val name: String,
    val iconKey: String
){
    companion object {
        fun default(): Icon {
            return Icon(UUID.randomUUID(), "Genérico", "shop")
        }
    }
}

