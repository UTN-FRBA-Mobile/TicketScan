package com.example.ticketscan.ui.screens

import android.content.Intent
import android.net.Uri
import com.example.ticketscan.ui.feedback.UserFeedbackManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.core.content.FileProvider
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.io.File

/**
 * Pantalla que muestra opciones para un ticket en PDF: descargar o compartir.
 * Mantiene la lógica de generación de PDF reutilizando la función exportTicketToPdf definida en TicketScreen.kt
 */
@Composable
fun PdfOptionsScreen(navController: NavController, viewModel: TicketViewModel) {
    val context = LocalContext.current
    val ticketState = viewModel.ticket.collectAsState()
    val ticket = ticketState.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Opciones de PDF",
            style = TicketScanTheme.typography.headlineSmall,
            color = TicketScanTheme.colors.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                // Descargar: genera el PDF usando la función existente.
                exportTicketToPdf(context, ticket)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Icon(imageVector = TicketScanIcons.Description, contentDescription = "Descargar", modifier = Modifier.size(20.dp))
            Text(text = "  Descargar")
        }

        Button(
            onClick = {
                // Compartir: asegurarse de que el PDF exista y lanzar intent
                if (ticket == null) {
                    UserFeedbackManager.showError("No hay ticket para compartir")
                    return@Button
                }

                val filename = "ticket_${ticket.id}.pdf"
                val file = File(context.filesDir, filename)
                if (!file.exists()) {
                    // Generar el PDF si no existe
                    exportTicketToPdf(context, ticket)
                }

                if (!file.exists()) {
                    UserFeedbackManager.showError("No se pudo generar el PDF para compartir")
                    return@Button
                }

                try {
                    val uri: Uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = "application/pdf"
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    val chooser = Intent.createChooser(shareIntent, "Compartir PDF")
                    context.startActivity(chooser)
                    UserFeedbackManager.showSuccess("PDF compartido correctamente")
                } catch (e: Exception) {
                    UserFeedbackManager.showError("Error compartiendo PDF: ${e.message ?: "Error desconocido"}")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Icon(imageVector = TicketScanIcons.Share, contentDescription = "Compartir", modifier = Modifier.size(20.dp))
            Text(text = "  Compartir")
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
        ) {
            Text(text = "Cerrar")
        }
    }
}
