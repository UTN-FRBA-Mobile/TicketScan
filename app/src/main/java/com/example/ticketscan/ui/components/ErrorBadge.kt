package com.example.ticketscan.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun ErrorBadge(
    message: String,
    modifier: Modifier = Modifier,
) {
    AssistChip(
        onClick = {},
        modifier = modifier,
        label = { Text(text = message) },
        leadingIcon = {
            androidx.compose.material3.Icon(
                imageVector = Icons.Filled.ErrorOutline,
                contentDescription = null
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = TicketScanTheme.colors.errorContainer,
            labelColor = TicketScanTheme.colors.onErrorContainer,
            leadingIconContentColor = TicketScanTheme.colors.onErrorContainer
        ),
        border = null
    )
}
