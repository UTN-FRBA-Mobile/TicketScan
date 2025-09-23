package com.example.ticketscan.domain.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

data class Category(
    val id: UUID,
    val name: String,
    val color: Color
)

