package com.example.ticketscan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.Icon
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.util.UUID

@Composable
fun CategoriesManagementModal(
    categories: List<Category>,
    icons: List<Icon>,
    onDismiss: () -> Unit,
    onToggleActive: (UUID, Boolean) -> Unit,
    onEditCategory: (Category) -> Unit,
    onAddCategory: (name: String, icon: Icon, color: Color) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            color = TicketScanTheme.colors.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(TicketScanTheme.spacing.lg)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gestión de Categorías",
                        style = TicketScanTheme.typography.titleLarge,
                        color = TicketScanTheme.colors.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = TicketScanIcons.Close,
                            contentDescription = "Cerrar",
                            tint = TicketScanTheme.colors.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(TicketScanTheme.spacing.md))

                // Categories List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.xs)
                ) {
                    items(categories) { category ->
                        CategoryManagementItem(
                            category = category,
                            icon = icons.find { it.id == category.icon.id },
                            onToggleActive = { onToggleActive(category.id, !category.isActive) },
                            onEdit = { editingCategory = category }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(TicketScanTheme.spacing.md))

                // Add Button
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = TicketScanIcons.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(TicketScanTheme.spacing.sm))
                    Text("Agregar Categoría")
                }
            }
        }
    }

    // Add Category Dialog
    if (showAddDialog) {
        CategoryEditorDialog(
            category = null,
            icons = icons,
            onDismiss = { showAddDialog = false },
            onSave = { name: String, icon: Icon, color: Color ->
                onAddCategory(name, icon, color)
                showAddDialog = false
            }
        )
    }

    // Edit Category Dialog
    editingCategory?.let { category ->
        CategoryEditorDialog(
            category = category,
            icons = icons,
            onDismiss = { editingCategory = null },
            onSave = { name: String, icon: Icon, color: Color ->
                onEditCategory(category.copy(name = name, icon = icon, color = color))
                editingCategory = null
            }
        )
    }
}

@Composable
private fun CategoryManagementItem(
    category: Category,
    icon: Icon?,
    onToggleActive: () -> Unit,
    onEdit: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        shape = RoundedCornerShape(8.dp),
        color = TicketScanTheme.colors.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = TicketScanTheme.spacing.md,
                    vertical = TicketScanTheme.spacing.xs
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color indicator
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(category.color, shape = RoundedCornerShape(50))
            )

            Spacer(modifier = Modifier.width(TicketScanTheme.spacing.sm))

            // Icon - Usando el icono mapeado por nombre
            Icon(
                imageVector = icon?.let { TicketScanIcons.categoryIcon(it.name) }
                    ?: TicketScanIcons.Category,
                contentDescription = null,
                tint = TicketScanTheme.colors.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(TicketScanTheme.spacing.sm))

            // Category name
            Text(
                text = category.name,
                style = TicketScanTheme.typography.bodyMedium,
                color = if (category.isActive)
                    TicketScanTheme.colors.onSurface
                else
                    TicketScanTheme.colors.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.weight(1f)
            )

            // Active/Inactive indicator
            Text(
                text = if (category.isActive) "Activa" else "Inactiva",
                style = TicketScanTheme.typography.bodySmall,
                color = TicketScanTheme.colors.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(TicketScanTheme.spacing.sm))

            // Toggle Switch
            Switch(
                checked = category.isActive,
                onCheckedChange = { onToggleActive() }
            )
        }
    }
}

