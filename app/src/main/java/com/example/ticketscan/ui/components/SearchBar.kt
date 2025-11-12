package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import kotlinx.coroutines.delay

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Buscar tickets..."
) {
    var textFieldValue by remember(query) { mutableStateOf(TextFieldValue(query)) }
    var debouncedQuery by remember { mutableStateOf(query) }

    // Debounce search query
    LaunchedEffect(textFieldValue.text) {
        delay(300) // 300ms debounce
        debouncedQuery = textFieldValue.text
        onQueryChange(debouncedQuery)
    }

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { textFieldValue = it },
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = {
            androidx.compose.material3.Icon(
                imageVector = TicketScanIcons.Search,
                contentDescription = "Buscar"
            )
        },
        trailingIcon = {
            if (textFieldValue.text.isNotEmpty()) {
                androidx.compose.material3.IconButton(onClick = {
                    textFieldValue = TextFieldValue("")
                    onQueryChange("")
                }) {
                    androidx.compose.material3.Icon(
                        imageVector = TicketScanIcons.Close,
                        contentDescription = "Limpiar búsqueda"
                    )
                }
            }
        },
        singleLine = true,
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedContainerColor = TicketScanTheme.colors.surface,
            unfocusedContainerColor = TicketScanTheme.colors.surface
        )
    )
}

