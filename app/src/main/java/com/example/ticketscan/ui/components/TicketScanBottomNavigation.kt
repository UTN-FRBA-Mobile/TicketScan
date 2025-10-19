package com.example.ticketscan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import com.example.ticketscan.ui.theme.TicketScanThemeProvider

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
        containerColor = TicketScanTheme.colors.surfaceVariant
    ) {
        BottomNavItem.items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    // If selected, show a circular accent background behind icon
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(TicketScanTheme.colors.primary, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = TicketScanTheme.colors.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = TicketScanTheme.colors.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                label = { Text(item.label, color = if (isSelected) TicketScanTheme.colors.primary else TicketScanTheme.colors.onSurfaceVariant) },
                selected = isSelected,
                onClick = {
                    if (item is BottomNavItem.Scan && onScanClick != null) {
                        onScanClick()
                    } else {
                        navController.navigate(item.route)
                    }
                },
                alwaysShowLabel = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicketScanBottomNavigationPreview() {
    TicketScanThemeProvider {
        val navController = rememberNavController()
        TicketScanBottomNavigation(navController = navController)
    }
}
