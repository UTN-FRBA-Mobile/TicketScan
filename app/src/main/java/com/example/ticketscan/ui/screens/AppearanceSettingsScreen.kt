package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ticketscan.ui.components.SettingDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettingsScreen(
    navController: NavController
) {
    var selectedTheme by remember { mutableStateOf("Claro") }
    var selectedFontSize by remember { mutableStateOf("Mediana") }

    val themeOptions = listOf("Claro", "Oscuro")
    val fontSizeOptions = listOf("Chica", "Mediana", "Grande")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Apariencia",
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Theme Selection
            SettingDropdown(
                label = "Tema",
                selectedOption = selectedTheme,
                options = themeOptions,
                onOptionSelected = { selectedTheme = it }
            )

            // Font Size Selection
            SettingDropdown(
                label = "Tamaño de letra",
                selectedOption = selectedFontSize,
                options = fontSizeOptions,
                onOptionSelected = { selectedFontSize = it }
            )
        }
    }
}
