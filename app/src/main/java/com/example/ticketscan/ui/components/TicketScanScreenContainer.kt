package com.example.ticketscan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ticketscan.ui.theme.TicketScanTheme

/**
 * Standard outer layout for TicketScan screens to keep spacing and background consistent.
 */
@Composable
fun TicketScanScreenContainer(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = TicketScanTheme.spacing.xl,
        vertical = TicketScanTheme.spacing.xl
    ),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(TicketScanTheme.spacing.lg),
    enableVerticalScroll: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val baseModifier = modifier
        .fillMaxSize()
        .background(TicketScanTheme.colors.background)

    val scrollState = rememberScrollState()
    val decoratedModifier = if (enableVerticalScroll) {
        baseModifier.verticalScroll(scrollState)
    } else {
        baseModifier
    }

    Column(
        modifier = decoratedModifier.padding(contentPadding),
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}
