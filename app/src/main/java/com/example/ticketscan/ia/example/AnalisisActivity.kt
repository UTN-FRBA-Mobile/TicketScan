package com.example.ticketscan.ia.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketscan.domain.AnalizedItem
import java.io.File

class AnalisisActivity : ComponentActivity() {
    private val viewModel: ExampleTicketScanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnalisisScreen(viewModel)
        }
    }
}

@Composable
fun AnalisisScreen(viewModel: ExampleTicketScanViewModel) {
    var resultado by remember { mutableStateOf<com.example.ticketscan.domain.Ticket?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            //TODO: reemplazar con el archivo real
            val imagenFile = File("/ruta/a/tu/imagen.jpg")
            viewModel.analizeImageTicket(imagenFile) { ticket ->
                resultado = ticket
            }
        }) {
            Text("Analizar Imagen")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            //TODO: reemplazar con el archivo real
            val audioFile = File("/ruta/a/tu/audio.mp3")
            viewModel.analizeAudioTicket(audioFile) { ticket ->
                resultado = ticket
            }
        }) {
            Text("Analizar Audio")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            //TODO: reemplazar con los items reales
            val items = mapOf("Pan" to 100.0, "Leche" to 200.0)
            viewModel.analizeItemsTicket(items) { ticket ->
                resultado = ticket
            }
        }) {
            Text("Analizar Items")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Resultados:")
        resultado?.items?.forEach {
            Text(text = "${it.name}: ${it.price} (${it.category})")
        }
    }
}
