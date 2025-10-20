package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun TicketScanButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = TicketScanTheme.colors.primary,
    contentColor: Color = TicketScanTheme.colors.onPrimary
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        shape = TicketScanTheme.shapes.medium,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        enabled = enabled,
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp),
        contentPadding = PaddingValues(
            vertical = TicketScanTheme.spacing.md,
            horizontal = TicketScanTheme.spacing.lg
        )
    ) {
        Text(
            text = text,
            style = TicketScanTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.ExtraBold
            )
        )
    }
}
