package com.example.ticketscan.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    val total = stats.sumOf { it.amount.toDouble() }.toFloat()
    val proportions = stats.map { it.amount / total }
    val angles = proportions.map { 360 * it }

    Row(modifier = modifier.padding(16.dp)) {
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

        Spacer(modifier = Modifier.width(16.dp))

        // Leyenda
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.align(Alignment.CenterVertically)
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${stat.name}: %.2f".format(stat.amount))
                }
            }
        }
    }
}