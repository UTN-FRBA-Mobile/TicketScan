package com.example.ticketscan.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DateRangeFilterInputs(
    startDate: Date?,
    endDate: Date?,
    onStartDateChange: (Date?) -> Unit,
    onEndDateChange: (Date?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val startCalendar = Calendar.getInstance().apply {
        time = startDate ?: Date()
    }
    val endCalendar = Calendar.getInstance().apply {
        time = endDate ?: Date()
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val calendar = Calendar.getInstance().apply {
                                set(year, month, dayOfMonth)
                            }
                            onStartDateChange(calendar.time)
                        },
                        startCalendar.get(Calendar.YEAR),
                        startCalendar.get(Calendar.MONTH),
                        startCalendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
        ) {
            OutlinedTextField(
                value = startDate?.let { dateFormat.format(it) } ?: "",
                onValueChange = { },
                readOnly = true,
                label = { Text("Fecha desde") },
                placeholder = { Text("dd/mm/aaaa") },
                trailingIcon = {
                    Row {
                        if (startDate != null) {
                            IconButton(onClick = { onStartDateChange(null) }) {
                                Icon(
                                    imageVector = TicketScanIcons.Close,
                                    contentDescription = "Limpiar fecha desde"
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TicketScanTheme.colors.surface,
                    unfocusedContainerColor = TicketScanTheme.colors.surface
                )
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val calendar = Calendar.getInstance().apply {
                                set(year, month, dayOfMonth)
                            }
                            onEndDateChange(calendar.time)
                        },
                        endCalendar.get(Calendar.YEAR),
                        endCalendar.get(Calendar.MONTH),
                        endCalendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
        ) {
            OutlinedTextField(
                value = endDate?.let { dateFormat.format(it) } ?: "",
                onValueChange = { },
                readOnly = true,
                label = { Text("Fecha hasta") },
                placeholder = { Text("dd/mm/aaaa") },
                trailingIcon = {
                    Row {
                        if (endDate != null) {
                            IconButton(onClick = { onEndDateChange(null) }) {
                                Icon(
                                    imageVector = TicketScanIcons.Close,
                                    contentDescription = "Limpiar fecha hasta"
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TicketScanTheme.colors.surface,
                    unfocusedContainerColor = TicketScanTheme.colors.surface
                )
            )
        }
    }
}

