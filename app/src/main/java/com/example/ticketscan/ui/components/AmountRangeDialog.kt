package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.util.Locale

@Composable
fun AmountRangeDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double?, Double?) -> Unit,
    initialMinAmount: Double? = null,
    initialMaxAmount: Double? = null
) {
    var minAmountText by remember { 
        mutableStateOf(initialMinAmount?.let { String.format(Locale.US, "%.2f", it) } ?: "")
    }
    var maxAmountText by remember { 
        mutableStateOf(initialMaxAmount?.let { String.format(Locale.US, "%.2f", it) } ?: "")
    }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar rango de montos") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = minAmountText,
                    onValueChange = { 
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            minAmountText = it
                        }
                    },
                    label = { Text("Monto mínimo") },
                    placeholder = { Text("0.00") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = maxAmountText,
                    onValueChange = { 
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            maxAmountText = it
                        }
                    },
                    label = { Text("Monto máximo") },
                    placeholder = { Text("0.00") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val min = minAmountText.takeIf { it.isNotBlank() }?.toDoubleOrNull()
                val max = maxAmountText.takeIf { it.isNotBlank() }?.toDoubleOrNull()
                onConfirm(min, max)
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
}

