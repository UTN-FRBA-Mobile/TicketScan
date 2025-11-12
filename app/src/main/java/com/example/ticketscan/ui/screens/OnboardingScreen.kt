package com.example.ticketscan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel
) {
    val currentPageIndex by viewModel.currentPageIndex.collectAsState()
    val pages = viewModel.pages
    val currentPage = pages[currentPageIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Skip button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                viewModel.skipOnboarding()
                navController.navigate("home") {
                    popUpTo("onboarding") { inclusive = true }
                }
            }) {
                Text("Omitir")
            }
        }

        // Page content
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = currentPage.icon,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = TicketScanTheme.colors.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = currentPage.title,
                style = TicketScanTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = TicketScanTheme.colors.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = currentPage.description,
                style = TicketScanTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = TicketScanTheme.colors.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Page indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            pages.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentPageIndex) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == currentPageIndex) 
                                TicketScanTheme.colors.primary 
                            else 
                                Color.Gray.copy(alpha = 0.5f)
                        )
                        .padding(4.dp)
                )
                if (index < pages.size - 1) {
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!viewModel.isFirstPage) {
                TextButton(onClick = {
                    viewModel.previousPage()
                }) {
                    Text("Anterior")
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            if (viewModel.isLastPage) {
                Button(onClick = {
                    viewModel.completeOnboarding()
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }) {
                    Text("Comenzar")
                }
            } else {
                Button(onClick = {
                    viewModel.nextPage()
                }) {
                    Text("Siguiente")
                }
            }
        }
    }
}

