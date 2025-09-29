package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ticketscan.ui.components.UploadCard
import com.example.ticketscan.ui.components.UploadOption
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = TicketScanTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                UploadOption(label = "Audio", icon = TicketScanIcons.Audio) { /* TODO */ }
                UploadOption(label = "Cámara", icon = TicketScanIcons.Camera) {
                    navController.navigate("scan")
                }
                UploadOption(label = "Texto", icon = TicketScanIcons.Text) { /* TODO */ }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Últimas cargas",
                style = TicketScanTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 16.dp),
                color = TicketScanTheme.colors.onBackground
            )
            // TODO: Add filter dropdown here
            Column(
                modifier = Modifier
                .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UploadCard(
                    title = "Por audio",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.navigate("ticket") }
                )
                UploadCard(
                    title = "Por texto",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.navigate("ticket") }
                )
                UploadCard(
                    title = "Por fotos",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.navigate("ticket") }
                )
            }
        }
    }
}

