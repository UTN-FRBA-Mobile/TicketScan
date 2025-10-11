package com.example.ticketscan.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ticketscan.R
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.model.TicketOrigin
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.ui.theme.TicketScanTheme
import kotlinx.coroutines.delay
import java.util.Date
import java.util.UUID

@Composable
fun ProcessingScreen(
    navController: NavController,
    repository: RepositoryViewModel,
    mode: TicketOrigin) {
    LaunchedEffect(mode) {
        delay(1200)
        val ticket = Ticket(
            id = UUID.randomUUID(),
            date = Date(),
            items = listOf(),
            total = 0.0,
            store = null,
            origin = TicketOrigin.TEXT
        )
        repository.insertTicket(ticket, onResult = {
            navController.navigate("ticket/${ticket.id}") {
                popUpTo("home") { inclusive = false }
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_carrito),
            contentDescription = "Carrito",
            modifier = Modifier.size(96.dp),
            colorFilter = ColorFilter.tint(TicketScanTheme.colors.primary)
        )
        Text(text = "Procesando $mode", modifier = Modifier.padding(top = 12.dp))
        CircularProgressIndicator(
            modifier = Modifier
                .padding(top = 24.dp)
                .size(48.dp),
            color = TicketScanTheme.colors.primary
        )
    }
}

//@RequiresApi(Build.VERSION_CODES.O)
//@Preview
//@Composable
//fun ProcessingScreenPreview() {
//    TicketScanThemeProvider {
//        val navController = rememberNavController()
//        val categoryRepository = CategoryRepositorySQLite(this@MainActivity)
//        val ticketItemRepository = TicketItemRepositorySQLite(this@MainActivity, categoryRepository)
//        val storeRepository = StoreRepositorySQLite(this@MainActivity)
//        val repositoryViewModel = RepositoryViewModel(
//            storeRepo = storeRepository,
//            categoryRepo = categoryRepository,
//            ticketRepo = TicketRepositorySQLite(this@, storeRepository, ticketItemRepository),
//            ticketItemRepo = ticketItemRepository,
//            statsRepo = StatsRepositorySQLite(this@MainActivity)
//        )
//
//        ProcessingScreen(navController = navController, repository = repositoryViewModel, mode = TicketOrigin.TEXT)
//    }
//}