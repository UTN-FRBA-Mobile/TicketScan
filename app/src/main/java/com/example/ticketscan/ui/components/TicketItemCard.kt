package com.example.ticketscan.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun TicketItemCard(
    item: TicketItem,
    isEditable: Boolean,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
            .border(
                BorderStroke(1.dp, TicketScanTheme.colors.primary),
                TicketScanTheme.shapes.small
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "${item.name} (${item.quantity} u.) - Total: $${item.price}",
            style = TicketScanTheme.typography.bodyLarge,
            color = TicketScanTheme.colors.onBackground,
            modifier = Modifier.padding(PaddingValues(horizontal = 18.dp, vertical = 12.dp))
        )
        if (isEditable) {
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .padding(0.dp)
                    .size(36.dp),
            ) {
                Icon(
                    imageVector = TicketScanIcons.Edit,
                    contentDescription = "Editar",
                    tint = TicketScanTheme.colors.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

