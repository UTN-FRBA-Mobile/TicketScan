package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ticketscan.ui.components.ProfileSection

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
        Text(
            text = "Perfil",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Gestión de datos section
        ProfileSection(
            title = "Gestión de datos",
            items = listOf(
                ProfileItem(
                    text = "Editar contacto",
                    icon = Icons.Default.Edit,
                    onClick = { navController.navigate("edit_contact") }
                ),
                ProfileItem(
                    text = "Notificaciones",
                    icon = Icons.Default.Notifications,
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
                    icon = Icons.Default.Category,
                    onClick = { navController.navigate("appearance_settings") }
                ),
                ProfileItem(
                    text = "Definir entrada por defecto del ticket",
                    icon = Icons.Default.SettingsInputComponent,
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
                    icon = Icons.Default.Description,
                    onClick = onNavigateToProfile //TODO: hay que definir que colocar
                ),
                ProfileItem(
                    text = "Sobre nosotros",
                    icon = Icons.Default.Info,
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

