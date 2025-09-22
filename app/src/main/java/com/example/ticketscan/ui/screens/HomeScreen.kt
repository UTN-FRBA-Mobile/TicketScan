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
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import android.app.Application
import com.example.ticketscan.ui.TicketViewModel
import com.example.ticketscan.ui.components.UploadCard
import com.example.ticketscan.ui.components.UploadOption
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val ticketViewModel: TicketViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val appContext = context.applicationContext
                if (appContext is Application) {
                    return TicketViewModel(appContext) as T
                } else {
                    throw IllegalStateException("El contexto no es Application")
                }
            }
        }
    )
    val tickets = ticketViewModel.tickets.collectAsState()
    val errorState = remember { mutableStateOf<String?>(null) }
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
                UploadOption(label = "Texto", icon = TicketScanIcons.Text) { /* TODO */ }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                try {
                    ticketViewModel.consultarTodosLosTickets()
                    errorState.value = null
                } catch (e: Exception) {
                    errorState.value = e.message ?: "Error desconocido"
                }
            }) {
                Text("Listar todos los tickets e ítems guardados")
            }
            Spacer(modifier = Modifier.height(16.dp))
            errorState.value?.let {
                Text("Error: $it", color = androidx.compose.ui.graphics.Color.Red)
            }
            if (tickets.value.isNotEmpty()) {
                Text("Tickets guardados:", style = TicketScanTheme.typography.headlineSmall)
                tickets.value.forEach { ticket ->
                    Text("Ticket #${ticket.id} - Fecha: ${ticket.creationDate}")
                    ticket.items.forEach { item ->
                        Text("- ${item.name}: ${item.price} (${item.category})")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Últimas cargas",
                style = TicketScanTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 16.dp),
                color = TicketScanTheme.colors.onBackground
            )
            // TODO: Add filter dropdown here
            Column(
                modifier = Modifier
                .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UploadCard(title = "Por audio", modifier = Modifier.fillMaxWidth()) {}
                UploadCard(title = "Por texto", modifier = Modifier.fillMaxWidth()) {}
                UploadCard(title = "Por fotos", modifier = Modifier.fillMaxWidth()) {}
            }
        }
    }
}
