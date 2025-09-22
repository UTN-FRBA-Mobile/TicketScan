package com.example.ticketscan.domain.repositories

import androidx.compose.ui.graphics.Color
import com.example.ticketscan.ui.components.CategoryStat
import com.example.ticketscan.ui.components.Period
import java.math.BigDecimal

class StatsRepositoryMock : StatsRepository {
    override suspend fun getCategoryStats(period: Period): List<CategoryStat> {
        if (period == Period.MENSUAL)
            return listOf(
                CategoryStat("Comida", BigDecimal.valueOf(250), Color(0xFFEF5350)),
                CategoryStat("Transporte", BigDecimal.valueOf(120), Color(0xFF42A5F5)),
                CategoryStat("Entretenimiento", BigDecimal.valueOf(80), Color(0xFFAB47BC)),
                CategoryStat("Otros", BigDecimal.valueOf(50), Color(0xFF66BB6A))
            )
        return listOf(
            CategoryStat("Comida", BigDecimal.valueOf(100), Color(0xFFEF5350)),
            CategoryStat("Transporte", BigDecimal.valueOf(300), Color(0xFF42A5F5)),
            CategoryStat("Entretenimiento", BigDecimal.valueOf(580), Color(0xFFAB47BC)),
            CategoryStat("Otros", BigDecimal.valueOf(10), Color(0xFF66BB6A))
        )
    }
}