package com.example.ticketscan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import androidx.compose.material3.IconButton

@Composable
fun ProfileHeader(
    name: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onEditClick: () -> Unit = {}
) {
    // Gradient background using theme primary and secondary for contrast
    val gradient = Brush.linearGradient(
        colors = listOf(TicketScanTheme.colors.primary, TicketScanTheme.colors.secondary)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(TicketScanTheme.colors.onPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    // Placeholder icon inside avatar
                    Icon(
                        imageVector = TicketScanIcons.Profile,
                        contentDescription = "Avatar",
                        tint = TicketScanTheme.colors.onPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        color = TicketScanTheme.colors.onPrimary,
                        fontSize = 20.sp
                    )
                    subtitle?.let {
                        Text(
                            text = it,
                            color = TicketScanTheme.colors.onPrimary.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }
                }

                // Edit button
                IconButton(onClick = onEditClick) {
                    Surface(
                        shape = CircleShape,
                        color = TicketScanTheme.colors.primaryContainer
                    ) {
                        Box(modifier = Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = TicketScanIcons.Edit,
                                contentDescription = "Editar",
                                tint = TicketScanTheme.colors.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
