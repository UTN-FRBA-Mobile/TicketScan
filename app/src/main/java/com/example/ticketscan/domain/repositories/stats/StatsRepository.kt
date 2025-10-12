package com.example.ticketscan.domain.repositories.stats

import com.example.ticketscan.domain.model.CategoryStat
import com.example.ticketscan.ui.components.Period
import java.math.BigDecimal

data class MonthlyExpense(
    val month: String,
    val amount: BigDecimal
)

interface StatsRepository {
    suspend fun getCategoryStats(period: Period, periodOffset: Int = 0): List<CategoryStat>
    suspend fun getMonthlyCategoryHistory(categoryName: String, periodQuantity: Int): List<MonthlyExpense>
}
