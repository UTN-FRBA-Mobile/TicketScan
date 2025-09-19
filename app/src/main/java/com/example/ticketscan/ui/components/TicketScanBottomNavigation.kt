package com.example.ticketscan.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", TicketScanIcons.Home, "Inicio")
    object Expenses : BottomNavItem("expenses", TicketScanIcons.Expenses, "Mis Gastos")
    object Scan : BottomNavItem("scan", TicketScanIcons.Scan, "Escanear")
    object Profile : BottomNavItem("profile", TicketScanIcons.Profile, "Mi Perfil")
    object More : BottomNavItem("more", TicketScanIcons.More, "Más")
    companion object {
        val items = listOf(Home, Expenses, Scan, Profile, More)
    }
}

@Composable
fun TicketScanBottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier,
    onScanClick: (() -> Unit)? = null
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = TicketScanTheme.colors.surface
    ) {
        BottomNavItem.items.forEach { item ->
            val isSelected = currentRoute == item.route
            if (item is BottomNavItem.Scan) {
                // Render a larger circular central button
                NavigationBarItem(
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(color = TicketScanTheme.colors.primary, shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = TicketScanTheme.colors.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    label = { Text(item.label) },
                    selected = isSelected,
                    onClick = {
                        if (onScanClick != null) onScanClick()
                    },
                    alwaysShowLabel = false
                )
            } else {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) TicketScanTheme.colors.primary else Color.Gray
                        )
                    },
                    label = { Text(item.label) },
                    selected = isSelected,
                    onClick = { navController.navigate(item.route) },
                    alwaysShowLabel = true
                )
            }
        }
    }
}
