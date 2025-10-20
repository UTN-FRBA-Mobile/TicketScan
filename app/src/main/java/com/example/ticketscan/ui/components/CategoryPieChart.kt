package com.example.ticketscan.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ticketscan.domain.model.CategoryStat
import com.example.ticketscan.ui.theme.TicketScanTheme
import com.example.ticketscan.ui.theme.TicketScanIcons
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat

@Composable
fun CategoryPieChart(
    stats: List<CategoryStat>,
    totalAmount: BigDecimal,
    modifier: Modifier = Modifier,
    onCategoryClick: (String) -> Unit = {}
) {
    if (stats.isEmpty()) {
        Text("No hay datos disponibles", modifier = modifier)
        return
    }

    val totalFromStats: BigDecimal = stats.sumOf { it.amount }

    val proportions: List<BigDecimal> = if (totalFromStats > BigDecimal.ZERO) {
        stats.map { it.amount.divide(totalFromStats, 4, RoundingMode.HALF_UP) }
    } else {
        List(stats.size) { BigDecimal.ZERO }
    }

    val angles = proportions.map { it.multiply(BigDecimal(360)).toFloat() }
    val currencyFormat = NumberFormat.getCurrencyInstance().apply {
        maximumFractionDigits = 0
    }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
            Canvas(modifier = Modifier.size(200.dp)) {
                var startAngle = -90f
                for (i in angles.indices) {
                    drawArc(
                        color = stats[i].color,
                        startAngle = startAngle,
                        sweepAngle = angles[i],
                        useCenter = false,
                        style = Stroke(width = 60f, cap = StrokeCap.Butt)
                    )
                    startAngle += angles[i]
                }
            }
            Text(
                text = currencyFormat.format(totalAmount),
                style = TicketScanTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Leyenda
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            stats.forEach { stat ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, TicketScanTheme.colors.outline, RoundedCornerShape(8.dp))
                        .clickable { onCategoryClick(stat.name) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)

                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(stat.color, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = TicketScanIcons.categoryIcon(stat.name),
                        contentDescription = null,
                        tint = TicketScanTheme.colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    FormattedCurrencyText(
                        label = stat.name,
                        amount = stat.amount,
                        textStyle = TicketScanTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(TicketScanIcons.ArrowForwardIos, contentDescription = "Ver detalle", tint = TicketScanTheme.colors.onSurfaceVariant)
                }
            }
        }
    }
}