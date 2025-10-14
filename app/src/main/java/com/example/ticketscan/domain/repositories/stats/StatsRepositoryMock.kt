package com.example.ticketscan.domain.repositories.stats

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.ticketscan.domain.model.CategoryStat
import com.example.ticketscan.ui.components.Period
import java.math.BigDecimal

class StatsRepositoryMock (
    private val context: Context
): StatsRepository {
    override suspend fun getCategoryStats(period: Period, periodOffset: Int): List<CategoryStat> {
        val factor = -periodOffset * 10L
        val monthlyStats = listOf(
            CategoryStat("Alimentación", BigDecimal.valueOf(350 + factor), Color(red = 0, green = 150, blue = 136, alpha = 255)),
            CategoryStat("Transporte", BigDecimal.valueOf(150 + factor), Color(red = 33, green = 150, blue = 243, alpha = 255)),
            CategoryStat("Entretenimiento", BigDecimal.valueOf(100 + factor), Color(0xFFFF9800)),
            CategoryStat("Salud", BigDecimal.valueOf(50 + factor), Color(0xFFF44336)),
            CategoryStat("Hogar", BigDecimal.valueOf(200 + factor), Color(0xFF9C27B0)),
            CategoryStat("Otros", BigDecimal.valueOf(75 + factor), Color.Gray)
        )

        val weeklyStats = listOf(
            CategoryStat("Alimentación", BigDecimal.valueOf(100 + factor), Color(red = 0, green = 150, blue = 136, alpha = 255)),
            CategoryStat("Transporte", BigDecimal.valueOf(50 + factor), Color(red = 33, green = 150, blue = 243, alpha = 255)),
            CategoryStat("Entretenimiento", BigDecimal.valueOf(25 + factor), Color(0xFFFF9800)),
            CategoryStat("Salud", BigDecimal.valueOf(10 + factor), Color(0xFFF44336)),
            CategoryStat("Hogar", BigDecimal.valueOf(70 + factor), Color(0xFF9C27B0)),
            CategoryStat("Otros", BigDecimal.valueOf(15 + factor), Color.Gray)
        )

        if (period == Period.MENSUAL)
            return monthlyStats
        return weeklyStats
    }

    override suspend fun getPeriodCategoryHistory(
        categoryName: String,
        period: Period,
        periodQuantity: Int
    ): List<PeriodExpense> {
        if (period == Period.MENSUAL) {
            return when (categoryName) {
            "Alimentación" -> listOf(
                PeriodExpense("Jul", BigDecimal.valueOf(350)),
                PeriodExpense("Ago", BigDecimal.valueOf(370)),
                PeriodExpense("Sep", BigDecimal.valueOf(360)),
                PeriodExpense("Oct", BigDecimal.valueOf(380)),
            )
            "Transporte" -> listOf(
                PeriodExpense("Jul", BigDecimal.valueOf(150)),
                PeriodExpense("Ago", BigDecimal.valueOf(140)),
                PeriodExpense("Sep", BigDecimal.valueOf(160)),
                PeriodExpense("Oct", BigDecimal.valueOf(155)),
            )
            "Salud" -> listOf(
                PeriodExpense("Jul", BigDecimal.valueOf(50)),
                PeriodExpense("Ago", BigDecimal.valueOf(45)),
                PeriodExpense("Sep", BigDecimal.valueOf(55)),
                PeriodExpense("Oct", BigDecimal.valueOf(60)),
            )
            "Hogar" -> listOf(
                PeriodExpense("Jul", BigDecimal.valueOf(200)),
                PeriodExpense("Ago", BigDecimal.valueOf(210)),
                PeriodExpense("Sep", BigDecimal.valueOf(190)),
                PeriodExpense("Oct", BigDecimal.valueOf(220)),
            )
            "Entretenimiento" -> listOf(
                PeriodExpense("Jul", BigDecimal.valueOf(100)),
                PeriodExpense("Ago", BigDecimal.valueOf(110)),
                PeriodExpense("Sep", BigDecimal.valueOf(90)),
                PeriodExpense("Oct", BigDecimal.valueOf(120)),
            )
            "Otros" -> listOf(
                PeriodExpense("Jul", BigDecimal.valueOf(75)),
                PeriodExpense("Ago", BigDecimal.valueOf(70)),
                PeriodExpense("Sep", BigDecimal.valueOf(80)),
                PeriodExpense("Oct", BigDecimal.valueOf(85)),
            )
            else -> emptyList()
        }
        } else {
            return when (categoryName) {
                "Alimentación" -> listOf(
                    PeriodExpense("S1", BigDecimal.valueOf(80)),
                    PeriodExpense("S2", BigDecimal.valueOf(90)),
                    PeriodExpense("S3", BigDecimal.valueOf(85)),
                    PeriodExpense("S4", BigDecimal.valueOf(95)),
                )
                "Transporte" -> listOf(
                    PeriodExpense("S1", BigDecimal.valueOf(30)),
                    PeriodExpense("S2", BigDecimal.valueOf(35)),
                    PeriodExpense("S3", BigDecimal.valueOf(40)),
                    PeriodExpense("S4", BigDecimal.valueOf(32)),
                )
                 "Salud" -> listOf(
                    PeriodExpense("S1", BigDecimal.valueOf(10)),
                    PeriodExpense("S2", BigDecimal.valueOf(15)),
                    PeriodExpense("S3", BigDecimal.valueOf(12)),
                    PeriodExpense("S4", BigDecimal.valueOf(18)),
                )
                "Hogar" -> listOf(
                    PeriodExpense("S1", BigDecimal.valueOf(50)),
                    PeriodExpense("S2", BigDecimal.valueOf(55)),
                    PeriodExpense("S3", BigDecimal.valueOf(48)),
                    PeriodExpense("S4", BigDecimal.valueOf(60)),
                )
                "Entretenimiento" -> listOf(
                    PeriodExpense("S1", BigDecimal.valueOf(20)),
                    PeriodExpense("S2", BigDecimal.valueOf(25)),
                    PeriodExpense("S3", BigDecimal.valueOf(22)),
                    PeriodExpense("S4", BigDecimal.valueOf(28)),
                )
                "Otros" -> listOf(
                    PeriodExpense("S1", BigDecimal.valueOf(15)),
                    PeriodExpense("S2", BigDecimal.valueOf(18)),
                    PeriodExpense("S3", BigDecimal.valueOf(12)),
                    PeriodExpense("S4", BigDecimal.valueOf(20)),
                )
                else -> emptyList()
            }
        }
    }
}
