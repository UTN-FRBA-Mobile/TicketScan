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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ticketscan.domain.repositories.stats.StatsRepositoryMock
import com.example.ticketscan.domain.repositories.ticket.TicketRepositoryMock
import com.example.ticketscan.ui.components.TicketScanBottomNavigation
import com.example.ticketscan.ui.screens.*
import com.example.ticketscan.ui.theme.TicketScanThemeProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TicketScanThemeProvider {
                val navController = rememberNavController()

                val ticketRepository = remember { TicketRepositoryMock() }
                val statsRepository = remember { StatsRepositoryMock(ticketRepository) }

                val ticketFactory = remember { TicketViewModelFactory(ticketRepository) }
                val ticketsViewModel: TicketViewModel = viewModel(factory = ticketFactory)
                val statsFactory = remember { StatsViewModelFactory(statsRepository) }
                val statsViewModel: StatsViewModel = viewModel(factory = statsFactory)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        TicketScanBottomNavigation(navController) {
                            navController.navigate("scan")
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { HomeScreen(navController = navController) }
                        composable("expenses") {
                            StatsScreen(
                                navController = navController,
                                statsViewModel = statsViewModel,
                                onCategoryClick = { categoryName ->
                                    navController.navigate("categoryDetails/$categoryName")
                                }
                            )
                        }
                        composable(
                            "categoryDetails/{categoryName}",
                            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
                            CategoryDetailsScreen(
                                categoryName = categoryName,
                                statsRepository = statsRepository,
                                onBack = { navController.popBackStack() }
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