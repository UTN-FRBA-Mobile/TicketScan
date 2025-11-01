package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ticketscan.R
import com.example.ticketscan.domain.model.notifications.NotificationPayload
import com.example.ticketscan.domain.model.notifications.NotificationType
import com.example.ticketscan.ui.components.ComparisonSection
import com.example.ticketscan.ui.components.TicketScanCard
import com.example.ticketscan.ui.components.TicketScanCardStyle
import com.example.ticketscan.ui.components.TicketScanScreenContainer
import com.example.ticketscan.ui.components.TicketScanSectionHeader
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyComparisonScreen(
    navController: NavController,
    navigationViewModel: NotificationNavigationViewModel
) {
    val payloadMap by navigationViewModel.latestByType.collectAsState()
    val payload = payloadMap[NotificationType.MONTHLY_COMPARISON] as? NotificationPayload.MonthlyComparison
    val currency = NumberFormat.getCurrencyInstance(Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.notification_monthly_comparison_title),
                        style = TicketScanTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = TicketScanIcons.ArrowBack,
                            contentDescription = stringResource(R.string.notification_back_content_description)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (payload == null) {
            TicketScanScreenContainer(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                enableVerticalScroll = false
            ) {
                Text(
                    text = stringResource(R.string.notification_detail_empty_title),
                    style = TicketScanTheme.typography.bodyLarge,
                    color = TicketScanTheme.colors.onSurface
                )
                Text(
                    text = stringResource(R.string.notification_detail_empty_body),
                    style = TicketScanTheme.typography.bodySmall,
                    color = TicketScanTheme.colors.onSurfaceVariant
                )
            }
            return@Scaffold
        }

        TicketScanScreenContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TicketScanSectionHeader(
                title = stringResource(R.string.notification_monthly_comparison_title),
                subtitle = stringResource(R.string.notification_detail_section_monthly)
            )

            TicketScanCard(
                style = TicketScanCardStyle.Tonal,
                contentPadding = PaddingValues(TicketScanTheme.spacing.lg)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.notification_detail_current_month),
                        style = TicketScanTheme.typography.titleMedium,
                        color = TicketScanTheme.colors.onSurface
                    )
                    Text(
                        text = currency.format(payload.currentTotal),
                        style = TicketScanTheme.typography.displaySmall,
                        color = TicketScanTheme.colors.onSurface
                    )
                    Text(
                        text = stringResource(
                            R.string.notification_detail_previous_month_value,
                            currency.format(payload.previousTotal)
                        ),
                        style = TicketScanTheme.typography.bodyMedium,
                        color = TicketScanTheme.colors.onSurfaceVariant
                    )
                }
            }

            TicketScanCard(
                style = TicketScanCardStyle.Tonal,
                contentPadding = PaddingValues(TicketScanTheme.spacing.lg)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.notification_detail_difference),
                        style = TicketScanTheme.typography.titleMedium,
                        color = TicketScanTheme.colors.onSurface
                    )
                    Text(
                        text = currency.format(payload.difference),
                        style = TicketScanTheme.typography.headlineLarge,
                        color = TicketScanTheme.colors.onSurface
                    )
                }
            }

            TicketScanCard(
                style = TicketScanCardStyle.Tonal,
                contentPadding = PaddingValues(TicketScanTheme.spacing.lg)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.notification_detail_variation),
                        style = TicketScanTheme.typography.titleMedium,
                        color = TicketScanTheme.colors.onSurface
                    )
                    ComparisonSection(
                        current = payload.currentTotal,
                        previous = payload.previousTotal
                    )
                }
            }
        }
    }
}
