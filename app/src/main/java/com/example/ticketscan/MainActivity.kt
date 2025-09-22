package com.example.ticketscan

import android.content.Intent
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
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ia.example.AnalisisActivity
import com.example.ticketscan.ui.theme.TicketScanTheme
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
import com.example.ticketscan.domain.StatsRepositoryImpl
import com.example.ticketscan.ui.components.TicketScanBottomNavigation
import com.example.ticketscan.ui.screens.StatsScreen
import com.example.ticketscan.ui.screens.StatsViewModel
import com.example.ticketscan.ui.screens.StatsViewModelFactory
import com.example.ticketscan.ui.theme.TicketScanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = StatsRepositoryImpl()
        val factory = StatsViewModelFactory(repository)

        setContent {
            TicketScanThemeProvider {
                val navController = rememberNavController()
                val factory = remember { TicketViewModelFactory(TicketRepositoryMock()) }
                val ticketsViewModel: TicketViewModel = viewModel(factory = factory)
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
                        composable("expenses") { /* TODO: Expenses screen */ HomeScreen(navController = navController) }
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
fun MainScreenWithBottomNavPreview() {
    TicketScanThemeProvider {
        val repository = StatsRepositoryImpl()
        val factory = StatsViewModelFactory(repository)
        val navController = rememberNavController()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = { TicketScanBottomNavigation(navController) }
        ) { innerPadding ->
            val statsViewModel: StatsViewModel = viewModel(factory = factory)
            StatsScreen(
                statsViewModel = statsViewModel,
                navController = navController
            )
        }
    }
}