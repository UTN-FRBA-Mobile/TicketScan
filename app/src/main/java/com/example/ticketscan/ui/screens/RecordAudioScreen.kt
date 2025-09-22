package com.example.ticketscan.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.ticketscan.ia.internal.IAServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun RecordAudioScreen(
    iaService: IAServiceImpl,
    baseUrl: String,
    onResult: (List<com.example.ticketscan.ia.AnalizedItem>) -> Unit
) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var recorder: MediaRecorder? by remember { mutableStateOf(null) }
    var audioFile by remember { mutableStateOf<File?>(null) }
    var hasPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Grabar nota de voz para registrar compra", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (!isRecording) {
                    val fileName = "ticket_audio_${System.currentTimeMillis()}.aac"
                    //val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)
                    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "grabacion_test.aac")
                    audioFile = file
                    recorder = MediaRecorder(context).apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                        setOutputFile(file.absolutePath)
                        prepare()
                        start()
                    }
                    isRecording = true
                } else {
                    recorder?.apply {
                        stop()
                        release()
                    }
                    recorder = null
                    isRecording = false
                }
            },
            enabled = hasPermission
        ) {
            Text(if (isRecording) "Detener grabación" else "Iniciar grabación")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                audioFile?.let { file ->
                    coroutineScope.launch {
                        val result = withContext(Dispatchers.IO) {
                            iaService.analizeTicketAudio(file)
                        }
                        onResult(result)
                    }
                }
            },
            enabled = audioFile != null && !isRecording
        ) {
            Text("Analizar audio")
        }

    }
}

