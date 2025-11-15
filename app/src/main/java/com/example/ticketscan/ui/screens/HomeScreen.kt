package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.domain.viewmodel.RepositoryViewModelFactory
import com.example.ticketscan.ui.components.FilterPanel
import com.example.ticketscan.ui.components.TicketScanCard
import com.example.ticketscan.ui.components.TicketScanCardStyle
import com.example.ticketscan.ui.components.TicketScanEmptyState
import com.example.ticketscan.ui.components.TicketScanScreenContainer
import com.example.ticketscan.ui.components.TicketScanSectionHeader
import com.example.ticketscan.ui.components.UploadCard
import com.example.ticketscan.ui.components.UploadOption
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import com.example.ticketscan.ui.theme.TicketScanThemeProvider
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    repositoryViewModel: RepositoryViewModel,
    modifier: Modifier = Modifier,
    ) {

    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repositoryViewModel))
    val tickets by homeViewModel.tickets.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()

    val searchFilterViewModel: SearchFilterViewModel = viewModel(factory = SearchFilterViewModelFactory())
    val ticketTitleSearch by searchFilterViewModel.ticketTitleSearch.collectAsState()
    val selectedStore by searchFilterViewModel.selectedStore.collectAsState()
    val dateRange by searchFilterViewModel.dateRange.collectAsState()
    val amountRange by searchFilterViewModel.amountRange.collectAsState()
    val selectedCategory by searchFilterViewModel.selectedCategory.collectAsState()
    val activeFilterCount = searchFilterViewModel.getActiveFilterCount()

    val allStores = remember { mutableStateOf<List<com.example.ticketscan.domain.model.Store>>(emptyList()) }
    val allCategories = remember { mutableStateOf<List<com.example.ticketscan.domain.model.Category>>(emptyList()) }

    // Load stores and categories
    LaunchedEffect(Unit) {
        allStores.value = repositoryViewModel.getAllStores()
        allCategories.value = repositoryViewModel.getAllCategories()
    }

    // Apply filters when they change
    LaunchedEffect(ticketTitleSearch, selectedStore, dateRange, amountRange, selectedCategory) {
        val filter = searchFilterViewModel.toTicketFilter()
        homeViewModel.applyFilters(filter)
    }

    var amountError by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(amountRange) {
        val min = amountRange?.min
        val max = amountRange?.max
        amountError = if (min != null && max != null && (min > max)) {
            "El monto mínimo debe ser menor o igual al máximo."
        } else {
            null
        }
    }

    TicketScanScreenContainer(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.xl)
    ) {
        TicketScanCard(
            style = TicketScanCardStyle.Filled,
            containerColor = TicketScanTheme.colors.primary,
            contentColor = TicketScanTheme.colors.onPrimary,
            contentPadding = PaddingValues(
                vertical = TicketScanTheme.spacing.xl,
                horizontal = TicketScanTheme.spacing.lg
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Carga tu ticket",
                    style = TicketScanTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth(),
                    color = TicketScanTheme.colors.onPrimary,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        TicketScanTheme.spacing.lg,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UploadOption(
                        label = "Audio",
                        icon = TicketScanIcons.Audio,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("processing/audio") }
                    )
                    UploadOption(
                        label = "Cámara",
                        icon = TicketScanIcons.Camera,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("scan") }
                    )
                    UploadOption(
                        label = "Texto",
                        icon = TicketScanIcons.Text,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("processing/text") }
                    )
                }
            }
        }

        // Filter Panel
        FilterPanel(
            ticketTitleSearch = ticketTitleSearch,
            onTicketTitleSearchChange = { searchFilterViewModel.updateTicketTitleSearch(it) },
            storeFilter = selectedStore,
            onStoreFilterChange = { searchFilterViewModel.setStoreFilter(it) },
            availableStores = allStores.value,
            categoryFilter = selectedCategory,
            onCategoryFilterChange = { searchFilterViewModel.setCategoryFilter(it) },
            availableCategories = allCategories.value,
            dateRange = dateRange,
            onDateRangeChange = { searchFilterViewModel.setDateRange(it) },
            amountRange = amountRange,
            onAmountRangeChange = { searchFilterViewModel.setAmountRange(it) },
            activeFilterCount = activeFilterCount,
            onClearAll = { searchFilterViewModel.clearFilters() },
            amountError = amountError,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        TicketScanSectionHeader(
            title = if (activeFilterCount > 0) "Resultados de búsqueda" else "Últimas cargas",
            subtitle = if (activeFilterCount > 0) "${tickets.size} ticket(s) encontrado(s)" else "Revisá tus comprobantes recientes"
        )

        if (isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator()
            }
        } else if (tickets.isEmpty()) {
            TicketScanEmptyState(
                icon = TicketScanIcons.EmptyInbox,
                title = if (activeFilterCount > 0) "No se encontraron tickets" else "Todavía no cargaste tickets",
                description = if (activeFilterCount > 0)
                    "Intenta ajustar los filtros de búsqueda."
                else
                    "Escaneá o subí un ticket para comenzar a ver tu historial.",
                actionLabel = if (activeFilterCount > 0) "Limpiar filtros" else "Cargar ticket",
                onActionClick = {
                    if (activeFilterCount > 0) {
                        searchFilterViewModel.clearFilters()
                    } else {
                        navController.navigate("scan")
                    }
                }
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.md)
            ) {
                tickets.forEach { ticket ->
                    UploadCard(
                        title = ticket.store?.name ?: "Ticket ${ticket.id.toString().take(8)}",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.navigate("ticket/${ticket.id}") }
                    ) {
                        val formattedDate = ticket.date.let {
                            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it)
                        } ?: "Fecha sin definir"
                        Text(
                            text = "$formattedDate  •  Total: $${ticket.total}",
                            style = TicketScanTheme.typography.bodyMedium,
                            color = TicketScanTheme.colors.onSurfaceVariant
                        )
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
        val repositoryViewModelFactory = RepositoryViewModelFactory(context = LocalContext.current)
        val repositoryViewModel: RepositoryViewModel = viewModel(factory = repositoryViewModelFactory)
        HomeScreen(navController = navController, repositoryViewModel)
    }
}
