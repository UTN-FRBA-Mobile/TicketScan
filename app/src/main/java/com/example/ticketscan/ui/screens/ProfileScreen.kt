package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.Icon
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.ui.components.CategoriesManagementModal
import com.example.ticketscan.ui.components.ProfileHeader
import com.example.ticketscan.ui.components.ProfileItem
import com.example.ticketscan.ui.components.ProfileSection
import com.example.ticketscan.ui.components.TicketScanCard
import com.example.ticketscan.ui.components.TicketScanCardStyle
import com.example.ticketscan.ui.components.TicketScanScreenContainer
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.util.UUID

@Composable
fun ProfileScreen(
    navController: NavController,
    repositoryViewModel: RepositoryViewModel
) {
    val categories = remember { mutableStateListOf<Category>() }
    val icons = remember { mutableStateListOf<Icon>() }
    var reloadKey by remember { mutableStateOf(0) }
    var showCategoriesModal by remember { mutableStateOf(false) }

    LaunchedEffect(reloadKey) {
        val allCategories = repositoryViewModel.getAllCategories()
        val allIcons = repositoryViewModel.getAllIcons()
        categories.clear()
        categories.addAll(allCategories)
        icons.clear()
        icons.addAll(allIcons)
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
                ),
                ProfileItem(
                    text = "Gestionar categorías",
                    icon = TicketScanIcons.Category,
                    onClick = { showCategoriesModal = true }
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
    }

    // Categories Management Modal
    if (showCategoriesModal) {
        CategoriesManagementModal(
            categories = categories,
            icons = icons,
            onDismiss = {
                showCategoriesModal = false
                reloadKey++
            },
            onToggleActive = { id: UUID, isActive: Boolean ->
                repositoryViewModel.toggleCategoryActive(id, isActive) { success ->
                    if (success) reloadKey++
                }
            },
            onEditCategory = { category: Category ->
                repositoryViewModel.updateCategory(category) { success ->
                    if (success) reloadKey++
                }
            },
            onAddCategory = { name: String, icon: Icon, color: Color ->
                val newCategory = Category(
                    id = UUID.randomUUID(),
                    name = name,
                    color = color,
                    icon = icon,
                    isActive = true
                )
                repositoryViewModel.insertCategory(newCategory) { success ->
                    if (success) reloadKey++
                }
            }
        )
    }
}

