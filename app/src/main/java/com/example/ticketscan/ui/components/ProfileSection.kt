package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun ProfileSection(
    title: String,
    items: List<ProfileItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.sm)
    ) {
        Text(
            text = title,
            style = TicketScanTheme.typography.titleMedium,
            color = TicketScanTheme.colors.onSurface,
            modifier = Modifier.padding(horizontal = TicketScanTheme.spacing.lg)
        )

        TicketScanCard(
            style = TicketScanCardStyle.Tonal,
            contentPadding = PaddingValues(vertical = TicketScanTheme.spacing.xs)
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    ProfileItemRow(
                        item = item,
                        showDivider = index < items.lastIndex
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileItemRow(
    item: ProfileItem,
    showDivider: Boolean
) {
    Column {
        TextButton(
            onClick = item.onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TicketScanTheme.spacing.xs),
            contentPadding = PaddingValues(
                horizontal = TicketScanTheme.spacing.md,
                vertical = TicketScanTheme.spacing.sm
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = TicketScanTheme.colors.onSurfaceVariant,
                    modifier = Modifier.size(TicketScanTheme.spacing.xl)
                )
                Spacer(modifier = Modifier.width(TicketScanTheme.spacing.md))
                Text(
                    text = item.text,
                    style = TicketScanTheme.typography.bodyLarge,
                    color = TicketScanTheme.colors.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = TicketScanIcons.ChevronRight,
                    contentDescription = null,
                    tint = TicketScanTheme.colors.onSurfaceVariant,
                    modifier = Modifier.size(TicketScanTheme.spacing.lg)
                )
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(
                    start = TicketScanTheme.spacing.xxl,
                    end = TicketScanTheme.spacing.md
                ),
                thickness = 0.5.dp,
                color = TicketScanTheme.colors.outline.copy(alpha = 0.5f)
            )
        }
    }
}
