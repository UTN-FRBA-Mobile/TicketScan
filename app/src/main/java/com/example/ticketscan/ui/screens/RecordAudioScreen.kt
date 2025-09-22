package com.example.ticketscan.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arthenica.mobileffmpeg.FFmpeg
import androidx.compose.runtime.rememberCoroutineScope
import com.example.ticketscan.ia.internal.IAServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

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
                    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)
                    audioFile = file
                    recorder = MediaRecorder().apply {
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
                        val mp3File = convertAacToMp3(file, context)
                        if (mp3File != null) {
                            val result = withContext(Dispatchers.IO) {
                                iaService.analizeTicketAudio(mp3File)
                            }
                            onResult(result)
                        }
                    }
                }
            },
            enabled = audioFile != null && !isRecording
        ) {
            Text("Analizar audio")
        }
    }
}

private suspend fun convertAacToMp3(aacFile: File, context: Context): File? = withContext(Dispatchers.IO) {
    try {
        val mp3File = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), aacFile.name.replace(".aac", ".mp3"))
        val command = "-y -i ${aacFile.absolutePath} -codec:a libmp3lame -qscale:a 2 ${mp3File.absolutePath}"
        val rc = FFmpeg.execute(command)
        if (rc == 0 && mp3File.exists()) {
            mp3File
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
