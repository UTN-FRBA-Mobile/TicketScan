package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.domain.viewmodel.RepositoryViewModelFactory
import com.example.ticketscan.ui.components.TicketScanCard
import com.example.ticketscan.ui.components.TicketScanCardStyle
import com.example.ticketscan.ui.components.TicketScanEmptyState
import com.example.ticketscan.ui.components.TicketScanScreenContainer
import com.example.ticketscan.ui.components.TicketScanSectionHeader
import com.example.ticketscan.ui.components.UploadCard
import com.example.ticketscan.ui.components.UploadOption
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import com.example.ticketscan.ui.theme.TicketScanThemeProvider
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    repositoryViewModel: RepositoryViewModel,
    modifier: Modifier = Modifier,
    ) {

    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repositoryViewModel))
    val tickets by viewModel.tickets.collectAsState()

    TicketScanScreenContainer(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.xl)
    ) {
        TicketScanCard(
            style = TicketScanCardStyle.Filled,
            containerColor = TicketScanTheme.colors.primary,
            contentColor = TicketScanTheme.colors.onPrimary,
            contentPadding = PaddingValues(
                vertical = TicketScanTheme.spacing.xl,
                horizontal = TicketScanTheme.spacing.lg
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Carga tu ticket",
                    style = TicketScanTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth(),
                    color = TicketScanTheme.colors.onPrimary,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        TicketScanTheme.spacing.lg,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UploadOption(
                        label = "Audio",
                        icon = TicketScanIcons.Audio,
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate("record_audio") }
                    UploadOption(
                        label = "Cámara",
                        icon = TicketScanIcons.Camera,
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate("scan") }
                    UploadOption(
                        label = "Texto",
                        icon = TicketScanIcons.Text,
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate("processing/texto") }
                }
            }
        }

        TicketScanSectionHeader(
            title = "Últimas cargas",
            subtitle = "Revisá tus comprobantes recientes"
        )

        if (tickets.isEmpty()) {
            TicketScanEmptyState(
                icon = TicketScanIcons.EmptyInbox,
                title = "Todavía no cargaste tickets",
                description = "Escaneá o subí un ticket para comenzar a ver tu historial.",
                actionLabel = "Cargar ticket",
                onActionClick = { navController.navigate("scan") }
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.md)
            ) {
                tickets.forEach { ticket ->
                    UploadCard(
                        title = ticket.store?.name ?: "Ticket ${ticket.id.toString().take(8)}",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.navigate("ticket/${ticket.id}") }
                    ) {
                        val formattedDate = ticket?.date?.let {
                            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it)
                        } ?: "Fecha sin definir"
                        Text(
                            text = "$formattedDate  •  Total: $${ticket.total}",
                            style = TicketScanTheme.typography.bodyMedium,
                            color = TicketScanTheme.colors.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    TicketScanThemeProvider {
        val navController = rememberNavController()
        val repositoryViewModelFactory = RepositoryViewModelFactory(context = LocalContext.current)
        val repositoryViewModel: RepositoryViewModel = viewModel(factory = repositoryViewModelFactory)
        HomeScreen(navController = navController, repositoryViewModel)
    }
}
