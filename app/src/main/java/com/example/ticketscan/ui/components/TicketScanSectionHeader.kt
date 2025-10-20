package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.theme.TicketScanTheme

/**
 * Shared section header used across screens to keep typography/spacing unified.
 */
@Composable
fun TicketScanSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f, fill = true)) {
            Spacer(modifier = Modifier.height(TicketScanTheme.spacing.xs))
            androidx.compose.material3.Text(
                text = title,
                style = TicketScanTheme.typography.titleLarge,
                color = TicketScanTheme.colors.onBackground
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(TicketScanTheme.spacing.xs))
                androidx.compose.material3.Text(
                    text = subtitle,
                    style = TicketScanTheme.typography.bodyMedium,
                    color = TicketScanTheme.colors.onSurfaceVariant
                )
            }
        }
        if (trailing != null) {
            Spacer(modifier = Modifier.width(TicketScanTheme.spacing.md))
            trailing()
        }
    }
}
