package com.example.ticketscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ticketscan.domain.repositories.StatsRepositoryMock
import com.example.ticketscan.domain.repositories.TicketRepositoryMock
import com.example.ticketscan.ui.components.TicketScanBottomNavigation
import com.example.ticketscan.ui.screens.HomeScreen
import com.example.ticketscan.ui.screens.ProcessingScreen
import com.example.ticketscan.ui.screens.StatsScreen
import com.example.ticketscan.ui.screens.StatsViewModel
import com.example.ticketscan.ui.screens.StatsViewModelFactory
import com.example.ticketscan.ui.screens.TicketScreen
import com.example.ticketscan.ui.screens.TicketViewModel
import com.example.ticketscan.ui.screens.TicketViewModelFactory
import com.example.ticketscan.ui.theme.TicketScanThemeProvider
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TicketScanThemeProvider {
                val navController = rememberNavController()
                val repository = StatsRepositoryMock()
                val statsFactory = StatsViewModelFactory(repository)
                val statsViewModel: StatsViewModel = viewModel(factory = statsFactory)
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
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        composable("home") { HomeScreen(navController = navController) }
                        composable("expenses") { StatsScreen(navController = navController,
                            statsViewModel = statsViewModel
                        ) }
                        composable("scan") { /* TODO: Scan screen */ HomeScreen(navController = navController) }
                        composable("profile") { /* TODO: Profile screen */ HomeScreen(navController = navController) }
                        composable("more") { /* TODO: More screen */ HomeScreen(navController = navController) }
                        composable("ticket/{id}") { backStackEntry ->
                            val idArg = backStackEntry.arguments?.getString("id")
                            val uuid = UUID.fromString(idArg)
                            val factoryWithId = remember(uuid) { TicketViewModelFactory(TicketRepositoryMock(), uuid) }
                            val viewModelWithId: TicketViewModel = viewModel(factory = factoryWithId)
                            TicketScreen(navController = navController, viewModelWithId)
                        }
                        composable("processing/{mode}") { backStackEntry ->
                            val mode = backStackEntry.arguments?.getString("mode") ?: "unknown"
                            ProcessingScreen(navController = navController, mode = mode)
                        }
                    }
                }
            }
        }
    }
}
