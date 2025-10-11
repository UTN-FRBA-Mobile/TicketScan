package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.ui.components.UploadCard
import com.example.ticketscan.ui.components.UploadOption
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun HomeScreen(
    navController: NavController,
    repositoryViewModel: RepositoryViewModel,
    modifier: Modifier = Modifier,
    ) {

    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repositoryViewModel))
    val tickets by viewModel.tickets.collectAsState()

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = TicketScanTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                UploadOption(label = "Audio", icon = TicketScanIcons.Audio) { /* TODO */ }
                UploadOption(label = "Cámara", icon = TicketScanIcons.Camera) { /* TODO */ }
                UploadOption(label = "Texto", icon = TicketScanIcons.Text) { navController.navigate("processing/texto") }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Últimas cargas",
                style = TicketScanTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 16.dp),
            // TODO: Add filter dropdown here
            )
            Column(
                modifier = Modifier
                .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (tickets.isEmpty()) {
                    Text(
                        text = "No hay tickets cargados aún",
                        style = TicketScanTheme.typography.bodyMedium,
                        color = TicketScanTheme.colors.onBackground
                    )
                } else {
                    tickets.forEach { ticket ->
                        UploadCard(
                            title = ticket.store?.name ?: "Ticket ${ticket.id.toString().take(8)}",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navController.navigate("ticket/${ticket.id}") }
                        ) {
                            Text(
                                text = "Fecha: ${ticket.date}  •  Total: $${ticket.total}",
                                style = TicketScanTheme.typography.bodyMedium,
                                color = TicketScanTheme.colors.onBackground
                            )
                        }
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun HomePreview() {
//    TicketScanThemeProvider {
//        val navController = rememberNavController()
//        HomeScreen(navController = navController)
//    }
//}