package com.example.ticketscan.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ticketscan.R
import com.example.ticketscan.ui.theme.TicketScanTheme
import com.example.ticketscan.ui.theme.TicketScanThemeProvider
import kotlinx.coroutines.delay
import java.util.UUID

@Composable
fun ProcessingScreen(navController: NavController, mode: String) {
    LaunchedEffect(mode) {
        delay(1200)
        val generatedId = UUID.randomUUID().toString()
        navController.navigate("ticket/$generatedId") {
            popUpTo("home") { inclusive = false }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_carrito),
            contentDescription = "Carrito",
            modifier = Modifier.size(96.dp),
            colorFilter = ColorFilter.tint(TicketScanTheme.colors.primary)
        )
        Text(text = "Procesando $mode", modifier = Modifier.padding(top = 12.dp))
        CircularProgressIndicator()
    }
}

@Preview
@Composable
fun ProcessingScreenPreview() {
    TicketScanThemeProvider {
        val navController = rememberNavController()
        ProcessingScreen(navController = navController, mode = "texto")
    }
}