package com.example.ticketscan.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ticketscan.domain.repositories.stats.PeriodExpense
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.ui.components.PeriodSelector
import com.example.ticketscan.ui.components.TicketScanCard
import com.example.ticketscan.ui.components.TicketScanCardStyle
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TicketScanCard(
                style = TicketScanCardStyle.Tonal,
                contentPadding = PaddingValues(TicketScanTheme.spacing.lg)
            ) {
                PeriodSelector(
                    selectedPeriod = uiState.period,
                    onPeriodChange = { viewModel.setPeriod(it) }
                )
            }
            Spacer(modifier = Modifier.height(TicketScanTheme.spacing.lg))
            TicketScanCard(
                style = TicketScanCardStyle.Tonal,
                contentPadding = PaddingValues(TicketScanTheme.spacing.lg)
            ) {
                Text(
                    text = "Importe total del periodo",
                    style = TicketScanTheme.typography.titleMedium,
                    color = TicketScanTheme.colors.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = TicketScanTheme.spacing.sm),
                    textAlign = TextAlign.Center
                )
                val currencyFormat = remember { NumberFormat.getCurrencyInstance() }
                Text(
                    text = currencyFormat.format(uiState.totalAmountForPeriod),
                    fontWeight = FontWeight.Bold,
                    color = TicketScanTheme.colors.primary,
                    fontSize = TicketScanTheme.typography.headlineMedium.fontSize,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(TicketScanTheme.spacing.lg))
            PeriodExpensesChart(periodExpenses = uiState.periodExpenses, maxPeriods = maxPeriods)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(uiState.transactions) { transaction ->
                    TransactionItem(transaction = transaction)
                }
            }
        }
    }
}

@Composable
fun PeriodExpensesChart(periodExpenses: List<PeriodExpense>, maxPeriods: Int) {
    // Compose-based bar chart with explicit Y axis labels, using themed colors for light/dark parity.
    val periodsToShow = periodExpenses.take(maxPeriods)
    val currencyFormat = NumberFormat.getCurrencyInstance().apply { maximumFractionDigits = 0 }
    val maxValue = periodsToShow.maxOfOrNull { it.amount } ?: BigDecimal.ZERO
    val maxDenominator = maxValue.toDouble().takeIf { it > 0.0 } ?: 1.0
    val chartVerticalPadding = TicketScanTheme.spacing.md
    val chartHorizontalPadding = TicketScanTheme.spacing.lg
    val axisSteps = 4
    val stepCount = BigDecimal.valueOf(axisSteps.toLong())
    val axisLabels = (0..axisSteps).map { step ->
        val numerator = BigDecimal.valueOf((axisSteps - step).toLong())
        val ratio = if (maxValue == BigDecimal.ZERO) BigDecimal.ZERO else numerator.divide(stepCount, 2, RoundingMode.HALF_UP)
        val value = maxValue.multiply(ratio)
        currencyFormat.format(value)
    }
    val chartShape = RoundedCornerShape(TicketScanTheme.spacing.sm)
    val barShape = RoundedCornerShape(topStart = TicketScanTheme.spacing.sm, topEnd = TicketScanTheme.spacing.sm)
    val chartBackgroundColor = TicketScanTheme.colors.surface
    val chartBorderColor = TicketScanTheme.colors.outline.copy(alpha = 0.2f)
    val gridColor = TicketScanTheme.colors.onSurfaceVariant.copy(alpha = 0.14f)
    val barColor = TicketScanTheme.colors.primary

    TicketScanCard(
        style = TicketScanCardStyle.Tonal,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = TicketScanTheme.spacing.md)
    ) {
        Column(modifier = Modifier.padding(vertical = TicketScanTheme.spacing.md)) {
            Text(
                text = "Gastos por período",
                style = TicketScanTheme.typography.titleMedium,
                color = TicketScanTheme.colors.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = TicketScanTheme.spacing.sm),
                textAlign = TextAlign.Center
            )

            val axisLabelWidth = 64.dp
            val axisSpacing = TicketScanTheme.spacing.sm
            if (periodsToShow.isEmpty()) {
                Text(
                    text = "Sin datos disponibles",
                    style = TicketScanTheme.typography.bodyMedium,
                    color = TicketScanTheme.colors.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = TicketScanTheme.spacing.md)
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(148.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(top = chartVerticalPadding, bottom = chartVerticalPadding)
                            .width(axisLabelWidth),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.End
                    ) {
                        axisLabels.forEach { label ->
                            Text(
                                text = label,
                                style = TicketScanTheme.typography.bodySmall,
                                color = TicketScanTheme.colors.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(axisSpacing))

                    val density = LocalDensity.current
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(chartShape)
                            .background(chartBackgroundColor)
                            .border(width = 1.dp, color = chartBorderColor, shape = chartShape)
                    ) {
                        val outlineColor = gridColor
                        Canvas(modifier = Modifier.matchParentSize()) {
                            val topPaddingPx = with(density) { chartVerticalPadding.toPx() }
                            val strokeWidthPx = with(density) { 1.dp.toPx() }
                            val usableHeight = size.height - (topPaddingPx * 2f)
                            if (usableHeight > 0f) {
                                for (step in 0..axisSteps) {
                                    val fraction = step / axisSteps.toFloat()
                                    val y = size.height - topPaddingPx - (usableHeight * fraction)
                                    drawLine(
                                        color = outlineColor,
                                        start = Offset(0f, y),
                                        end = Offset(size.width, y),
                                        strokeWidth = strokeWidthPx
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = chartHorizontalPadding - axisSpacing, vertical = chartVerticalPadding),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            periodsToShow.forEach { expense ->
                                val fraction = (expense.amount.toDouble() / maxDenominator).toFloat().coerceIn(0f, 1f)

                                Box(
                                    modifier = Modifier
                                        .width(TicketScanTheme.spacing.xl)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    if (fraction > 0f) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(fraction)
                                                .clip(barShape)
                                                .background(barColor)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = TicketScanTheme.spacing.sm)
                        .padding(start = axisLabelWidth + axisSpacing),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    periodsToShow.forEach { expense ->
                        val periodName = expense.periodName
                        Text(
                            text = periodName,
                            style = TicketScanTheme.typography.bodySmall,
                            color = TicketScanTheme.colors.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(TicketScanTheme.spacing.xxl)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: CategoryTransaction) {
    TicketScanCard(
        style = TicketScanCardStyle.Tonal,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = transaction.storeName ?: transaction.ticketId.toString().take(8),
                    fontWeight = FontWeight.Bold,
                    color = TicketScanTheme.colors.onSurface
                )
                val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
                Text(
                    text = dateFormatter.format(transaction.date),
                    style = TicketScanTheme.typography.bodySmall,
                    color = TicketScanTheme.colors.onSurfaceVariant
                )
            }
            val currencyFormat = remember { NumberFormat.getCurrencyInstance() }
            Text(
                text = currencyFormat.format(transaction.amount),
                fontWeight = FontWeight.Bold,
                color = TicketScanTheme.colors.primary
            )
        }
    }
}