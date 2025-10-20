package com.example.ticketscan.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.theme.TicketScanTheme

enum class TicketScanCardStyle {
    Filled,
    Tonal,
    Outline
}

/**
 * Reusable card wrapper enforcing TicketScan shapes, colors and padding.
 */
@Composable
fun TicketScanCard(
    modifier: Modifier = Modifier,
    style: TicketScanCardStyle = TicketScanCardStyle.Filled,
    onClick: (() -> Unit)? = null,
    containerColor: Color? = null,
    contentColor: Color? = null,
    contentPadding: PaddingValues = PaddingValues(TicketScanTheme.spacing.lg),
    content: @Composable ColumnScope.() -> Unit
) {
    val resolvedContainerColor = containerColor ?: when (style) {
        TicketScanCardStyle.Filled -> TicketScanTheme.colors.surface
        TicketScanCardStyle.Tonal -> TicketScanTheme.colors.surfaceVariant
        TicketScanCardStyle.Outline -> TicketScanTheme.colors.surface
    }

    val resolvedContentColor = contentColor ?: when (style) {
        TicketScanCardStyle.Filled -> TicketScanTheme.colors.onSurface
        TicketScanCardStyle.Tonal -> TicketScanTheme.colors.onSurface
        TicketScanCardStyle.Outline -> TicketScanTheme.colors.onSurface
    }

    val borderStroke: BorderStroke? = when (style) {
        TicketScanCardStyle.Outline -> BorderStroke(1.dp, TicketScanTheme.colors.outline.copy(alpha = 0.5f))
        else -> null
    }

    val cardColors = CardDefaults.cardColors(
        containerColor = resolvedContainerColor,
        contentColor = resolvedContentColor
    )

    if (onClick != null) {
        Card(
            modifier = modifier,
            onClick = onClick,
            shape = TicketScanTheme.shapes.medium,
            border = borderStroke,
            colors = cardColors
        ) {
            Column(modifier = Modifier.padding(contentPadding), content = content)
        }
    } else {
        Card(
            modifier = modifier,
            shape = TicketScanTheme.shapes.medium,
            border = borderStroke,
            colors = cardColors
        ) {
            Column(modifier = Modifier.padding(contentPadding), content = content)
        }
    }
}
