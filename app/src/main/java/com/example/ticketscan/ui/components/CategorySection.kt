package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.util.UUID

@Composable
fun CategorySection(
    modifier: Modifier = Modifier,
    category: Category,
    items: List<TicketItem>,
    categories: List<Category>,
    isEditable: Boolean = false,
    onItemChange: (id: UUID, name: String?, price: Double?, quantity: Int?, category: Category?) -> Unit = { _, _, _, _, _ -> },
    onItemDelete: (id: UUID) -> Unit = {},
) {
    var editingItem by remember { mutableStateOf<TicketItem?>(null) }

    Column(modifier = modifier) {
        Text(
            text = category.name,
            style = TicketScanTheme.typography.titleMedium,
            color = TicketScanTheme.colors.onBackground,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
        )

        items.forEach { item ->
            TicketItemCard(
                item = item,
                isEditable = isEditable,
                onEdit = { editingItem = item }
            )
        }

        if (editingItem != null) {
            EditItemDialog(
                initial = editingItem!!,
                categories = categories,
                onDismiss = { editingItem = null },
                onSave = { name, price, quantity, categoryValue ->
                    onItemChange(editingItem!!.id, name, price, quantity, categoryValue)
                    editingItem = null
                },
                onDelete = { id ->
                    onItemDelete(id)
                    editingItem = null
                }
            )
        }

        Spacer(Modifier.height(8.dp))
        val categoryTotal = items.sumOf { it.price * it.quantity }
        Text(
            text = "Total ${category.name}: $${"%.2f".format(categoryTotal)}",
            style = TicketScanTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.End
        )
    }
}

