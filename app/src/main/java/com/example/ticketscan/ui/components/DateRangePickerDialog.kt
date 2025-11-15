package com.example.ticketscan.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.util.Calendar
import java.util.Date

@Composable
fun DateRangePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Date?, Date?) -> Unit,
    initialStartDate: Date? = null,
    initialEndDate: Date? = null
) {
    val context = LocalContext.current
    var startDate by remember { mutableStateOf(initialStartDate) }
    var endDate by remember { mutableStateOf(initialEndDate) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val startCalendar = Calendar.getInstance().apply {
        time = startDate ?: Date()
    }
    val endCalendar = Calendar.getInstance().apply {
        time = endDate ?: Date()
    }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar rango de fechas") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { showStartPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Fecha inicio: ${startDate?.let { 
                            java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(it)
                        } ?: "No seleccionada"}"
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { showEndPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Fecha fin: ${endDate?.let { 
                            java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(it)
                        } ?: "No seleccionada"}"
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(startDate, endDate)
                onDismiss()
            }) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )

    if (showStartPicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                startDate = calendar.time
                showStartPicker = false
            },
            startCalendar.get(Calendar.YEAR),
            startCalendar.get(Calendar.MONTH),
            startCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showEndPicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                endDate = calendar.time
                showEndPicker = false
            },
            endCalendar.get(Calendar.YEAR),
            endCalendar.get(Calendar.MONTH),
            endCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}

