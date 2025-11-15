package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.util.Locale

@Composable
fun AmountRangeFilterInputs(
    minAmount: Double?,
    maxAmount: Double?,
    onMinAmountChange: (Double?) -> Unit,
    onMaxAmountChange: (Double?) -> Unit,
    modifier: Modifier = Modifier
) {
    var minAmountText by remember(minAmount) {
        mutableStateOf(minAmount?.let { String.format(Locale.US, "%.2f", it) } ?: "")
    }
    var maxAmountText by remember(maxAmount) {
        mutableStateOf(maxAmount?.let { String.format(Locale.US, "%.2f", it) } ?: "")
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = minAmountText,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    minAmountText = newValue
                    onMinAmountChange(newValue.toDoubleOrNull())
                }
            },
            label = { Text("Monto mínimo") },
            placeholder = { Text("0.00") },
            prefix = { Text("$") },
            trailingIcon = {
                if (minAmountText.isNotEmpty()) {
                    IconButton(onClick = {
                        minAmountText = ""
                        onMinAmountChange(null)
                    }) {
                        Icon(
                            imageVector = TicketScanIcons.Close,
                            contentDescription = "Limpiar monto mínimo"
                        )
                    }
                }
            },
            modifier = Modifier.weight(1f),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedContainerColor = TicketScanTheme.colors.surface,
                unfocusedContainerColor = TicketScanTheme.colors.surface
            )
        )

        OutlinedTextField(
            value = maxAmountText,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    maxAmountText = newValue
                    onMaxAmountChange(newValue.toDoubleOrNull())
                }
            },
            label = { Text("Monto máximo") },
            placeholder = { Text("0.00") },
            prefix = { Text("$") },
            trailingIcon = {
                if (maxAmountText.isNotEmpty()) {
                    IconButton(onClick = {
                        maxAmountText = ""
                        onMaxAmountChange(null)
                    }) {
                        Icon(
                            imageVector = TicketScanIcons.Close,
                            contentDescription = "Limpiar monto máximo"
                        )
                    }
                }
            },
            modifier = Modifier.weight(1f),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedContainerColor = TicketScanTheme.colors.surface,
                unfocusedContainerColor = TicketScanTheme.colors.surface
            )
        )
    }
}

