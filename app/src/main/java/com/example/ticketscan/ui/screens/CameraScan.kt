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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.io.File
import java.util.concurrent.Executor
import java.util.Locale

@Composable
fun CameraScan(
    modifier: Modifier = Modifier,
    vm: CameraScanViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalContext.current as LifecycleOwner
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

    LaunchedEffect(hasCameraPermission) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
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
                                }

                                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                    vm.analyzeImage(photoFile)
                                }
                            }
                        )
                    }
                ) { Text("Capturar y analizar") }
                Button(
                    enabled = items.isNotEmpty() && !loading,
                    onClick = { navController.navigate("ticket") }
                ) { Text("Continuar") }
            }

            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
            }

            if (items.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text("Resultados", style = MaterialTheme.typography.titleMedium)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                ) {
                    items(items) { it ->
                        ListItem(
                            headlineContent = { Text(it.name) },
                            supportingContent = { Text(it.category) },
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


