package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.repositories.stats.MonthlyExpense
import com.example.ticketscan.domain.repositories.stats.StatsRepository
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailsScreen(
    categoryName: String,
    statsRepository: StatsRepository,
    onBack: () -> Unit
) {
    val viewModel: CategoryDetailsViewModel = viewModel(
        factory = CategoryDetailsViewModelFactory(statsRepository, categoryName)
    )
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Gastos de la categoría") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            MonthlyExpensesChart(monthlyExpenses = uiState.monthlyExpenses)
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.transactions) {
                    TransactionItem(ticket = it)
                }
            }
        }
    }
}

@Composable
fun MonthlyExpensesChart(monthlyExpenses: List<MonthlyExpense>) {
    val chartEntryModelProducer = ChartEntryModelProducer(
        monthlyExpenses.mapIndexed { index, expense ->
            entryOf(index.toFloat(), expense.amount.toFloat())
        }
    )
    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        monthlyExpenses.getOrNull(value.toInt())?.month ?: ""
    }

    val currencyFormat = NumberFormat.getCurrencyInstance().apply {
        maximumFractionDigits = 0
    }
    val startAxisValueFormatter = AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
        currencyFormat.format(value)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "Gastos por mes", style = MaterialTheme.typography.titleMedium)
            Chart(
                chart = columnChart(
                    columns = listOf(lineComponent(color = MaterialTheme.colorScheme.primary))
                ),
                chartModelProducer = chartEntryModelProducer,
                startAxis = rememberStartAxis(
                    valueFormatter = startAxisValueFormatter,
                    itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 5)
                ),
                bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisValueFormatter),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
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
                Text(text = ticket.store.name, fontWeight = FontWeight.Bold)
                Text(text = ticket.date.toString(), style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "$${ticket.total}",
                fontWeight = FontWeight.Bold,
                color = if (ticket.total > 0) Color.Green else Color.Red
            )
        }
    }
}