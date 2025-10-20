package com.example.ticketscan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemDialog(
    initial: TicketItem,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSave: (name: String, price: Double?, quantity: Int?, category: Category) -> Unit,
    onDelete: ((UUID) -> Unit)? = null
) {
    var name by remember { mutableStateOf(initial.name) }
    var qtyText by remember { mutableStateOf(initial.quantity.toString()) }
    var priceText by remember { mutableStateOf(initial.price.toString()) }
    var category by remember { mutableStateOf(initial.category) }
    var expanded by remember { mutableStateOf(false) }
    var showConfirmDelete by remember { mutableStateOf(false) }

    // Estados para rastrear si el usuario ha interactuado con cada campo
    var nameTouched by remember { mutableStateOf(false) }
    var qtyTouched by remember { mutableStateOf(false) }
    var priceTouched by remember { mutableStateOf(false) }
    var attemptedSave by remember { mutableStateOf(false) }

    // Validaciones
    val isNameValid = name.trim().isNotEmpty()
    val parsedQty = qtyText.toIntOrNull()
    val isQtyValid = parsedQty != null && parsedQty > 0
    val parsedPrice = priceText.toDoubleOrNull()
    val isPriceValid = parsedPrice != null && parsedPrice > 0.0
    val isFormValid = isNameValid && isQtyValid && isPriceValid

    // Mostrar errores solo si se intentó guardar o si el campo fue tocado
    val showNameError = !isNameValid && (nameTouched || attemptedSave)
    val showQtyError = !isQtyValid && (qtyTouched || attemptedSave)
    val showPriceError = !isPriceValid && (priceTouched || attemptedSave)

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
        errorSupportingTextColor = TicketScanTheme.colors.error,
        focusedSupportingTextColor = TicketScanTheme.colors.onSurfaceVariant,
        unfocusedSupportingTextColor = TicketScanTheme.colors.onSurfaceVariant
    )

    TicketScanDialog(
        onDismissRequest = onDismiss,
        title = if (initial.name.isBlank()) "Agregar artículo" else "Editar artículo",
        subtitle = "Actualiza la información del artículo del ticket.",
        actions = {
            val saveButtonColors = if (isFormValid) {
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
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = TicketScanTheme.colors.onSurfaceVariant)
            ) {
                Text("Cancelar")
            }
            TicketScanDialogActionSpacer()
            Button(
                onClick = {
                    attemptedSave = true
                    if (isFormValid) {
                        onSave(name.trim(), parsedPrice, parsedQty, category)
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
                onValueChange = {
                    name = it
                    nameTouched = true
                },
                label = { Text("Nombre") },
                isError = showNameError,
                supportingText = {
                    if (showNameError) {
                        Text(
                            text = "El nombre no puede estar vacío",
                            style = TicketScanTheme.typography.bodySmall,
                            color = TicketScanTheme.colors.error
                        )
                    }
                },
                textStyle = TicketScanTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
            OutlinedTextField(
                value = qtyText,
                onValueChange = {
                    if (it.all { c -> c.isDigit() }) qtyText = it
                    qtyTouched = true
                },
                label = { Text("Cantidad") },
                isError = showQtyError,
                supportingText = {
                    if (showQtyError) {
                        Text(
                            text = "La cantidad debe ser mayor a 0",
                            style = TicketScanTheme.typography.bodySmall,
                            color = TicketScanTheme.colors.error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                textStyle = TicketScanTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
            OutlinedTextField(
                value = priceText,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*"))) priceText = it
                    priceTouched = true
                },
                label = { Text("Precio") },
                isError = showPriceError,
                supportingText = {
                    if (showPriceError) {
                        Text(
                            text = "El precio debe ser mayor a 0",
                            style = TicketScanTheme.typography.bodySmall,
                            color = TicketScanTheme.colors.error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                textStyle = TicketScanTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = category.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    textStyle = TicketScanTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = textFieldColors
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .exposedDropdownSize(true)
                        .heightIn(max = 240.dp)
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.md)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(
                                                color = cat.color,
                                                shape = CircleShape
                                            )
                                    )
                                    Text(
                                        text = cat.name,
                                        style = TicketScanTheme.typography.bodyMedium,
                                        color = TicketScanTheme.colors.onSurface
                                    )
                                }
                            },
                            onClick = {
                                category = cat
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (onDelete != null) {
                TextButton(
                    onClick = { showConfirmDelete = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = TicketScanTheme.colors.error),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Eliminar")
                }

                if (showConfirmDelete) {
                    TicketScanDialog(
                        onDismissRequest = { showConfirmDelete = false },
                        title = "Eliminar artículo",
                        subtitle = "Esta acción no se puede deshacer.",
                        style = TicketScanCardStyle.Tonal,
                        actions = {
                            TextButton(
                                onClick = { showConfirmDelete = false },
                                colors = ButtonDefaults.textButtonColors(contentColor = TicketScanTheme.colors.onSurfaceVariant)
                            ) {
                                Text("Cancelar")
                            }
                            TicketScanDialogActionSpacer()
                            Button(
                                onClick = {
                                    onDelete(initial.id)
                                    showConfirmDelete = false
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TicketScanTheme.colors.errorContainer,
                                    contentColor = TicketScanTheme.colors.onErrorContainer
                                ),
                                shape = TicketScanTheme.shapes.small
                            ) {
                                Text("Eliminar")
                            }
                        }
                    ) {
                        Text(
                            text = "¿Estás seguro de que quieres eliminar este artículo?",
                            style = TicketScanTheme.typography.bodyMedium,
                            color = TicketScanTheme.colors.onSurface
                        )
                    }
                }
            }
        }
    }
}

