package com.example.ticketscan.domain.repositories.stats

import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.ui.components.CategoryStat
import com.example.ticketscan.ui.components.Period
import java.math.BigDecimal

data class MonthlyExpense(
    val month: String,
    val amount: BigDecimal
)

interface StatsRepository {
    suspend fun getCategoryStats(period: Period): List<CategoryStat>
    suspend fun getMonthlyCategoryHistory(categoryName: String): List<MonthlyExpense>
    suspend fun getTransactionsForCategory(categoryName: String, period: Period): List<Ticket>
}