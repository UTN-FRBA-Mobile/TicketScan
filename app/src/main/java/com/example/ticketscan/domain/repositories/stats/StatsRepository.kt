package com.example.ticketscan.domain.repositories.stats

import com.example.ticketscan.domain.model.CategoryStat
import com.example.ticketscan.ui.components.Period
import java.math.BigDecimal

data class PeriodExpense(
    val periodName: String,
    val amount: BigDecimal
)

interface StatsRepository {
    suspend fun getCategoryStats(period: Period, periodOffset: Int = 0): List<CategoryStat>
    suspend fun getPeriodCategoryHistory(categoryName: String, period: Period, periodQuantity: Int): List<PeriodExpense>
}
