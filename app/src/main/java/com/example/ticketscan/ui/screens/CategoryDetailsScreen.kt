package com.example.ticketscan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.repositories.stats.PeriodExpense
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.ui.components.PeriodSelector
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailsScreen(
    categoryName: String,
    repositoryViewModel: RepositoryViewModel,
    maxPeriods: Int,
    onBack: () -> Unit
) {
    val viewModel: CategoryDetailsViewModel = viewModel(
        factory = CategoryDetailsViewModelFactory(repositoryViewModel, categoryName, maxPeriods)
    )
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = TicketScanIcons.categoryIcon(categoryName),
                            contentDescription = null,
                            tint = TicketScanTheme.colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(TicketScanTheme.spacing.sm))
                        Text(text = categoryName)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(TicketScanIcons.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            PeriodSelector(selectedPeriod = uiState.period, onPeriodChange = { viewModel.setPeriod(it) })
            PeriodExpensesChart(periodExpenses = uiState.periodExpenses, maxPeriods = maxPeriods)
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.transactions) {
                    TransactionItem(ticket = it)
                }
            }
        }
    }
}

@Composable
fun PeriodExpensesChart(periodExpenses: List<PeriodExpense>, maxPeriods: Int) {
    // Simple placeholder bar chart built with Compose primitives to avoid adding a 3rd-party dependency.
    val currencyFormat = NumberFormat.getCurrencyInstance().apply { maximumFractionDigits = 0 }
    val maxValue = periodExpenses.maxOfOrNull { it.amount } ?: java.math.BigDecimal.ONE

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Gastos por período", style = TicketScanTheme.typography.titleMedium)

            Row(modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Show up to maxPeriods bars; if less data, fill remaining with empty space
                val periodsToShow = periodExpenses.take(maxPeriods)
                for (i in 0 until maxPeriods) {
                    val expense = periodsToShow.getOrNull(i)
                    val fraction = if (expense != null) {
                        val amt = expense.amount.toDouble()
                        val max = maxValue.toDouble().coerceAtLeast(1.0)
                        (amt / max).toFloat()
                    } else 0f

                    val barColor = TicketScanTheme.colors.primary
                    Box(modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height((fraction * 100).dp)
                            .background(color = barColor)
                        )
                    }
                }
            }

            // Legend / values
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                periodExpenses.take(maxPeriods).forEach { p ->
                    Text(text = p.periodName, style = TicketScanTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(ticket: Ticket) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = ticket.store?.name ?: ticket.id.toString().take(8), fontWeight = FontWeight.Bold)
                Text(text = ticket.date.toString(), style = TicketScanTheme.typography.bodySmall)
            }
            Text(
                text = "$${ticket.total}",
                fontWeight = FontWeight.Bold,
                color = TicketScanTheme.colors.primary
            )
        }
    }
}