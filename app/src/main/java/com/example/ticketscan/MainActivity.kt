package com.example.ticketscan

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ticketscan.data.database.DatabaseHelper
import com.example.ticketscan.domain.model.TicketOrigin
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.domain.viewmodel.RepositoryViewModelFactory
import com.example.ticketscan.ia.internal.IAService
import com.example.ticketscan.ia.internal.IAServiceImpl
import com.example.ticketscan.ia.internal.mock.MockIAApi
import com.example.ticketscan.ui.components.TicketScanBottomNavigation
import com.example.ticketscan.ui.screens.AppearanceSettingsScreen
import com.example.ticketscan.ui.screens.AppearanceSettingsViewModel
import com.example.ticketscan.ui.screens.AppearanceSettingsViewModelFactory
import com.example.ticketscan.ui.screens.CameraScanScreen
import com.example.ticketscan.ui.screens.CameraScanViewModel
import com.example.ticketscan.ui.screens.CameraScanViewModelFactory
import com.example.ticketscan.ui.screens.CategoryDetailsScreen
import com.example.ticketscan.ui.screens.DefaultTicketEntryScreen
import com.example.ticketscan.ui.screens.EditContactScreen
import com.example.ticketscan.ui.screens.HomeScreen
import com.example.ticketscan.ui.screens.MoreScreen
import com.example.ticketscan.ui.screens.NotificationSettingsScreen
import com.example.ticketscan.ui.screens.ProcessingScreen
import com.example.ticketscan.ui.screens.ProfileScreen
import com.example.ticketscan.ui.screens.RecordAudioScreen
import com.example.ticketscan.ui.screens.StatsScreen
import com.example.ticketscan.ui.screens.StatsViewModel
import com.example.ticketscan.ui.screens.StatsViewModelFactory
import com.example.ticketscan.ui.screens.TicketScreen
import com.example.ticketscan.ui.screens.TicketViewModel
import com.example.ticketscan.ui.screens.TicketViewModelFactory
import com.example.ticketscan.ui.theme.TicketScanThemeProvider
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar la base de datos
        // Esto creará las tablas si no existen e insertará datos por defecto
        DatabaseHelper.getInstance(this).writableDatabase

        enableEdgeToEdge()

        setContent {
            val appearanceFactory = remember { AppearanceSettingsViewModelFactory(applicationContext) }
            val appearanceViewModel: AppearanceSettingsViewModel = viewModel(factory = appearanceFactory)
            val appearancePreferences by appearanceViewModel.uiState.collectAsState()

            TicketScanThemeProvider(appearance = appearancePreferences) {
                val navController = rememberNavController()
                val repositoryViewModelFactory = RepositoryViewModelFactory(context = this@MainActivity)
                val repositoryViewModel: RepositoryViewModel = viewModel(factory = repositoryViewModelFactory)
                val iaService: IAService = remember { IAServiceImpl(MockIAApi(repositoryViewModel)) }

                val cameraScanFactory = CameraScanViewModelFactory(iaService, repositoryViewModel)
                val cameraScanViewModel: CameraScanViewModel = viewModel(factory = cameraScanFactory)

                val statsFactory = remember { StatsViewModelFactory(repositoryViewModel) }
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
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        composable("home") { HomeScreen(navController, repositoryViewModel) }
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
                                repositoryViewModel = repositoryViewModel,
                                maxPeriods = 4,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("record_audio") {
                            RecordAudioScreen(
                                navController = navController,
                                iaService = iaService,
                                repositoryViewModel = repositoryViewModel,
                                onResult = { result -> navController.navigate("ticket") {}
                                }
                            )
                        }
                        composable("scan") { CameraScanScreen(navController = navController, vm = cameraScanViewModel) }
                        composable("profile") { ProfileScreen(navController = navController) }
                        composable("edit_contact") {
                            EditContactScreen(
                                navController = navController,
                                onSave = { name, lastName, email, phone ->
                                    // TODO: Save the contact information
                                }
                            )
                        }
                        composable("notification_settings") {
                            NotificationSettingsScreen(navController = navController)
                        }
                        composable("appearance_settings") {
                            AppearanceSettingsScreen(
                                navController = navController,
                                viewModel = appearanceViewModel
                            )
                        }
                        composable("default_ticket_entry") {
                            DefaultTicketEntryScreen(navController = navController)
                        }
                        composable("more") {
                            MoreScreen(navController = navController)
                        }
                        composable("ticket/{id}") { backStackEntry ->
                            val idArg = backStackEntry.arguments?.getString("id")
                            val uuid = UUID.fromString(idArg)
                            val factoryWithId = remember(uuid) { TicketViewModelFactory(repositoryViewModel, uuid) }
                            val viewModelWithId: TicketViewModel = viewModel(factory = factoryWithId)
                            TicketScreen(navController = navController, viewModelWithId)
                        }
                        composable("processing/{mode}") { backStackEntry ->
                            val mode = backStackEntry.arguments?.getString("mode") ?: "unknown"
                            ProcessingScreen(navController = navController, mode = TicketOrigin.fromString(mode), repository = repositoryViewModel)
                        }
                    }
                }
            }
        }
    }
}