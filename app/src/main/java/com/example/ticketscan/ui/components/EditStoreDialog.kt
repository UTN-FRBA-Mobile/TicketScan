package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ticketscan.domain.model.Store
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun EditStoreDialog(
    initial: Store?,
    onDismiss: () -> Unit,
    onSave: (name: String, cuit: Long, location: String) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var cuitText by remember { mutableStateOf(initial?.cuit?.toString() ?: "") }
    var location by remember { mutableStateOf(initial?.location ?: "") }

    // Estados para rastrear si el usuario ha interactuado con cada campo
    var nameTouched by remember { mutableStateOf(false) }
    var cuitTouched by remember { mutableStateOf(false) }
    var locationTouched by remember { mutableStateOf(false) }
    var attemptedSave by remember { mutableStateOf(false) }

    // Validaciones
    val isNameValid = name.trim().isNotEmpty()
    val parsedCuit = cuitText.toLongOrNull()
    val isCuitValid = parsedCuit != null && cuitText.length == 11
    val isLocationValid = location.trim().isNotEmpty()
    val isFormValid = isNameValid && isCuitValid && isLocationValid

    // Mostrar errores solo si se intentó guardar o si el campo fue tocado
    val showNameError = !isNameValid && (nameTouched || attemptedSave)
    val showCuitError = !isCuitValid && (cuitTouched || attemptedSave)
    val showLocationError = !isLocationValid && (locationTouched || attemptedSave)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    attemptedSave = true
                    if (isFormValid) {
                        onSave(name.trim(), parsedCuit, location.trim())
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        title = { Text(if (initial == null) "Agregar tienda" else "Editar tienda") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameTouched = true
                    },
                    label = { Text("Nombre de la tienda") },
                    isError = showNameError,
                    supportingText = {
                        if (showNameError) {
                            Text("El nombre no puede estar vacío", color = TicketScanTheme.colors.error)
                        } else {
                            Text(" ")
                        }
                    },
                    textStyle = TicketScanTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = cuitText,
                    onValueChange = {
                        if (it.isEmpty() || (it.all { c -> c.isDigit() } && it.length <= 11)) {
                            cuitText = it
                            cuitTouched = true
                        }
                    },
                    label = { Text("CUIT (11 dígitos)") },
                    isError = showCuitError,
                    supportingText = {
                        if (showCuitError) {
                            Text("El CUIT debe tener 11 dígitos", color = TicketScanTheme.colors.error)
                        } else {
                            Text(" ")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    textStyle = TicketScanTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = {
                        location = it
                        locationTouched = true
                    },
                    label = { Text("Ubicación") },
                    isError = showLocationError,
                    supportingText = {
                        if (showLocationError) {
                            Text("La ubicación no puede estar vacía", color = TicketScanTheme.colors.error)
                        } else {
                            Text(" ")
                        }
                    },
                    textStyle = TicketScanTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }
        }
    )
}

