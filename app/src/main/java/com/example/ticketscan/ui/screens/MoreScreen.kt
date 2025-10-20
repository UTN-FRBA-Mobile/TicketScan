package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.ticketscan.ui.components.ProfileItem
import com.example.ticketscan.ui.components.ProfileSection
import com.example.ticketscan.ui.components.TicketScanScreenContainer
import com.example.ticketscan.ui.components.TicketScanSectionHeader
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme

@Suppress("UNUSED_PARAMETER")
@Composable
fun MoreScreen(
    navController: NavController,
    onTermsClick: () -> Unit = {},
    onAboutClick: () -> Unit = {}
) {
    TicketScanScreenContainer(
        verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.lg)
    ) {
        TicketScanSectionHeader(
            title = "Más opciones",
            subtitle = "Accedé a la información institucional"
        )

            ProfileSection(
                title = "Sobre TicketScan",
                items = listOf(
                    ProfileItem(
                        text = "Términos y Condiciones",
                        icon = TicketScanIcons.Description,
                        onClick = onTermsClick
                    ),
                    ProfileItem(
                        text = "Sobre nosotros",
                        icon = TicketScanIcons.Info,
                        onClick = onAboutClick
                    )
                )
            )
    }
}
