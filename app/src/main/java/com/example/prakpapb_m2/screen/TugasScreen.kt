package com.example.prakpapb_m2.screen

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.LifecycleOwner
import coil.compose.rememberImagePainter
import com.example.prakpapb_m2.data.model.local.TugasRepository
import com.example.prakpapb_m2.viewmodel.MainViewModel
import com.example.prakpapb_m2.viewmodel.MainViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun TugasScreen(tugasRepository: TugasRepository) {
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(tugasRepository))
    var matkul by remember { mutableStateOf("") }
    var detailTugas by remember { mutableStateOf("") }
    val tugasList by mainViewModel.tugasList.observeAsState(emptyList())

    var showCamera by remember { mutableStateOf(false) }
    var imageUris by remember { mutableStateOf(listOf<Uri>()) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                showCamera = true
            } else {
                Log.e("CameraPermission", "Camera permission denied")
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Title
        Text(
            text = "Tambah Tugas",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Input Fields
        Column {
            OutlinedTextField(
                value = matkul,
                onValueChange = { matkul = it },
                label = { Text("Matkul") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = detailTugas,
                onValueChange = { detailTugas = it },
                label = { Text("Detail Tugas") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Camera Preview and Capture
        if (showCamera) {
            CameraCapture(
                onImageCaptured = { uri ->
                    imageUris = imageUris + uri
                    showCamera = false
                },
                onError = { exc ->
                    Log.e("CameraCapture", "Error capturing image: ${exc.message}")
                },
                cameraExecutor = cameraExecutor
            )
        }

        // Buttons: Camera and Add in a row with equal width
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Camera")
            }

            Button(
                onClick = {
                    if (matkul.isNotEmpty() && detailTugas.isNotEmpty()) {
                        mainViewModel.addTugas(matkul, detailTugas)
                        matkul = ""
                        detailTugas = ""
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Add")
            }
        }

        // Display captured images in a LazyRow
        if (imageUris.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                items(imageUris) { uri ->
                    Image(
                        painter = rememberImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                    )
                }
            }
        }

        // List of Tasks with LazyColumn
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .weight(1f)
        ) {
            items(tugasList) { tugas ->
                var isDone by remember { mutableStateOf(tugas.selesai) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Matkul: ${tugas.matkul}", fontSize = 18.sp)
                            Text(text = "Detail: ${tugas.detailTugas}", fontSize = 16.sp)
                        }
                        if (!isDone) {
                            TextButton(
                                onClick = {
                                    isDone = true
                                }
                            ) {
                                Text(text = "Done")
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Done",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CameraCapture(
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    cameraExecutor: ExecutorService
) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    val previewView = remember { PreviewView(context) }

    LaunchedEffect(key1 = context) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build()

        preview.setSurfaceProvider(previewView.surfaceProvider)

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as LifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            onError(ImageCaptureException(ImageCapture.ERROR_UNKNOWN, "Failed to bind camera", exc))
        }
    }

    IconButton(
        onClick = {
            val photoFile = createImageFile(context)
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            imageCapture.takePicture(
                outputOptions,
                cameraExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        onImageCaptured(Uri.fromFile(photoFile))
                    }

                    override fun onError(exception: ImageCaptureException) {
                        onError(exception)
                    }
                }
            )
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Capture")
    }

    AndroidView(factory = { previewView })
}

fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}
