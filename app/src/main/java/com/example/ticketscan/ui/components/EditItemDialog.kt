package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    attemptedSave = true
                    if (isFormValid) {
                        onSave(name.trim(), parsedPrice, parsedQty, category)
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        title = { Text("Editar artículo") },
        text = {
            Column {
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
                            Text("El nombre no puede estar vacío", color = TicketScanTheme.colors.error)
                        }
                    },
                    textStyle = TicketScanTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                )
                Spacer(Modifier.height(12.dp))
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
                            Text("La cantidad debe ser mayor a 0", color = TicketScanTheme.colors.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    textStyle = TicketScanTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                )
                Spacer(Modifier.height(12.dp))
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
                            Text("El precio debe ser mayor a 0", color = TicketScanTheme.colors.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    textStyle = TicketScanTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                )
                Spacer(Modifier.height(12.dp))
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
                        modifier = Modifier.fillMaxWidth().menuAnchor().height(64.dp)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.exposedDropdownSize(true)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    category = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (onDelete != null) {
                    Spacer(Modifier.height(12.dp))
                    TextButton(
                        onClick = { showConfirmDelete = true },
                    ) {
                        Text("Eliminar", color = TicketScanTheme.colors.error)
                    }

                    if (showConfirmDelete) {
                        AlertDialog(
                            onDismissRequest = { showConfirmDelete = false },
                            title = { Text("Confirmar eliminación") },
                            text = { Text("¿Estás seguro de que quieres eliminar este artículo?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    onDelete(initial.id)
                                    showConfirmDelete = false
                                    onDismiss()
                                }) { Text("Eliminar", color = TicketScanTheme.colors.error) }
                            },
                            dismissButton = {
                                TextButton(onClick = { showConfirmDelete = false }) { Text("Cancelar") }
                            }
                        )
                    }
                }
            }
        }
    )
}
