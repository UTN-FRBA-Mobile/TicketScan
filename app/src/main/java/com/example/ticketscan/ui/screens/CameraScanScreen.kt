package com.example.ticketscan.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.io.File
import java.util.concurrent.Executor
import java.util.Locale
import com.example.ticketscan.ui.components.CaptureThumbnailPreview
import com.example.ticketscan.ui.components.ErrorBadge
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun CameraScanScreen(
    modifier: Modifier = Modifier,
    vm: CameraScanViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor: Executor = ContextCompat.getMainExecutor(context)

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val imageCapture = remember { ImageCapture.Builder().build() }
    val previewView = remember { PreviewView(context) }
    var cameraBound by remember { mutableStateOf(false) }

    LaunchedEffect(hasCameraPermission, lifecycleOwner) {
        if (hasCameraPermission) {
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
                cameraBound = true
            } catch (e: Exception) {
                Log.e("CameraScan", "Failed to bind camera", e)
            }
        }
    }

    val loading by vm.isLoading.collectAsState()
    val items by vm.items.collectAsState()
    val error by vm.error.collectAsState()
    val capturedThumbnail by vm.capturedThumbnail.collectAsState()
    val canContinue by vm.canContinue.collectAsState()
    val createdTicket by vm.createdTicket.collectAsState()

    createdTicket?.let { ticketId ->
        LaunchedEffect(ticketId) {
            navController.navigate("ticket/$ticketId")
            vm.onTicketNavigationHandled()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { previewView }
            )
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Se requiere permiso de cámara")
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
            }

            capturedThumbnail?.let { image ->
                CaptureThumbnailPreview(
                    image = image,
                    modifier = Modifier
                        .size(128.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (canContinue) Arrangement.SpaceEvenly else Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    enabled = hasCameraPermission && cameraBound && !loading,
                    onClick = {
                        val photoFile = File.createTempFile("ticketscan_", ".jpg", context.cacheDir)
                        val output = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                        imageCapture.takePicture(
                            output,
                            mainExecutor,
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onError(exception: ImageCaptureException) {
                                    Log.e("CameraScan", "Capture failed", exception)
                                    vm.onCaptureError(exception.message ?: "Error al capturar la imagen")
                                }

                                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                    vm.analyzeImage(photoFile)
                                }
                            }
                        )
                    }
                ) { Text("Capturar y analizar") }
                if (canContinue) {
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = { vm.saveTicket(items) }
                    ) { Text("Continuar") }
                }

            }

            if (error != null) {
                Spacer(Modifier.height(8.dp))
                ErrorBadge(
                    message = error?.let { "Error: $it" } ?: "Error desconocido",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            if (items.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text("Resultados", style = TicketScanTheme.typography.titleMedium)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                ) {
                    items(items) { it ->
                        ListItem(
                            headlineContent = { Text(it.name) },
                            supportingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = TicketScanIcons.categoryIcon(it.category.name),
                                        contentDescription = null,
                                        tint = TicketScanTheme.colors.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(TicketScanTheme.spacing.xs))
                                    Text(
                                        text = it.category.name,
                                        style = TicketScanTheme.typography.bodyMedium,
                                        color = TicketScanTheme.colors.onSurfaceVariant
                                    )
                                }
                            },
                            trailingContent = {
                                val priceText = "$" + String.format(Locale.US, "%.2f", it.price)
                                Text(priceText)
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}
