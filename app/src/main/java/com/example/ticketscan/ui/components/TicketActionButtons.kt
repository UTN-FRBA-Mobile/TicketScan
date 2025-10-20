package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun TicketActionButtons(
    isEditing: Boolean,
    onCancelEdit: () -> Unit,
    onAddItem: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (isEditing) {
            IconButton(onClick = onCancelEdit) {
                Icon(
                    imageVector = TicketScanIcons.Close,
                    contentDescription = "Cancelar edición",
                    tint = TicketScanTheme.colors.onBackground,
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(onClick = onAddItem) {
                Icon(
                    imageVector = TicketScanIcons.Add,
                    contentDescription = "Crear artículo",
                    tint = TicketScanTheme.colors.onBackground,
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(onClick = onSave) {
                Icon(
                    imageVector = TicketScanIcons.Save,
                    contentDescription = "Guardar",
                    tint = TicketScanTheme.colors.onBackground,
                    modifier = Modifier.size(40.dp)
                )
            }
        } else {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = TicketScanIcons.Delete,
                    contentDescription = "Eliminar ticket",
                    tint = TicketScanTheme.colors.onBackground,
                    modifier = Modifier.size(40.dp)
                )
            }

            // Botón para exportar ticket a PDF (junto a Eliminar y Editar)
            IconButton(onClick = {
                onShare()
            }) {
                Icon(
                    imageVector = TicketScanIcons.Share,
                    contentDescription = "Exportar ticket",
                    tint = TicketScanTheme.colors.onBackground,
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = TicketScanIcons.Edit,
                    contentDescription = "Editar",
                    tint = TicketScanTheme.colors.onBackground,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}
