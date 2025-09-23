package com.example.ticketscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.ticketscan.domain.repositories.TicketRepositoryMock
import com.example.ticketscan.ui.components.TicketScanBottomNavigation
import com.example.ticketscan.ui.screens.HomeScreen
import com.example.ticketscan.ui.screens.TicketScreen
import com.example.ticketscan.ui.screens.TicketViewModel
import com.example.ticketscan.ui.screens.TicketViewModelFactory
import com.example.ticketscan.ui.theme.TicketScanThemeProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ticketscan.domain.repositories.StatsRepositoryMock
import com.example.ticketscan.ia.internal.IAServiceImpl
import com.example.ticketscan.ui.screens.RecordAudioScreen
import com.example.ticketscan.ui.screens.StatsScreen
import com.example.ticketscan.ui.screens.StatsViewModel
import com.example.ticketscan.ui.screens.StatsViewModelFactory
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TicketScanThemeProvider {
                val navController = rememberNavController()
                val ticketFactory = remember { TicketViewModelFactory(TicketRepositoryMock()) }
                val ticketsViewModel: TicketViewModel = viewModel(factory = ticketFactory)
                val repository = StatsRepositoryMock()
                val statsFactory = StatsViewModelFactory(repository)
                val statsViewModel: StatsViewModel = viewModel(factory = statsFactory)
                val iaService = remember { IAServiceImpl("https://your-api-base-url.com") }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        TicketScanBottomNavigation(navController) {
                            navController.navigate(
                                "scan"
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { HomeScreen(navController = navController) }
                        composable("expenses") { StatsScreen(navController = navController,
                            statsViewModel = statsViewModel
                        ) }
                        composable("record_audio") {
                            RecordAudioScreen(
                                navController = navController,
                                iaService = iaService,
                                onResult = { result -> navController.navigate("ticket") {}
                                }
                            )
                        }
                        composable("scan") { /* TODO: Scan screen */ HomeScreen(navController = navController) }
                        composable("profile") { /* TODO: Profile screen */ HomeScreen(navController = navController) }
                        composable("more") { /* TODO: More screen */ HomeScreen(navController = navController) }
                        composable("ticket") { TicketScreen(navController = navController, ticketsViewModel) }
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
        HomeScreen(navController = navController)
    }
}