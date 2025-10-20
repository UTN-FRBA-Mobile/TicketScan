package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    val (containerColor, contentColor, iconTint) = when {
        isNeutral -> Triple(
            TicketScanTheme.colors.surfaceVariant,
            TicketScanTheme.colors.onSurfaceVariant,
            TicketScanTheme.colors.onSurfaceVariant
        )
        isPositive -> Triple(
            TicketScanTheme.colors.errorContainer,
            TicketScanTheme.colors.error,
            TicketScanTheme.colors.error
        )
        else -> Triple(
            TicketScanTheme.colors.successContainer,
            TicketScanTheme.colors.onSuccessContainer,
            TicketScanTheme.colors.success
        )
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
        TicketScanCard(
            style = TicketScanCardStyle.Tonal,
            containerColor = containerColor,
            contentColor = contentColor,
            contentPadding = PaddingValues(TicketScanTheme.spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = iconTint)
                }
                val formattedPercentage = percentageChange.abs().multiply(BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toPlainString()
                Text(
                    text = if (isNeutral) "Sin cambios" else "$formattedPercentage%",
                    style = TicketScanTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = if (icon != null) TicketScanTheme.spacing.sm else TicketScanTheme.spacing.none),
                    color = contentColor
                )
            }
        }
    }
}