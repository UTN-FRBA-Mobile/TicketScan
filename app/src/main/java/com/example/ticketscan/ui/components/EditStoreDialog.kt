package com.example.ticketscan.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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

    val isInputValid = name.trim().isNotEmpty()
    val saveButtonColors = if (isInputValid) {
        ButtonDefaults.buttonColors(
            containerColor = TicketScanTheme.colors.primary,
            contentColor = TicketScanTheme.colors.onPrimary
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = TicketScanTheme.colors.primary.copy(alpha = 0.4f),
            contentColor = TicketScanTheme.colors.onPrimary.copy(alpha = 0.6f)
        )
    }
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = TicketScanTheme.colors.surfaceVariant,
        unfocusedContainerColor = TicketScanTheme.colors.surfaceVariant,
        disabledContainerColor = TicketScanTheme.colors.surfaceVariant,
        focusedBorderColor = TicketScanTheme.colors.primary,
        unfocusedBorderColor = TicketScanTheme.colors.outline.copy(alpha = 0.6f),
        focusedLabelColor = TicketScanTheme.colors.primary,
        unfocusedLabelColor = TicketScanTheme.colors.onSurfaceVariant,
        cursorColor = TicketScanTheme.colors.primary,
        errorCursorColor = TicketScanTheme.colors.error,
        errorBorderColor = TicketScanTheme.colors.error,
        errorContainerColor = TicketScanTheme.colors.errorContainer,
        errorLabelColor = TicketScanTheme.colors.error,
        focusedSupportingTextColor = TicketScanTheme.colors.onSurfaceVariant,
        unfocusedSupportingTextColor = TicketScanTheme.colors.onSurfaceVariant
    )

    TicketScanDialog(
        onDismissRequest = onDismiss,
        title = "Seleccionar tienda",
        subtitle = "Busca y selecciona una tienda existente o crea una nueva.",
        actions = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = TicketScanTheme.colors.onSurfaceVariant)
            ) {
                Text("Cancelar")
            }
            TicketScanDialogActionSpacer()
            Button(
                onClick = {
                    if (isInputValid) {
                        onSave(name.trim())
                    }
                },
                colors = saveButtonColors,
                shape = TicketScanTheme.shapes.small
            ) {
                Text("Guardar")
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.md)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la tienda") },
                textStyle = TicketScanTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = true,
                colors = textFieldColors
            )

            if (showSuggestions && suggestions.isNotEmpty()) {
                TicketScanCard(
                    style = TicketScanCardStyle.Outline,
                    contentPadding = PaddingValues(vertical = TicketScanTheme.spacing.xs)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                    ) {
                        itemsIndexed(suggestions) { index, store ->
                            Text(
                                text = store.name,
                                style = TicketScanTheme.typography.bodyLarge,
                                color = TicketScanTheme.colors.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSave(store.name)
                                        onDismiss()
                                    }
                                    .padding(
                                        horizontal = TicketScanTheme.spacing.lg,
                                        vertical = TicketScanTheme.spacing.md
                                    )
                            )
                            if (index < suggestions.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = TicketScanTheme.colors.outline.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

