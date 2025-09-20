package com.example.ticketscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.ticketscan.ui.screens.HomeScreen
import com.example.ticketscan.ui.theme.TicketScanThemeProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ticketscan.domain.StatsRepositoryImpl
import com.example.ticketscan.ui.screens.StatsScreen
import com.example.ticketscan.ui.screens.StatsViewModel
import com.example.ticketscan.ui.screens.StatsViewModelFactory
import com.example.ticketscan.ui.theme.TicketScanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicketScanThemeProvider {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { com.example.ticketscan.ui.components.TicketScanBottomNavigation(navController) }
                ) { innerPadding ->
                    HomeScreen(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// ver mi screen como main
/*class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = StatsRepositoryImpl()
        val factory = StatsViewModelFactory(repository)

        setContent {
            TicketScanTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val statsViewModel: StatsViewModel = viewModel(factory = factory)
                    StatsScreen(
                        statsViewModel = statsViewModel
                    )
                }
            }
        }
    }
}*/

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    TicketScanThemeProvider {
        val navController = rememberNavController()
        HomeScreen(navController = navController)
    }
}