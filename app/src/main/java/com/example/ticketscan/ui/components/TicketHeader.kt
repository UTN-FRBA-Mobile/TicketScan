package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun TicketHeader(
    title: String,
    date: Date?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = TicketScanTheme.typography.headlineLarge,
                color = TicketScanTheme.colors.onBackground
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = date?.let {
                    SimpleDateFormat("dd/MM/yyyy HH:mm").format(it)
                } ?: "Fecha xx/xx/xxxx HH:mm",
                style = TicketScanTheme.typography.bodyLarge,
                color = TicketScanTheme.colors.onBackground
            )
        }
    }
}

