package com.example.ticketscan.ui.screens

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun PhotoPreviewDialog(
    photoFile: File,
    onClose: () -> Unit
) {
    var bmp by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(photoFile) {
        bmp = try {
            android.graphics.BitmapFactory.decodeFile(photoFile.absolutePath)
        } catch (_: Throwable) {
            null
        }
    }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Previsualizar foto") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                bmp?.let { image ->
                    Image(
                        bitmap = image.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(220.dp),
                        alignment = Alignment.Center,
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                    Spacer(Modifier.size(8.dp))
                } ?: Text("No se puede mostrar la imagen")
            }
        },
        confirmButton = {
            Button(onClick = onClose) { Text("Cerrar") }
        }
    )
}

@Composable
fun AudioPreviewDialog(
    audioFile: File,
    onClose: () -> Unit
) {
    var player: MediaPlayer? by remember { mutableStateOf(null) }
    var isPlaying by remember { mutableStateOf(false) }

    DisposableEffect(audioFile) {
        val p = MediaPlayer()
        try {
            p.setDataSource(audioFile.absolutePath)
            p.prepare()
            p.setOnCompletionListener { isPlaying = false }
        } catch (_: Throwable) {
            // ignore prepare errors
        }
        player = p
        onDispose {
            try {
                player?.release()
            } catch (_: Throwable) {
            }
            player = null
        }
    }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Previsualizar audio") },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(audioFile.name)
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    player?.let {
                        if (isPlaying) {
                            it.pause()
                            isPlaying = false
                        } else {
                            try {
                                it.start()
                                isPlaying = true
                            } catch (_: Throwable) {
                                // ignore
                            }
                        }
                    }
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(if (isPlaying) "Pausar" else "Reproducir")
            }
        },
        confirmButton = {
            Button(onClick = onClose) { Text("Cerrar") }
        }
    )
}
