package com.example.ticketscan.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.collections.map

@Composable
fun CategoryPieChart(
    stats: List<CategoryStat>,
    modifier: Modifier = Modifier
) {
    if (stats.isEmpty()) {
        Text("No hay datos disponibles", modifier = modifier)
        return
    }

    val total: BigDecimal = stats
        .map { it.amount }
        .reduceOrNull { acc, value -> acc + value }
        ?: BigDecimal.ZERO

    val proportions: List<BigDecimal> = if (total > BigDecimal.ZERO) {
        stats.map { it.amount.divide(total, 4, RoundingMode.HALF_UP) }
    } else {
        List(stats.size) { BigDecimal.ZERO }
    }

    val angles = proportions.map {
        it.multiply(BigDecimal(360)).toFloat()
    }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier
            .size(200.dp)
            .padding(16.dp)) {
            var startAngle = -90f

            for (i in angles.indices) {
                drawArc(
                    color = stats[i].color,
                    startAngle = startAngle,
                    sweepAngle = angles[i],
                    useCenter = true
                )
                startAngle += angles[i]
            }
        }

        Spacer(modifier = Modifier.width(30.dp))

        // Leyenda
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            stats.forEach { stat ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(stat.color, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    FormattedCurrencyText(
                        label = stat.name,
                        amount = stat.amount,
                        textStyle = TicketScanTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}