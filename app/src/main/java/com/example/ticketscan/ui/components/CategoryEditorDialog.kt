package com.example.ticketscan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.Icon
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun CategoryEditorDialog(
    category: Category?,
    icons: List<Icon>,
    onDismiss: () -> Unit,
    onSave: (name: String, icon: Icon, color: Color) -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var selectedIcon by remember { mutableStateOf<Icon?>(
        icons.find { it.id == category?.icon?.id } ?: icons.firstOrNull()
    ) }
    var selectedColor by remember { mutableStateOf(category?.color ?: Color.Gray) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showIconPicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = TicketScanTheme.colors.surface
        ) {
            Column(
                modifier = Modifier.padding(TicketScanTheme.spacing.lg)
            ) {
                // Title
                Text(
                    text = if (category == null) "Nueva Categoría" else "Editar Categoría",
                    style = TicketScanTheme.typography.titleLarge,
                    color = TicketScanTheme.colors.onSurface
                )

                Spacer(modifier = Modifier.height(TicketScanTheme.spacing.lg))

                // Name input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(TicketScanTheme.spacing.md))

                // Icon selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showIconPicker = true }
                        .border(
                            width = 1.dp,
                            color = TicketScanTheme.colors.outline,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(TicketScanTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = selectedIcon?.let { TicketScanIcons.categoryIcon(it.name) }
                            ?: TicketScanIcons.Category,
                        contentDescription = null,
                        tint = TicketScanTheme.colors.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(TicketScanTheme.spacing.md))
                    Text(
                        text = selectedIcon?.name ?: "Seleccionar ícono",
                        style = TicketScanTheme.typography.bodyLarge,
                        color = TicketScanTheme.colors.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = TicketScanIcons.Edit,
                        contentDescription = null,
                        tint = TicketScanTheme.colors.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(TicketScanTheme.spacing.md))

                // Color selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showColorPicker = true }
                        .border(
                            width = 1.dp,
                            color = TicketScanTheme.colors.outline,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(TicketScanTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(selectedColor, shape = CircleShape)
                            .border(1.dp, TicketScanTheme.colors.outline, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(TicketScanTheme.spacing.md))
                    Text(
                        text = "Seleccionar color",
                        style = TicketScanTheme.typography.bodyLarge,
                        color = TicketScanTheme.colors.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = TicketScanIcons.Edit,
                        contentDescription = null,
                        tint = TicketScanTheme.colors.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(TicketScanTheme.spacing.lg))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(TicketScanTheme.spacing.sm))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && selectedIcon != null) {
                                onSave(name, selectedIcon!!, selectedColor)
                            }
                        },
                        enabled = name.isNotBlank() && selectedIcon != null
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }

    // Icon Picker Dialog
    if (showIconPicker) {
        IconPickerDialog(
            icons = icons,
            selectedIcon = selectedIcon,
            onDismiss = { showIconPicker = false },
            onSelect = { icon ->
                selectedIcon = icon
                showIconPicker = false
            }
        )
    }

    // Color Picker Dialog
    if (showColorPicker) {
        ColorPickerDialog(
            currentColor = selectedColor,
            onDismiss = { showColorPicker = false },
            onSelect = { color ->
                selectedColor = color
                showColorPicker = false
            }
        )
    }
}

@Composable
private fun IconPickerDialog(
    icons: List<Icon>,
    selectedIcon: Icon?,
    onDismiss: () -> Unit,
    onSelect: (Icon) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = TicketScanTheme.colors.surface
        ) {
            Column(
                modifier = Modifier.padding(TicketScanTheme.spacing.lg)
            ) {
                Text(
                    text = "Seleccionar Ícono",
                    style = TicketScanTheme.typography.titleMedium,
                    color = TicketScanTheme.colors.onSurface
                )

                Spacer(modifier = Modifier.height(TicketScanTheme.spacing.md))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.sm),
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(icons) { icon ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .background(
                                    color = if (icon.id == selectedIcon?.id)
                                        TicketScanTheme.colors.primary.copy(alpha = 0.1f)
                                    else
                                        TicketScanTheme.colors.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = if (icon.id == selectedIcon?.id) 2.dp else 0.dp,
                                    color = TicketScanTheme.colors.primary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onSelect(icon) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = TicketScanIcons.categoryIcon(icon.name),
                                contentDescription = icon.name,
                                tint = if (icon.id == selectedIcon?.id)
                                    TicketScanTheme.colors.primary
                                else
                                    TicketScanTheme.colors.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorPickerDialog(
    currentColor: Color,
    onDismiss: () -> Unit,
    onSelect: (Color) -> Unit
) {
    val predefinedColors = listOf(
        Color(0xFFF44336), // Red
        Color(0xFFE91E63), // Pink
        Color(0xFF9C27B0), // Purple
        Color(0xFF673AB7), // Deep Purple
        Color(0xFF3F51B5), // Indigo
        Color(0xFF2196F3), // Blue
        Color(0xFF03A9F4), // Light Blue
        Color(0xFF00BCD4), // Cyan
        Color(0xFF009688), // Teal
        Color(0xFF4CAF50), // Green
        Color(0xFF8BC34A), // Light Green
        Color(0xFFCDDC39), // Lime
        Color(0xFFFFEB3B), // Yellow
        Color(0xFFFFC107), // Amber
        Color(0xFFFF9800), // Orange
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF795548), // Brown
        Color(0xFF9E9E9E), // Grey
        Color(0xFF607D8B), // Blue Grey
        Color.Gray
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = TicketScanTheme.colors.surface
        ) {
            Column(
                modifier = Modifier.padding(TicketScanTheme.spacing.lg)
            ) {
                Text(
                    text = "Seleccionar Color",
                    style = TicketScanTheme.typography.titleMedium,
                    color = TicketScanTheme.colors.onSurface
                )

                Spacer(modifier = Modifier.height(TicketScanTheme.spacing.md))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.sm)
                ) {
                    items(predefinedColors) { color ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .background(color, shape = CircleShape)
                                .border(
                                    width = if (color == currentColor) 3.dp else 1.dp,
                                    color = if (color == currentColor)
                                        TicketScanTheme.colors.primary
                                    else
                                        TicketScanTheme.colors.outline,
                                    shape = CircleShape
                                )
                                .clickable { onSelect(color) }
                        )
                    }
                }
            }
        }
    }
}

