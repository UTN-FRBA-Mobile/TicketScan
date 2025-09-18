package com.example.ticketscan.domain

import com.example.ticketscan.ui.components.CategoryStat
import com.example.ticketscan.ui.components.Period

interface StatsRepository {
    suspend fun getCategoryStats(period: Period): List<CategoryStat>
}