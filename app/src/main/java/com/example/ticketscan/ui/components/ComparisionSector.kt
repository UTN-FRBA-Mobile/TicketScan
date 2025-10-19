package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.theme.TicketScanTheme
import com.example.ticketscan.ui.theme.TicketScanIcons
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun ComparisonSection(current: BigDecimal, previous: BigDecimal) {
    val percentageChange = if (previous.compareTo(BigDecimal.ZERO) != 0) {
        current.subtract(previous).divide(previous, 4, RoundingMode.HALF_UP)
    } else if (current.compareTo(BigDecimal.ZERO) != 0) {
        BigDecimal.ONE
    } else {
        BigDecimal.ZERO
    }

    val isPositive = percentageChange.compareTo(BigDecimal.ZERO) > 0
    val isNeutral = percentageChange.compareTo(BigDecimal.ZERO) == 0

    val backgroundColor = when {
        isNeutral -> TicketScanTheme.colors.surfaceVariant
        isPositive -> Color(0xFFFBE9E7) // A light red color
        else -> Color(0xFFE8F5E9) // A light green color
    }

    val contentColor = when {
        isNeutral -> TicketScanTheme.colors.onSurfaceVariant
        isPositive -> Color(0xFFD32F2F) // A dark red color
        else -> Color(0xFF388E3C) // A dark green color
    }

    val icon = when {
        isNeutral -> null
        isPositive -> TicketScanIcons.ArrowUpward
        else -> TicketScanIcons.ArrowDownward
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = contentColor)
                }
                val formattedPercentage = percentageChange.abs().multiply(BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toPlainString()
                Text(
                    text = if(isNeutral) "Sin cambios" else "$formattedPercentage%",
                    color = contentColor,
                    style = TicketScanTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = if (icon != null) 8.dp else 0.dp)
                )
            }
        }
    }
}