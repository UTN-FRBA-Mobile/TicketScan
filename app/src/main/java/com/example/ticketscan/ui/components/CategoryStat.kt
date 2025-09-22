package com.example.ticketscan.ui.components

import androidx.compose.ui.graphics.Color
import java.math.BigDecimal

data class CategoryStat(
    val name: String,
    val amount: BigDecimal,
    val color: Color
)