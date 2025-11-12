package com.example.ticketscan

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
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
import com.example.ticketscan.ui.components.NotificationPermissionHandler
import com.example.ticketscan.ui.components.Period
import com.example.ticketscan.ui.components.TicketScanBottomNavigation
import com.example.ticketscan.ui.screens.AppearanceSettingsScreen
import com.example.ticketscan.ui.screens.AppearanceSettingsViewModel
import com.example.ticketscan.ui.screens.AppearanceSettingsViewModelFactory
import com.example.ticketscan.ui.screens.CameraScanScreen
import com.example.ticketscan.ui.screens.CameraScanViewModel
import com.example.ticketscan.ui.screens.CameraScanViewModelFactory
import com.example.ticketscan.ui.screens.CategoryDetailsScreen
import com.example.ticketscan.ui.screens.ContactInfoViewModel
import com.example.ticketscan.ui.screens.ContactInfoViewModelFactory
import com.example.ticketscan.ui.screens.DefaultTicketEntryScreen
import com.example.ticketscan.ui.screens.EditContactScreen
import com.example.ticketscan.ui.screens.HomeScreen
import com.example.ticketscan.ui.screens.MoreScreen
import com.example.ticketscan.ui.screens.NotificationNavigationViewModel
import com.example.ticketscan.ui.screens.NotificationSettingsScreen
import com.example.ticketscan.ui.screens.NotificationSettingsViewModel
import com.example.ticketscan.ui.screens.NotificationSettingsViewModelFactory
import com.example.ticketscan.ui.screens.MonthlyComparisonScreen
import com.example.ticketscan.ui.screens.ProcessingScreen
import com.example.ticketscan.ui.screens.ProfileScreen
import com.example.ticketscan.ui.screens.RecordAudioScreen
import com.example.ticketscan.ui.screens.RecordAudioViewModel
import com.example.ticketscan.ui.screens.RecordAudioViewModelFactory
import com.example.ticketscan.ui.screens.StatsScreen
import com.example.ticketscan.ui.screens.StatsViewModel
import com.example.ticketscan.ui.screens.StatsViewModelFactory
import com.example.ticketscan.ui.screens.TicketScreen
import com.example.ticketscan.ui.screens.TicketViewModel
import com.example.ticketscan.ui.screens.TicketViewModelFactory
import com.example.ticketscan.ui.screens.PdfOptionsScreen
import com.example.ticketscan.domain.model.notifications.NotificationType
import com.example.ticketscan.ui.theme.TicketScanThemeProvider
import kotlinx.coroutines.flow.collectLatest
import java.util.UUID

class MainActivity : ComponentActivity() {
    private val notificationNavigationViewModel: NotificationNavigationViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar la base de datos
        // Esto creará las tablas si no existen e insertará datos por defecto
        DatabaseHelper.getInstance(this).writableDatabase

        enableEdgeToEdge()

        notificationNavigationViewModel.handleIntent(intent)

        setContent {
            val appearanceFactory = remember { AppearanceSettingsViewModelFactory(applicationContext) }
            val appearanceViewModel: AppearanceSettingsViewModel = viewModel(factory = appearanceFactory)
            val appearancePreferences by appearanceViewModel.uiState.collectAsState()

            TicketScanThemeProvider(appearance = appearancePreferences) {
                val navController = rememberNavController()
                val repositoryViewModelFactory = RepositoryViewModelFactory(context = this@MainActivity)
                val repositoryViewModel: RepositoryViewModel = viewModel(factory = repositoryViewModelFactory)

                // Configure AI Service
                // Option 1: Use real mock-ai-server
                // For emulator: use 10.0.2.2 (localhost on host machine)
                // For physical device: replace with your computer's IP address (e.g., 192.168.1.XXX)
                val iaService: IAService = remember { IAServiceImpl("http://10.0.2.2:8080/") }

                // Option 2: Use mock API for offline testing (no server needed)
                // val iaService: IAService = remember { IAServiceImpl(MockIAApi(repositoryViewModel)) }

                val cameraScanFactory = CameraScanViewModelFactory(iaService, repositoryViewModel)
                val cameraScanViewModel: CameraScanViewModel = viewModel(factory = cameraScanFactory)

                val statsFactory = remember { StatsViewModelFactory(repositoryViewModel) }
                val statsViewModel: StatsViewModel = viewModel(factory = statsFactory)
                val contactInfoFactory = remember { ContactInfoViewModelFactory(applicationContext) }
                val contactInfoViewModel: ContactInfoViewModel = viewModel(factory = contactInfoFactory)

                // Record audio viewmodel factory and instance
                val recordAudioFactory = remember { RecordAudioViewModelFactory(iaService, repositoryViewModel) }
                val recordAudioViewModel: RecordAudioViewModel = viewModel(factory = recordAudioFactory)
                val notificationSettingsFactory = remember { NotificationSettingsViewModelFactory.fromContext(applicationContext) }

                NotificationPermissionHandler()

                LaunchedEffect(Unit) {
                    notificationNavigationViewModel.navigationEvents.collectLatest { payload ->
                        when (payload.type) {
                            NotificationType.WEEKLY_INACTIVITY -> {
                                navController.navigate("home") {
                                    launchSingleTop = true
                                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                }
                            }
                            NotificationType.WEEKLY_STATS -> {
                                statsViewModel.onPeriodChanged(Period.SEMANAL)
                                navController.navigate("expenses") {
                                    launchSingleTop = true
                                }
                            }
                            NotificationType.MONTHLY_COMPARISON -> {
                                navController.navigate("notification_monthly_comparison") {
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                }

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
                        composable("record_audio") { RecordAudioScreen(navController = navController, vm = recordAudioViewModel) }

                        composable("scan") { CameraScanScreen(navController = navController, vm = cameraScanViewModel) }
                        composable("profile") {
                            ProfileScreen(
                                navController = navController,
                                viewModel = contactInfoViewModel,
                                repositoryViewModel = repositoryViewModel
                            )
                        }
                        composable("edit_contact") {
                            EditContactScreen(
                                navController = navController,
                                viewModel = contactInfoViewModel
                            )
                        }
                        composable("notification_settings") {
                            val notificationSettingsViewModel: NotificationSettingsViewModel = viewModel(factory = notificationSettingsFactory)
                            NotificationSettingsScreen(
                                navController = navController,
                                viewModel = notificationSettingsViewModel
                            )
                        }
                        composable("notification_monthly_comparison") {
                            MonthlyComparisonScreen(
                                navController = navController,
                                navigationViewModel = notificationNavigationViewModel
                            )
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
                        composable("pdf_options/{id}") { backStackEntry ->
                            val idArg = backStackEntry.arguments?.getString("id")
                            val uuid = UUID.fromString(idArg)
                            val factoryWithId = remember(uuid) { TicketViewModelFactory(repositoryViewModel, uuid) }
                            val viewModelWithId: TicketViewModel = viewModel(factory = factoryWithId)
                            PdfOptionsScreen(navController = navController, viewModel = viewModelWithId)
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        notificationNavigationViewModel.handleIntent(intent)
    }
}