package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun UploadCard(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    TicketScanCard(
        modifier = modifier,
        style = TicketScanCardStyle.Outline,
        onClick = onClick,
        contentPadding = PaddingValues(TicketScanTheme.spacing.lg)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.sm)
        ) {
            Text(
                text = title,
                style = TicketScanTheme.typography.titleLarge,
                color = TicketScanTheme.colors.onSurface
            )
            Text(
                text = "Ver más",
                style = TicketScanTheme.typography.labelLarge,
                color = TicketScanTheme.colors.info
            )
            content()
        }
    }
}
