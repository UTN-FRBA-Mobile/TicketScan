package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ticketscan.ui.theme.TicketScanTheme

/**
 * Consistent empty state presentation with icon, title, message and optional action.
 */
@Composable
fun TicketScanEmptyState(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    TicketScanCard(
        modifier = modifier,
        style = TicketScanCardStyle.Tonal
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.sm)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TicketScanTheme.colors.primary
            )
            Text(
                text = title,
                style = TicketScanTheme.typography.titleMedium,
                color = TicketScanTheme.colors.onSurface
            )
            if (!description.isNullOrBlank()) {
                Text(
                    text = description,
                    style = TicketScanTheme.typography.bodyMedium,
                    color = TicketScanTheme.colors.onSurfaceVariant
                )
            }
            if (!actionLabel.isNullOrBlank() && onActionClick != null) {
                TicketScanButton(
                    onClick = onActionClick,
                    text = actionLabel,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
