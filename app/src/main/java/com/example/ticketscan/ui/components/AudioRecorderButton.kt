package com.example.ticketscan.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ticketscan.ui.theme.TicketScanIcons

@Composable
fun AudioRecorderButton(
    isRecording: Boolean,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    Button(onClick = { if (isRecording) onStopRecording() else onStartRecording() }) {
        Icon(
            imageVector = if (isRecording) TicketScanIcons.Stop else TicketScanIcons.Mic,
            contentDescription = if (isRecording) "Detener grabación" else "Iniciar grabación"
        )
        Text(if (isRecording) "Detener" else "Grabar")
    }
}

