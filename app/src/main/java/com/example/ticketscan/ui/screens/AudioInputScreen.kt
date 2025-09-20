package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.components.AudioRecorderButton
import com.example.ticketscan.ui.theme.Typography
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AudioInputScreen() {
    val viewModel: AudioInputViewModel = hiltViewModel()
    val isRecording by viewModel.isRecording.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        AudioRecorderButton(
            isRecording = isRecording,
            onStartRecording = { viewModel.startRecording() },
            onStopRecording = { viewModel.stopRecording() }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ingresar ",
            style = Typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "{audio}",
            style = Typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Otros métodos",
            style = Typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Placeholders para otros métodos
            AudioRecorderButton(
                isRecording = false,
                onStartRecording = {},
                onStopRecording = {}
            )
            Spacer(modifier = Modifier.size(32.dp))
            AudioRecorderButton(
                isRecording = false,
                onStartRecording = {},
                onStopRecording = {}
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "{otro_metodo}", style = Typography.bodyMedium)
            Spacer(modifier = Modifier.size(32.dp))
            Text(text = "{otro_metodo}", style = Typography.bodyMedium)
        }
    }
}
