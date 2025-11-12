package com.example.ticketscan.domain.repositories.icon

import com.example.ticketscan.domain.model.Icon
import java.util.UUID

interface IconRepository {
    suspend fun getAllIcons(): List<Icon>
    suspend fun getIconById(id: UUID): Icon?
}

