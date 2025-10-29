package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ticketscan.R
import com.example.ticketscan.domain.model.notifications.NotificationPayload
import com.example.ticketscan.domain.model.notifications.NotificationType
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    navController: NavController,
    notificationType: NotificationType?,
    navigationViewModel: NotificationNavigationViewModel
) {
    val payloadMap by navigationViewModel.latestByType.collectAsState()
    val payload = notificationType?.let(payloadMap::get)

    val currencyFormatter = remember { NumberFormat.getCurrencyInstance() }
    val percentageFormatter = remember {
        NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            maximumFractionDigits = 1
            minimumFractionDigits = 1
        }
    }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = when (notificationType) {
                        NotificationType.WEEKLY_STATS -> stringResource(R.string.notification_weekly_stats_title)
                        NotificationType.MONTHLY_COMPARISON -> stringResource(R.string.notification_monthly_comparison_title)
                        NotificationType.WEEKLY_INACTIVITY -> stringResource(R.string.notification_weekly_inactivity_title)
                        null -> stringResource(R.string.notification_detail_section_activity)
                    }
                    Text(text = title, style = TicketScanTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = TicketScanIcons.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (payload == null || notificationType == null) {
                EmptyNotificationState()
            } else {
                NotificationDetailContent(
                    payload = payload,
                    currencyFormatter = currencyFormatter,
                    percentageFormatter = percentageFormatter,
                    dateFormatter = dateFormatter
                )
            }
        }
    }
}

@Composable
private fun NotificationDetailContent(
    payload: NotificationPayload,
    currencyFormatter: NumberFormat,
    percentageFormatter: NumberFormat,
    dateFormatter: DateTimeFormatter
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (payload) {
            is NotificationPayload.WeeklyStats -> WeeklyStatsDetail(payload, currencyFormatter)
            is NotificationPayload.MonthlyComparison -> MonthlyComparisonDetail(payload, currencyFormatter, percentageFormatter)
            is NotificationPayload.WeeklyInactivity -> WeeklyInactivityDetail(payload, dateFormatter)
        }
    }
}

@Composable
private fun WeeklyStatsDetail(
    payload: NotificationPayload.WeeklyStats,
    currencyFormatter: NumberFormat
) {
    DetailSection(title = stringResource(R.string.notification_detail_section_weekly)) {
        DetailRow(
            label = stringResource(R.string.notification_detail_total_week),
            value = currencyFormatter.format(payload.totalSpent)
        )
        DetailRow(
            label = stringResource(R.string.notification_detail_average_ticket),
            value = currencyFormatter.format(payload.averagePerTicket)
        )
        DetailRow(
            label = stringResource(R.string.notification_detail_previous_week),
            value = currencyFormatter.format(payload.previousTotal)
        )
        DetailRow(
            label = stringResource(R.string.notification_detail_ticket_count),
            value = payload.ticketCount.toString()
        )
        payload.topCategory?.takeIf { it.isNotBlank() }?.let {
            DetailRow(
                label = stringResource(R.string.notification_detail_top_category),
                value = it
            )
        }
    }
}

@Composable
private fun MonthlyComparisonDetail(
    payload: NotificationPayload.MonthlyComparison,
    currencyFormatter: NumberFormat,
    percentageFormatter: NumberFormat
) {
    DetailSection(title = stringResource(R.string.notification_detail_section_monthly)) {
        DetailRow(
            label = stringResource(R.string.notification_detail_current_month),
            value = currencyFormatter.format(payload.currentTotal)
        )
        DetailRow(
            label = stringResource(R.string.notification_detail_previous_month),
            value = currencyFormatter.format(payload.previousTotal)
        )
        DetailRow(
            label = stringResource(R.string.notification_detail_difference),
            value = formatDifference(currencyFormatter, payload.difference)
        )
        DetailRow(
            label = stringResource(R.string.notification_detail_variation),
            value = formatVariation(percentageFormatter, payload.variationPercent)
        )
    }
}

@Composable
private fun WeeklyInactivityDetail(
    payload: NotificationPayload.WeeklyInactivity,
    dateFormatter: DateTimeFormatter
) {
    DetailSection(title = stringResource(R.string.notification_detail_section_activity)) {
        DetailRow(
            label = stringResource(R.string.notification_detail_days_without_tickets),
            value = payload.daysWithoutTicket.toString()
        )
        payload.lastTicketDate?.let {
            DetailRow(
                label = stringResource(R.string.notification_detail_last_ticket),
                value = it.format(dateFormatter)
            )
        }
    }
}

@Composable
private fun DetailSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = title,
            style = TicketScanTheme.typography.titleMedium,
            color = TicketScanTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = TicketScanTheme.typography.bodySmall,
            color = TicketScanTheme.colors.onSurfaceVariant
        )
        Text(
            text = value,
            style = TicketScanTheme.typography.bodyLarge,
            color = TicketScanTheme.colors.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun EmptyNotificationState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.notification_detail_empty_title),
            style = TicketScanTheme.typography.bodyLarge,
            color = TicketScanTheme.colors.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.notification_detail_empty_body),
            style = TicketScanTheme.typography.bodySmall,
            color = TicketScanTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

private fun formatDifference(currencyFormatter: NumberFormat, value: java.math.BigDecimal): String {
    val sign = if (value.signum() >= 0) "+" else "-"
    return "$sign${currencyFormatter.format(value.abs())}"
}

private fun formatVariation(percentageFormatter: NumberFormat, value: java.math.BigDecimal): String {
    val sign = if (value.signum() >= 0) "+" else "-"
    val formatted = percentageFormatter.format(value.abs())
    return "$sign$formatted%"
}
