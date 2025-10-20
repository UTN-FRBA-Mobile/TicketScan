package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.ticketscan.domain.model.AppearancePreferences
import com.example.ticketscan.domain.model.FontScale
import com.example.ticketscan.domain.model.ThemeMode
import com.example.ticketscan.ui.components.SettingDropdown
import com.example.ticketscan.ui.components.TicketScanCard
import com.example.ticketscan.ui.components.TicketScanCardStyle
import com.example.ticketscan.ui.components.TicketScanScreenContainer
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme

private data class ThemeOptionUi(val label: String, val mode: ThemeMode)
private data class FontOptionUi(val label: String, val scale: FontScale)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettingsScreen(
    navController: NavController,
    viewModel: AppearanceSettingsViewModel
) {
    val preferences by viewModel.uiState.collectAsState()

    AppearanceSettingsContent(
        navController = navController,
        preferences = preferences,
        onThemeSelected = viewModel::onThemeSelected,
        onFontSizeSelected = viewModel::onFontScaleSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppearanceSettingsContent(
    navController: NavController,
    preferences: AppearancePreferences,
    onThemeSelected: (ThemeMode) -> Unit,
    onFontSizeSelected: (FontScale) -> Unit
) {
    val themeOptions = remember {
        listOf(
            ThemeOptionUi(label = "Claro", mode = ThemeMode.LIGHT),
            ThemeOptionUi(label = "Oscuro", mode = ThemeMode.DARK)
        )
    }
    val fontOptions = remember {
        listOf(
            FontOptionUi(label = "Chica", scale = FontScale.SMALL),
            FontOptionUi(label = "Mediana", scale = FontScale.MEDIUM),
            FontOptionUi(label = "Grande", scale = FontScale.LARGE)
        )
    }

    val selectedThemeLabel = themeOptions.firstOrNull { it.mode == preferences.themeMode }?.label
        ?: themeOptions.first().label
    val selectedFontLabel = fontOptions.firstOrNull { it.scale == preferences.fontScale }?.label
        ?: fontOptions.first().label

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Apariencia",
                        style = TicketScanTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = TicketScanIcons.ArrowBack,
                            contentDescription = "Volver",
                            tint = TicketScanTheme.colors.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        TicketScanScreenContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.xl)
        ) {
            TicketScanCard(
                style = TicketScanCardStyle.Tonal,
                contentPadding = PaddingValues(TicketScanTheme.spacing.lg)
            ) {
                SettingDropdown(
                    label = "Tema",
                    selectedOption = selectedThemeLabel,
                    options = themeOptions.map { it.label },
                    onOptionSelected = { label ->
                        themeOptions.firstOrNull { it.label == label }?.let { onThemeSelected(it.mode) }
                    }
                )
            }

            TicketScanCard(
                style = TicketScanCardStyle.Tonal,
                contentPadding = PaddingValues(TicketScanTheme.spacing.lg)
            ) {
                SettingDropdown(
                    label = "Tamaño de letra",
                    selectedOption = selectedFontLabel,
                    options = fontOptions.map { it.label },
                    onOptionSelected = { label ->
                        fontOptions.firstOrNull { it.label == label }?.let { onFontSizeSelected(it.scale) }
                    }
                )
            }
        }
    }
}
