package com.example.ticketscan.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.text.SimpleDateFormat

@Composable
fun UploadCard(
    ticket: Ticket,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = ticket.name().take(20),
                style = TicketScanTheme.typography.titleLarge,
                color = TicketScanTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fecha: ${SimpleDateFormat("yyyy/MM/dd").format(ticket.date)}",
                    style = TicketScanTheme.typography.bodyMedium,
                    color = TicketScanTheme.colors.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Total: ${ticket.total}",
                    style = TicketScanTheme.typography.bodyMedium,
                    color = TicketScanTheme.colors.onBackground
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ver más",
                style = TicketScanTheme.typography.bodyMedium,
                color = TicketScanTheme.colors.primary,
            )
        }
    }
}
