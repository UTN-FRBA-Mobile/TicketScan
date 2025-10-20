package com.example.ticketscan.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.domain.viewmodel.RepositoryViewModelFactory
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import com.example.ticketscan.ui.theme.TicketScanThemeProvider
import java.util.UUID
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.ticketscan.domain.model.Ticket

@Suppress("UNUSED_PARAMETER")
@Composable
fun TicketScreen(
    navController: NavController,
    viewModel: TicketViewModel
) {
    val ticket by viewModel.ticket.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var creatingItem by remember { mutableStateOf<TicketItem?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = if (isEditing) "Editar Ticket" else "Ticket",
                style = TicketScanTheme.typography.headlineLarge,
                color = TicketScanTheme.colors.onBackground
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = ticket?.date?.let {
                    java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(it)
                } ?: "Fecha xx/xx/xxxx HH:mm",
                style = TicketScanTheme.typography.bodyLarge,
                color = TicketScanTheme.colors.onBackground
            )
        }

        Spacer(Modifier.height(20.dp))

        val itemsByCategory = ticket?.items?.groupBy { it.category } ?: emptyMap()
        itemsByCategory.forEach { (category, items) ->
            CategorySection(
                category = category,
                items = items,
                isEditable = isEditing,
                categories = categories,
                onItemChange = { id, name, price, quantity, categoryValue ->
                    viewModel.updateTicketItem(
                        itemId = id,
                        name = name,
                        price = price,
                        quantity = quantity,
                        category = categoryValue
                    )
                },
                onItemDelete = { id -> viewModel.removeTicketItem(id) }
            )
            Spacer(Modifier.height(12.dp))
        }

        // Total general del ticket (suma de price * quantity sobre todos los items)
        val ticketTotal = ticket?.items?.sumOf { it.price * it.quantity } ?: 0.0
        Text(
            text = "Total ticket: $${"%.2f".format(ticketTotal)}",
            style = TicketScanTheme.typography.headlineSmall,
            color = TicketScanTheme.colors.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            textAlign = TextAlign.End
        )

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (isEditing) {
                IconButton(onClick = {
                    viewModel.refreshTicket()
                    isEditing = false
                }) {
                    Icon(
                        imageVector = TicketScanIcons.Close,
                        contentDescription = "Cancelar edición",
                        tint = TicketScanTheme.colors.onBackground,
                        modifier = Modifier.size(40.dp)
                    )
                }

                IconButton(onClick = {
                    val defaultCategory = categories.firstOrNull() ?: Category.default()
                    creatingItem = TicketItem(
                        id = UUID.randomUUID(),
                        name = "",
                        category = defaultCategory,
                        quantity = 0,
                        isIntUnit = true,
                        price = 0.0
                    )
                }) {
                    Icon(
                        imageVector = TicketScanIcons.Add,
                        contentDescription = "Crear artículo",
                        tint = TicketScanTheme.colors.onBackground,
                        modifier = Modifier.size(40.dp)
                    )
                }

                IconButton(onClick = {
                    viewModel.saveTicket()
                    isEditing = false
                }) {
                    Icon(
                        imageVector = TicketScanIcons.Save,
                        contentDescription = "Guardar",
                        tint = TicketScanTheme.colors.onBackground,
                        modifier = Modifier.size(40.dp)
                    )
                }
            } else {
                IconButton(onClick = {
                    viewModel.deleteTicket {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = false }
                        }
                    }
                }) {
                    Icon(
                        imageVector = TicketScanIcons.Delete,
                        contentDescription = "Eliminar ticket",
                        tint = TicketScanTheme.colors.onBackground,
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Botón para exportar ticket a PDF (junto a Eliminar y Editar)
                IconButton(onClick = {
                    exportTicketToPdf(context, ticket)
                }) {
                    Icon(
                        imageVector = TicketScanIcons.Share,
                        contentDescription = "Exportar ticket",
                        tint = TicketScanTheme.colors.onBackground,
                        modifier = Modifier.size(40.dp)
                    )
                }

                IconButton(onClick = {
                    isEditing = true
                }) {
                    Icon(
                        imageVector = TicketScanIcons.Edit,
                        contentDescription = "Editar",
                        tint = TicketScanTheme.colors.onBackground,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        if (creatingItem != null) {
            EditItemDialog(
                initial = creatingItem!!,
                categories = categories,
                onDismiss = { creatingItem = null },
                onSave = { name, price, quantity, categoryValue ->
                    val item = TicketItem(
                        id = creatingItem!!.id,
                        name = name,
                        category = categoryValue,
                        quantity = quantity ?: 0,
                        isIntUnit = true,
                        price = price ?: 0.0
                    )
                    viewModel.addTicketItem(item)
                    creatingItem = null
                },
                onDelete = null
            )
        }
    }
}

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
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 520.dp)
            .padding(horizontal = TicketScanTheme.spacing.xl),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        containerColor = TicketScanTheme.colors.surface,
        titleContentColor = TicketScanTheme.colors.onSurface,
        textContentColor = TicketScanTheme.colors.onSurfaceVariant,
        confirmButton = {
            TextButton(
                colors = ButtonDefaults.textButtonColors(
                    contentColor = TicketScanTheme.colors.primary
                ),
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
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = TicketScanTheme.colors.onSurfaceVariant)
            ) { Text("Cancelar") }
        },
        title = {
            Text(
                text = "Editar artículo",
                style = TicketScanTheme.typography.titleLarge,
                color = TicketScanTheme.colors.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.md)
            ) {
                val textFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TicketScanTheme.colors.primary,
                    unfocusedBorderColor = TicketScanTheme.colors.outline,
                    focusedLabelColor = TicketScanTheme.colors.primary,
                    cursorColor = TicketScanTheme.colors.primary,
                    focusedContainerColor = TicketScanTheme.colors.surfaceVariant,
                    unfocusedContainerColor = TicketScanTheme.colors.surfaceVariant,
                    errorCursorColor = TicketScanTheme.colors.error,
                    errorBorderColor = TicketScanTheme.colors.error,
                    errorLabelColor = TicketScanTheme.colors.error
                )

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
                        leadingIcon = {
                            Icon(
                                imageVector = TicketScanIcons.categoryIcon(category.name),
                                contentDescription = null,
                                tint = TicketScanTheme.colors.onSurfaceVariant
                            )
                        },
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
                        modifier = Modifier.exposedDropdownSize(true)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = cat.name,
                                        style = TicketScanTheme.typography.bodyLarge,
                                        color = TicketScanTheme.colors.onSurface
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = TicketScanIcons.categoryIcon(cat.name),
                                        contentDescription = null,
                                        tint = TicketScanTheme.colors.primary
                                    )
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
                        colors = ButtonDefaults.textButtonColors(contentColor = TicketScanTheme.colors.error)
                    ) {
                        Text("Eliminar")
                    }

                    if (showConfirmDelete) {
                        AlertDialog(
                            onDismissRequest = { showConfirmDelete = false },
                            containerColor = TicketScanTheme.colors.surface,
                            titleContentColor = TicketScanTheme.colors.onSurface,
                            textContentColor = TicketScanTheme.colors.onSurfaceVariant,
                            title = { Text("Confirmar eliminación") },
                            text = { Text("¿Estás seguro de que quieres eliminar este artículo?") },
                            confirmButton = {
                                TextButton(
                                    colors = ButtonDefaults.textButtonColors(contentColor = TicketScanTheme.colors.error),
                                    onClick = {
                                        onDelete(initial.id)
                                        showConfirmDelete = false
                                        onDismiss()
                                    }
                                ) { Text("Eliminar") }
                            },
                            dismissButton = {
                                TextButton(
                                    colors = ButtonDefaults.textButtonColors(contentColor = TicketScanTheme.colors.onSurfaceVariant),
                                    onClick = { showConfirmDelete = false }
                                ) { Text("Cancelar") }
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun CategorySection(
    category: Category,
    items: List<TicketItem>,
    categories: List<Category>,
    isEditable: Boolean = false,
    onItemChange: (id: UUID, name: String?, price: Double?, quantity: Int?, category: Category?) -> Unit = { _, _, _, _, _ -> },
    onItemDelete: (id: UUID) -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = TicketScanTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.sm)
        ) {
            Icon(
                imageVector = TicketScanIcons.categoryIcon(category.name),
                contentDescription = null,
                tint = TicketScanTheme.colors.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = category.name,
                style = TicketScanTheme.typography.titleMedium,
                color = TicketScanTheme.colors.onBackground,
                modifier = Modifier.weight(1f)
            )
        }
        var editingItem by remember { mutableStateOf<TicketItem?>(null) }

        items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 14.dp)
                     .border(BorderStroke(1.dp, TicketScanTheme.colors.primary), TicketScanTheme.shapes.small),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${item.name} (${item.quantity} u.) - $${item.price}",
                    style = TicketScanTheme.typography.bodyLarge,
                    color = TicketScanTheme.colors.onBackground,
                    modifier = Modifier.padding(PaddingValues(horizontal = 18.dp, vertical = 12.dp))
                )
                if (isEditable) {
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = { editingItem = item },
                        modifier = Modifier.padding(0.dp).size(36.dp),

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

        if (editingItem != null) {
            EditItemDialog(
                initial = editingItem!!,
                categories = categories,
                onDismiss = { editingItem = null },
                onSave = { name, price, quantity, categoryValue ->
                    onItemChange(editingItem!!.id, name, price, quantity, categoryValue)
                    editingItem = null
                },
                onDelete = { id ->
                    onItemDelete(id)
                    editingItem = null
                }
            )
        }

        Spacer(Modifier.height(8.dp))
        val categoryTotal = items.sumOf { it.price * it.quantity }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = TicketScanTheme.spacing.xs),
            horizontalArrangement = Arrangement.End
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.xs)
            ) {
                Icon(
                    imageVector = TicketScanIcons.categoryIcon(category.name),
                    contentDescription = null,
                    tint = TicketScanTheme.colors.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Total ${category.name}: $${"%.2f".format(categoryTotal)}",
                    style = TicketScanTheme.typography.bodyLarge,
                    color = TicketScanTheme.colors.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicketScreenPreview() {
    TicketScanThemeProvider {
        val navController = rememberNavController()
        val repositoryViewModelFactory = RepositoryViewModelFactory(context = LocalContext.current)
        val repositoryViewModel: RepositoryViewModel = viewModel(factory = repositoryViewModelFactory)
        val factory = remember { TicketViewModelFactory(repositoryViewModel, UUID.randomUUID()) }
        val viewModel: TicketViewModel = viewModel(factory = factory)
        TicketScreen(navController = navController, viewModel)
    }
}

// Función auxiliar para exportar un ticket sencillo a PDF y guardarlo en filesDir.
private fun exportTicketToPdf(context: Context, ticket: Ticket?) {
    if (ticket == null) {
        Toast.makeText(context, "No hay ticket para exportar", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 tamaño en pts
        val page = doc.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 12f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        }

        var y = 40f
        val left = 20f
        val df = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        canvas.drawText("Ticket: ${ticket.id}", left, y, paint)
        y += 20f
        canvas.drawText("Fecha: ${df.format(ticket.date)}", left, y, paint)
        y += 24f

        // Items
        ticket.items.forEach { item ->
            val line = "${item.name} (${item.quantity} u.) - $${item.price}"
            canvas.drawText(line, left, y, paint)
            y += 18f
        }

        y += 8f
        canvas.drawText("Total: $${ticket.total}", left, y, paint)

        doc.finishPage(page)

        val filename = "ticket_${ticket.id}.pdf"
        val file = File(context.filesDir, filename)
        FileOutputStream(file).use { out ->
            doc.writeTo(out)
        }
        doc.close()

        Toast.makeText(context, "Ticket exportado: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error exportando PDF: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
