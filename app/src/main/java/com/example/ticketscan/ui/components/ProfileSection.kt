package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.screens.ProfileItem
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section title
        Text(
            text = title,
            style = TicketScanTheme.typography.titleLarge,
            color = TicketScanTheme.colors.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Section items
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = TicketScanTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = TicketScanTheme.colors.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
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
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = TicketScanTheme.colors.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
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
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 48.dp, end = 8.dp),
                thickness = 0.5.dp,
                color = TicketScanTheme.colors.outline.copy(alpha = 0.5f)
            )
        }
    }
}
