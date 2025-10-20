package com.example.ticketscan.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.ticketscan.domain.model.Store
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun EditStoreDialog(
    initial: Store?,
    onDismiss: () -> Unit,
    onSave: (name: String) -> Unit,
    onSearch: suspend (String) -> List<Store>
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var suggestions by remember { mutableStateOf<List<Store>>(emptyList()) }
    var showSuggestions by remember { mutableStateOf(false) }

    // Buscar sugerencias cuando el nombre cambia
    LaunchedEffect(name) {
        if (name.isNotBlank()) {
            suggestions = onSearch(name)
            showSuggestions = suggestions.isNotEmpty()
        } else {
            suggestions = emptyList()
            showSuggestions = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.trim().isNotEmpty()) {
                        onSave(name.trim())
                    }
                },
                enabled = name.trim().isNotEmpty()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        title = { Text("Seleccionar tienda") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la tienda") },
                    textStyle = TicketScanTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    singleLine = true
                )

                // Mostrar sugerencias
                if (showSuggestions && suggestions.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .heightIn(max = 200.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = TicketScanTheme.colors.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        LazyColumn {
                            items(suggestions) { store ->
                                Text(
                                    text = store.name,
                                    style = TicketScanTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            // Al hacer clic, guardar la tienda seleccionada
                                            onSave(store.name)
                                        }
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

