package com.example.ticketscan.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.ia.internal.IAServiceImpl
import com.example.ticketscan.ui.components.UploadOption
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun RecordAudioScreen(
    navController: NavController,
    iaService: IAServiceImpl,
    repositoryViewModel: RepositoryViewModel = viewModel(),
    onResult: (List<TicketItem>) -> Unit
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = TicketScanTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title with audio icon
            Spacer(modifier = Modifier.height(64.dp))
            UploadOption(
                label = "Ingresar Audio",
                icon = TicketScanIcons.Audio,
                modifier = Modifier.size(160.dp),
                onClick = {}
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Other methods section
            Text(
                text = "Otros Métodos",
                style = LocalTextStyle.current.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                color = TicketScanTheme.colors.onBackground,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Other upload options
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                UploadOption(
                    label = "Cámara",
                    icon = TicketScanIcons.Camera,
                    onClick = { navController.navigate("record_audio") }
                )
                UploadOption(
                    label = "Texto",
                    icon = TicketScanIcons.Text,
                    onClick = { navController.navigate("record_audio") }
                )
            }

            // Recording controls
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (!isRecording) {
                        try {
                            val file = File(context.filesDir, "recordings/grabacion_${System.currentTimeMillis()}.aac")
                            file.parentFile?.mkdirs()  // Create directories if they don't exist
                            audioFile = file
                            
                            MediaRecorder(context).apply {
                                setAudioSource(MediaRecorder.AudioSource.MIC)
                                setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                                setOutputFile(file.absolutePath)
                                try {
                                    prepare()
                                    start()
                                    recorder = this
                                } catch (e: Exception) {
                                    release()
                                    throw e
                                }
                            }
                        } catch (e: Exception) {
                            // Handle any errors during initialization
                            e.printStackTrace()
                            // Reset state and show error to user
                            isRecording = false
                            audioFile = null
                            return@Button
                        }
                        isRecording = true
                    } else {
                        try {
                            recorder?.apply {
                                try {
                                    stop()
                                } catch (e: Exception) {
                                    // Handle case where stop fails (e.g., recording was too short)
                                    e.printStackTrace()
                                } finally {
                                    release()
                                }
                            }
                        } catch (e: Exception) {
                            // Handle any other errors during stop/release
                            e.printStackTrace()
                        }
                        recorder = null
                        isRecording = false
                    }
                },
                enabled = hasPermission,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(if (isRecording) "Detener grabación" else "Iniciar grabación")
            }

            Button(
                onClick = {
                    audioFile?.let { file ->
                        coroutineScope.launch {
                            val categories = withContext(Dispatchers.IO) {
                                repositoryViewModel.getAllCategories()
                            }
                            val result = withContext(Dispatchers.IO) {
                                iaService.analyzeTicketAudio(file, categories)
                            }
                            onResult(result)
                        }
                    }
                },
                enabled = audioFile != null && !isRecording,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Analizar audio")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
