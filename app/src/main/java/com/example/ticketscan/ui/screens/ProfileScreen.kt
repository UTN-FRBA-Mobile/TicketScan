package com.example.ticketscan.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.ui.components.ProfileHeader
import com.example.ticketscan.ui.components.ProfileItem
import com.example.ticketscan.ui.components.ProfileSection
import com.example.ticketscan.ui.components.TicketScanCard
import com.example.ticketscan.ui.components.TicketScanCardStyle
import com.example.ticketscan.ui.components.TicketScanScreenContainer
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.util.UUID
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment

@Composable
fun ProfileScreen(
    navController: NavController,
    repositoryViewModel: RepositoryViewModel
) {
    // categories state
    val categories = remember { mutableStateListOf<Category>() }
    val icons = remember { mutableStateListOf<com.example.ticketscan.domain.model.Icon>() }
    var reloadKey by remember { mutableStateOf(0) }

    // add form state
    var newName by remember { mutableStateOf("") }
    var iconExpanded by remember { mutableStateOf(false) }
    var selectedIcon by remember { mutableStateOf<com.example.ticketscan.domain.model.Icon?>(null) }

    LaunchedEffect(reloadKey) {
        val allCategories = repositoryViewModel.getAllCategories()
        val allIcons = repositoryViewModel.getAllIcons()
        categories.clear()
        categories.addAll(allCategories)
        icons.clear()
        icons.addAll(allIcons)
        if (selectedIcon == null && allIcons.isNotEmpty()) {
            selectedIcon = allIcons.first()
        }
    }

    TicketScanScreenContainer(
        verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.lg)
    ) {
        TicketScanCard(
            style = TicketScanCardStyle.Filled,
            contentPadding = PaddingValues(TicketScanTheme.spacing.lg)
        ) {
            ProfileHeader(
                name = "Juan Pérez",
                subtitle = "Mi cuenta",
                onEditClick = { navController.navigate("edit_contact") }
            )
        }

        ProfileSection(
            title = "Gestión de datos",
            items = listOf(
                ProfileItem(
                    text = "Editar contacto",
                    icon = TicketScanIcons.Edit,
                    onClick = { navController.navigate("edit_contact") }
                ),
                ProfileItem(
                    text = "Notificaciones",
                    icon = TicketScanIcons.Notifications,
                    onClick = { navController.navigate("notification_settings") }
                )
            )
        )

        ProfileSection(
            title = "Accesibilidad",
            items = listOf(
                ProfileItem(
                    text = "Editar apariencia",
                    icon = TicketScanIcons.Category,
                    onClick = { navController.navigate("appearance_settings") }
                ),
                ProfileItem(
                    text = "Definir entrada por defecto del ticket",
                    icon = TicketScanIcons.SettingsInputComponent,
                    onClick = { navController.navigate("default_ticket_entry") }
                )
            )
        )

        // Categories list + add form
        Column {
            Text(
                text = "Categorías",
                style = TicketScanTheme.typography.titleMedium,
                color = TicketScanTheme.colors.onSurface,
                modifier = Modifier.padding(horizontal = TicketScanTheme.spacing.lg)
            )

            TicketScanCard(
                style = TicketScanCardStyle.Tonal,
                contentPadding = PaddingValues(vertical = TicketScanTheme.spacing.xs)
            ) {
                Column {
                    categories.forEach { cat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* future edit */ }
                                .padding(horizontal = TicketScanTheme.spacing.md, vertical = TicketScanTheme.spacing.sm)
                        ) {
                            Icon(
                                imageVector = TicketScanIcons.categoryIcon(cat.name),
                                contentDescription = null,
                                tint = TicketScanTheme.colors.onSurfaceVariant,
                                modifier = Modifier.size(TicketScanTheme.spacing.xl)
                            )
                            Spacer(modifier = Modifier.width(TicketScanTheme.spacing.md))
                            Text(
                                text = cat.name,
                                style = TicketScanTheme.typography.bodyLarge,
                                color = TicketScanTheme.colors.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                repositoryViewModel.deleteCategory(cat.id) { success ->
                                    if (success) reloadKey++
                                }
                            }) {
                                Icon(
                                    imageVector = TicketScanIcons.Delete,
                                    contentDescription = null,
                                    tint = TicketScanTheme.colors.error,
                                    modifier = Modifier.size(TicketScanTheme.spacing.lg)
                                )
                            }
                        }
                    }

                    // add form
                    Column(modifier = Modifier.padding(horizontal = TicketScanTheme.spacing.md)) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("Nombre de la categoría") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = TicketScanTheme.spacing.sm),
                            horizontalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.md)
                        ) {
                            Row(modifier = Modifier
                                .clickable { iconExpanded = !iconExpanded }
                                .padding(8.dp)) {
                                Icon(
                                    imageVector = selectedIcon?.let { TicketScanIcons.categoryIcon(it.name) } ?: TicketScanIcons.categoryIcon("compr"),
                                    contentDescription = null,
                                    tint = TicketScanTheme.colors.onSurfaceVariant,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = selectedIcon?.name ?: "Selecciona un ícono", color = TicketScanTheme.colors.onSurface)
                            }

                            DropdownMenu(
                                expanded = iconExpanded,
                                onDismissRequest = { iconExpanded = false }
                            ) {
                                icons.forEach { icon ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = TicketScanIcons.categoryIcon(icon.name),
                                                    contentDescription = null,
                                                    tint = TicketScanTheme.colors.onSurfaceVariant,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(text = icon.name, color = TicketScanTheme.colors.onSurface)
                                            }
                                        },
                                        onClick = {
                                            selectedIcon = icon
                                            iconExpanded = false
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Button(onClick = {
                                val name = newName.trim()
                                if (name.isNotEmpty() && selectedIcon != null) {
                                    val color = when (selectedIcon!!.name) {
                                        "Alimentación" -> Color(0xFF009688)
                                        "Transporte" -> Color(0xFF2196F3)
                                        "Entretenimiento" -> Color(0xFFFF9800)
                                        "Salud" -> Color(0xFFF44336)
                                        "Hogar" -> Color(0xFF9C27B0)
                                        "Compras" -> Color(0xFFFFC107)
                                        else -> Color.Gray
                                    }
                                    val newCategory = Category(
                                        id = UUID.randomUUID(),
                                        name = name,
                                        color = color,
                                        icon = selectedIcon!!,
                                        isActive = true
                                    )
                                    repositoryViewModel.insertCategory(newCategory) { success ->
                                        if (success) {
                                            newName = ""
                                            reloadKey++
                                        }
                                    }
                                }
                            }) {
                                Text("Agregar")
                            }
                        }
                    }
                }
            }
        }
    }
}
