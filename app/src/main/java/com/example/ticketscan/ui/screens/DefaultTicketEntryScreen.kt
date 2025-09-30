package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ticketscan.ui.components.SettingDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTicketEntryScreen(
    navController: NavController
) {
    var selectedEntryType by remember { mutableStateOf("Texto") }
    val entryTypes = listOf("Audio", "Imagen", "Texto")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Entrada por defecto",
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
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main content with centered dropdown
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Entry type selection
                SettingDropdown(
                    label = "Selecciona el tipo de entrada por defecto",
                    selectedOption = selectedEntryType,
                    options = entryTypes,
                    onOptionSelected = { selectedEntryType = it },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                )
            }
            
            // Help text
            Text(
                text = "Esta configuración se aplicará cuando crees un nuevo ticket.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}
