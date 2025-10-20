package com.example.ticketscan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ticketscan.ui.theme.TicketScanTheme

/**
 * Shared dialog scaffold that wraps content in a themed [TicketScanCard]
 * so dialogs follow the same shapes, colors and spacing across the app.
 */
@Composable
fun TicketScanDialog(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: (@Composable () -> Unit)? = null,
    style: TicketScanCardStyle = TicketScanCardStyle.Filled,
    headerColor: Color = TicketScanTheme.colors.onSurface,
    actions: @Composable RowScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        TicketScanCard(
            modifier = modifier.widthIn(min = 280.dp, max = 420.dp),
            style = style,
            contentPadding = PaddingValues(
                horizontal = TicketScanTheme.spacing.lg,
                vertical = TicketScanTheme.spacing.lg
            )
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.lg)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.xs)
                ) {
                    icon?.let { composable ->
                        Box(
                            modifier = Modifier
                                .size(TicketScanTheme.spacing.xxl)
                                .clip(TicketScanTheme.shapes.medium)
                                .background(TicketScanTheme.colors.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CompositionLocalProvider(LocalContentColor provides TicketScanTheme.colors.primary) {
                                composable()
                            }
                        }
                    }
                    if (title.isNotBlank()) {
                        Text(
                            text = title,
                            style = TicketScanTheme.typography.titleLarge,
                            color = headerColor
                        )
                    }
                    subtitle?.takeIf { it.isNotBlank() }?.let { text ->
                        Text(
                            text = text,
                            style = TicketScanTheme.typography.bodyMedium,
                            color = TicketScanTheme.colors.onSurfaceVariant
                        )
                    }
                }

                content()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    actions()
                }
            }
        }
    }
}

/**
 * Small helper to keep consistent spacing between dialog actions while using [TicketScanDialog].
 */
@Composable
fun RowScope.TicketScanDialogActionSpacer() {
    Spacer(modifier = Modifier.width(TicketScanTheme.spacing.sm))
}
