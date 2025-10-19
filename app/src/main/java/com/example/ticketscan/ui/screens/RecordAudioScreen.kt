package com.example.ticketscan.ui.screens

import android.Manifest
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    viewModel: RecordAudioViewModel = viewModel(),
    onBack: () -> Unit = { navController.navigateUp() }
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // State from ViewModel
    val isRecording by viewModel.isRecording.collectAsState()
    val hasAudioPermission by viewModel.hasAudioPermission.collectAsState()
    val items by viewModel.items.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val canContinue by viewModel.canContinue.collectAsState()
    val createdTicket by viewModel.createdTicket.collectAsState()
    
    // Local state
    var recorder: MediaRecorder? by remember { mutableStateOf(null) }
    
    // Handle navigation when ticket is created
    LaunchedEffect(createdTicket) {
        createdTicket?.let { ticketId ->
            navController.navigate("ticket/$ticketId")
            viewModel.onTicketNavigationHandled()
        }
    }
    
    // Request permission on first launch
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onPermissionResult(granted)
    }
    
    LaunchedEffect(Unit) {
        if (!viewModel.hasAudioPermission.value) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
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
                // Title with audio icon
                Spacer(modifier = Modifier.height(32.dp))
                UploadOption(
                    label = if (isRecording) "Grabando..." else "Grabar Audio",
                    icon = TicketScanIcons.Audio,
                    modifier = Modifier.size(160.dp),
                    onClick = {}
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Loading indicator
                if (isLoading) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Error message
                error?.let { errorMessage ->
                    ErrorBadge(
                        message = errorMessage,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Items list if available
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

                // Recording controls
                Button(
                    onClick = {
                        if (!isRecording) {
                            try {
                                val file = File(context.filesDir, "recordings/grabacion_${System.currentTimeMillis()}.aac")
                                file.parentFile?.mkdirs()
                                
                                MediaRecorder(context).apply {
                                    setAudioSource(MediaRecorder.AudioSource.MIC)
                                    setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                                    setOutputFile(file.absolutePath)
                                    try {
                                        prepare()
                                        start()
                                        recorder = this
                                        viewModel.onStartRecording(file)
                                    } catch (e: Exception) {
                                        release()
                                        viewModel.onRecordingError(e.message ?: "Error al iniciar la grabación")
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                viewModel.onRecordingError(e.message ?: "Error al configurar la grabación")
                            }
                        } else {
                            try {
                                recorder?.apply {
                                    try {
                                        stop()
                                        viewModel.onStopRecording()
                                    } catch (e: Exception) {
                                        viewModel.onRecordingError("Error al detener la grabación: ${e.message}")
                                    } finally {
                                        release()
                                    }
                                }
                            } catch (e: Exception) {
                                viewModel.onRecordingError("Error: ${e.message}")
                            } finally {
                                recorder = null
                                viewModel.onStopRecording()
                            }
                        }
                    },
                    enabled = hasAudioPermission && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(if (isRecording) "Detener grabación" else "Iniciar grabación")
                }

                if (canContinue) {
                    Button(
                        onClick = { viewModel.saveTicket() },
                        enabled = items.isNotEmpty() && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text("Guardar Ticket")
                    }
                }
            }
        }
    }
}
