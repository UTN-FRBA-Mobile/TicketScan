package com.example.ticketscan.ui.screens

import android.Manifest
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ticketscan.ui.components.ErrorBadge
import com.example.ticketscan.ui.components.UploadOption
import com.example.ticketscan.ui.theme.TicketScanIcons
import java.io.File

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordAudioScreen(
    navController: NavController,
    vm: RecordAudioViewModel,
    onBack: () -> Unit = { navController.navigateUp() }
) {
    val context = LocalContext.current

    // State from ViewModel
    val isLoading by vm.isLoading.collectAsState()
    val items by vm.items.collectAsState()
    val error by vm.error.collectAsState()
    val canContinue by vm.canContinue.collectAsState()
    val createdTicket by vm.createdTicket.collectAsState()

    // Local UI state for recording
    var isRecording by remember { mutableStateOf(false) }
    var recorder: MediaRecorder? by remember { mutableStateOf(null) }
    var currentFile: File? by remember { mutableStateOf(null) }
    var lastRecordedFile by remember { mutableStateOf<File?>(null) }
    var pendingAudioFile by remember { mutableStateOf<File?>(null) }

    LaunchedEffect(createdTicket) {
        createdTicket?.let { ticketId ->
            navController.navigate("ticket/$ticketId")
            vm.onTicketNavigationHandled()
        }
    }

    var hasAudioPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasAudioPermission = granted }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grabar Audio") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                UploadOption(
                    label = if (isRecording) "Grabando..." else "Grabar Audio",
                    icon = TicketScanIcons.Audio,
                    modifier = Modifier.size(160.dp),
                    onClick = { /* decorative */ }
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                error?.let { errorMessage ->
                    ErrorBadge(
                        message = errorMessage,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                if (items.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        items(items) { item ->
                            ListItem(
                                headlineContent = { Text(item.name) },
                                supportingContent = { Text(item.category.name) },
                                trailingContent = {
                                    Text("$${String.format("%.2f", item.price)}")
                                }
                            )
                            Divider()
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            if (!isRecording) {
                                try {
                                    val file = File(context.filesDir, "recordings/grabacion_${System.currentTimeMillis()}.aac")
                                    file.parentFile?.mkdirs()
                                    val m = MediaRecorder(context)
                                    m.setAudioSource(MediaRecorder.AudioSource.MIC)
                                    m.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                                    m.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                                    m.setOutputFile(file.absolutePath)
                                    try {
                                        m.prepare()
                                        m.start()
                                        recorder = m
                                        currentFile = file
                                        isRecording = true
                                    } catch (e: Exception) {
                                        m.release()
                                        vm.onAnalyzeError(e.message ?: "Error al iniciar la grabación")
                                    }
                                } catch (e: Exception) {
                                    vm.onAnalyzeError(e.message ?: "Error al configurar la grabación")
                                }
                            } else {
                                try {
                                    recorder?.apply {
                                        try {
                                            stop()
                                        } catch (e: Exception) {
                                            // ignore stop errors
                                        } finally {
                                            release()
                                        }
                                    }
                                    isRecording = false
                                    recorder = null
                                    currentFile?.let { file ->
                                        vm.analyzeAudio(file)
                                        lastRecordedFile = file
                                    }
                                    currentFile = null
                                } catch (e: Exception) {
                                    vm.onAnalyzeError(e.message ?: "Error al detener la grabación")
                                }
                            }
                        },
                        enabled = /* no explicit permission check here, system will request */ !isLoading,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(if (isRecording) "Detener grabación" else "Grabar Audio")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            lastRecordedFile?.let { file -> pendingAudioFile = file }
                        },
                        enabled = !isRecording && lastRecordedFile != null,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp)
                    ) {
                        Text("Previsualizar")
                    }
                }

                if (canContinue) {
                    Button(
                        onClick = { vm.saveTicket(items) },
                        enabled = items.isNotEmpty() && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text("Guardar Ticket")
                    }
                }
            }

            pendingAudioFile?.let { file ->
                AudioPreviewDialog(
                    audioFile = file,
                    onClose = {
                        pendingAudioFile = null
                    }
                )
            }
        }
    }
}
