package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.ticketscan.domain.model.ContactInfo
import com.example.ticketscan.ui.components.ProfileHeader
import com.example.ticketscan.ui.components.ProfileItem
import com.example.ticketscan.ui.components.ProfileSection
import com.example.ticketscan.ui.components.TicketScanCard
import com.example.ticketscan.ui.components.TicketScanCardStyle
import com.example.ticketscan.ui.components.TicketScanScreenContainer
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ContactInfoViewModel
) {
    val contactInfo by viewModel.contactInfo.collectAsState()

    ProfileContent(
        navController = navController,
        contactInfo = contactInfo
    )
}

@Composable
private fun ProfileContent(
    navController: NavController,
    contactInfo: ContactInfo
) {
    val displayName = contactInfo.fullName.ifBlank { "Juan Pérez" }
    val subtitle = contactInfo.email.ifBlank { "Mi cuenta" }

    TicketScanScreenContainer(
        verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.lg)
    ) {
        TicketScanCard(
            style = TicketScanCardStyle.Filled,
            contentPadding = PaddingValues(TicketScanTheme.spacing.lg)
        ) {
            ProfileHeader(
                name = displayName,
                subtitle = subtitle,
                onEditClick = { navController.navigate("edit_contact") }
            )
        }

        ProfileSection(
            title = "Gestión de datos",
            items = listOf(
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
    }
}
