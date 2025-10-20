package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketscan.domain.model.Store
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun TicketHeader(
    modifier: Modifier = Modifier,
    title: String,
    date: Date,
    store: Store? = null,
    isEditing: Boolean = false,
    onEditDateClick: () -> Unit = {},
    onEditStoreClick: () -> Unit = {},
) {
    Column(modifier = modifier) {
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

        Spacer(Modifier.height(8.dp))

        // Fecha con icono de edición
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fecha: ${SimpleDateFormat("yyyy/MM/dd").format(date)}",
                style = TicketScanTheme.typography.bodyLarge,
                color = TicketScanTheme.colors.onBackground
            )
            if (isEditing) {
                IconButton(
                    onClick = onEditDateClick,
                    modifier = Modifier
                        .padding(0.dp)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = TicketScanIcons.Edit,
                        contentDescription = "Editar fecha",
                        tint = TicketScanTheme.colors.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Tienda con icono de edición (solo mostrar si existe o si está en modo edición)
        if (store != null || isEditing) {
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tienda: " + (store?.name ?: "?"),
                    style = TicketScanTheme.typography.bodyLarge,
                    color = if (store != null) TicketScanTheme.colors.onBackground else TicketScanTheme.colors.onBackground.copy(alpha = 0.6f)
                )
                if (isEditing) {
                    IconButton(
                        onClick = onEditStoreClick,
                        modifier = Modifier
                            .padding(0.dp)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = TicketScanIcons.Edit,
                            contentDescription = "Editar tienda",
                            tint = TicketScanTheme.colors.onBackground,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

