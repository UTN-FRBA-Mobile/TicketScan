package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ticketscan.ui.components.ProfileSection
import com.example.ticketscan.ui.components.ProfileHeader
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun ProfileScreen(
    navController: NavController,
    onNavigateToProfile: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        ProfileHeader(
            name = "Juan Pérez",
            subtitle = "Mi cuenta",
            onEditClick = { navController.navigate("edit_contact") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Gestión de datos section
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

        Spacer(modifier = Modifier.height(24.dp))

        // Accesibilidad section
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

        Spacer(modifier = Modifier.height(24.dp))

        // Sobre TicketScan section
        ProfileSection(
            title = "Sobre TicketScan",
            items = listOf(
                ProfileItem(
                    text = "Términos y Condiciones",
                    icon = TicketScanIcons.Description,
                    onClick = onNavigateToProfile //TODO: hay que definir que colocar
                ),
                ProfileItem(
                    text = "Sobre nosotros",
                    icon = TicketScanIcons.Info,
                    onClick = onNavigateToProfile //TODO: hay que definir que colocar
                )
            )
        )
    }
}

data class ProfileItem(
    val text: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
)
