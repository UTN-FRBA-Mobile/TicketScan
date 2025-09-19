package com.example.ticketscan.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ticketscan.domain.repositories.TicketRepositoryImpl
import com.example.ticketscan.ui.theme.TicketScanTheme
import com.example.ticketscan.ui.theme.TicketScanThemeProvider


@Composable
fun TicketScreen(
    navController: NavController,
) {
    val factory = remember { TicketViewModelFactory(TicketRepositoryImpl()) }
    val viewModel: TicketViewModel = viewModel(factory = factory)

    val categories by viewModel.categories.collectAsState()

    TicketScanThemeProvider {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Ticket ###",
                    style = TicketScanTheme.typography.headlineLarge,
                    color = TicketScanTheme.colors.onBackground
                )
                Text(
                    text = "Fecha xx/xx/xxxx",
                    style = TicketScanTheme.typography.bodyLarge,
                    color = TicketScanTheme.colors.onBackground
                )
            }

            Spacer(Modifier.height(20.dp))

            categories.forEach { category ->
                Text(
                    text = category.name,
                    style = TicketScanTheme.typography.titleMedium,
                    color = TicketScanTheme.colors.onBackground,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                CategorySection(category = category, navController = navController)
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.weight(1f)) // push actions to bottom

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                androidx.compose.material3.IconButton(onClick = { /* eliminar */ }) {
                    androidx.compose.material3.Icon(
                        imageVector = com.example.ticketscan.ui.theme.TicketScanIcons.Close,
                        contentDescription = "Eliminar",
                        tint = TicketScanTheme.colors.onBackground,
                        modifier = Modifier.size(40.dp)
                    )
                }
                androidx.compose.material3.IconButton(onClick = { /* editar */ }) {
                    androidx.compose.material3.Icon(
                        imageVector = com.example.ticketscan.ui.theme.TicketScanIcons.Edit,
                        contentDescription = "Editar",
                        tint = TicketScanTheme.colors.onBackground,
                        modifier = Modifier.size(40.dp)
                    )
                }
                androidx.compose.material3.IconButton(onClick = { /* compartir */ }) {
                    androidx.compose.material3.Icon(
                        imageVector = com.example.ticketscan.ui.theme.TicketScanIcons.Share,
                        contentDescription = "Compartir",
                        tint = TicketScanTheme.colors.onBackground,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CategorySection(category: Category, navController: NavController) {
    Text(category.name)
    Column {
        category.items.forEach { item ->
            ItemRow(item, navController)
        }
    }
}

@Composable
fun ItemRow(item: Item, navController: NavController) {
    Row(
        modifier = Modifier
            .height(52.dp)
            .fillMaxWidth()
    ) {
        androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
        androidx.compose.material3.Surface(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
            color = TicketScanTheme.colors.surface,
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    item.name,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = item.route != null) {
                            item.route?.let { navController.navigate(it) }
                        }
                )
                IntTextField()
            }
        }
    }
}

@Composable
fun IntTextField() {
    var text by remember { mutableStateOf("0.0") }
    val number: Int? = text.toIntOrNull() // null si no es número válido

    TextField(
        value = text,
        onValueChange = { newText -> text = newText },
        label = { Text(text) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Preview(showBackground = true)
@Composable
fun TicketScreenPreview() {
    val navController = rememberNavController()
    TicketScreen(navController = navController)
}

data class Category(
    val name: String,
    val items: List<Item>
)

data class Item(
    val name: String,
    val quantity: Float,
    val route: String? = null // opcional, por si querés redirigir
)