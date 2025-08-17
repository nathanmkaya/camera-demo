@file:OptIn(ExperimentalTime::class)

package dev.nathanmkaya.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kashif.cameraK.controller.CameraController
import com.kashif.cameraK.enums.CameraLens
import com.kashif.cameraK.enums.Directory
import com.kashif.cameraK.enums.FlashMode
import com.kashif.cameraK.enums.ImageFormat
import com.kashif.cameraK.permissions.Permissions
import com.kashif.cameraK.permissions.providePermissions
import com.kashif.cameraK.result.ImageCaptureResult
import com.kashif.cameraK.ui.CameraPreview
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.copyTo
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun Camera() {
    // Initialize and check permissions
    val permissions: Permissions = providePermissions()
    val coroutineScope = rememberCoroutineScope()
    val cameraPermissionState = remember { mutableStateOf(permissions.hasCameraPermission()) }
    val storagePermissionState = remember { mutableStateOf(permissions.hasStoragePermission()) }

    // Request permissions if needed
    if (!cameraPermissionState.value) {
        permissions.RequestCameraPermission(
            onGranted = { cameraPermissionState.value = true },
            onDenied = { println("Camera Permission Denied") }
        )
    }

    if (!storagePermissionState.value) {
        permissions.RequestStoragePermission(
            onGranted = { storagePermissionState.value = true },
            onDenied = { println("Storage Permission Denied") }
        )
    }

    val cameraController = remember { mutableStateOf<CameraController?>(null) }
    var imageFile by remember { mutableStateOf<PlatformFile?>(null) }
    var capturedImagePaths by remember { mutableStateOf<List<String>>(emptyList()) }

    val launcher = rememberFilePickerLauncher(
        type = FileKitType.Image,
    ) { image ->
        val tempImage = createTempImageFile()
        CoroutineScope(Dispatchers.IO).launch{ image?.copyTo(tempImage) }
        imageFile = tempImage
        imageFile?.path.let { path ->
            capturedImagePaths = capturedImagePaths.plus(path.orEmpty())
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.65f)
                    .clipToBounds()
            ) {
                imageFile?.let { image ->
                    CapturedImagePreview(
                        imageFile = imageFile,
                        onDismiss = {
                            imageFile = null
                        }
                    )
                    capturedImagePaths.plus(image.path)
                } ?: CameraPreview(
                    modifier = Modifier.matchParentSize(),
                    cameraConfiguration = {
                        setCameraLens(CameraLens.BACK)
                        setFlashMode(FlashMode.OFF)
                        setImageFormat(ImageFormat.JPEG)
                        setDirectory(Directory.PICTURES)
                    },
                    onCameraControllerReady = { controller ->
                        cameraController.value = controller
                    }
                )
            }

            CameraRoll(
                modifier = Modifier.padding(top = 16.dp).weight(1f),
                files = capturedImagePaths,
                onPreview = { imageFile = PlatformFile(it) },
                onRemove = { file ->
                    capturedImagePaths = capturedImagePaths.filter { path -> path != file }
                    CoroutineScope(Dispatchers.IO).launch { PlatformFile(file).delete() }
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    IconButton(
                        modifier = Modifier,
                        onClick = {
                            launcher.launch()
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(70.dp),
                            imageVector = Icons.Default.Photo,
                            contentDescription = "Open Gallery"
                        )
                    }
                }
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    ShutterButton(
                        modifier = Modifier,
                        onClick = {
                            coroutineScope.launch {
                                when (val result = cameraController.value?.takePicture()) {
                                    is ImageCaptureResult.Success -> {
                                        // Handle the captured image
                                        imageFile = createImageFile(result.byteArray)
                                        imageFile?.path.let { path ->
                                            capturedImagePaths = capturedImagePaths.plus(path.orEmpty())
                                        }
                                    }

                                    is ImageCaptureResult.Error -> {
                                        println("Image Capture Error: ${result.exception.message}")
                                    }

                                    null -> Unit
                                }
                            }
                        }
                    )
                }
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {  }
            }
        }
    }
}

@Composable
private fun CapturedImagePreview(
    imageFile: PlatformFile?,
    onDismiss: () -> Unit
) {
    imageFile?.let { bitmap ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.9f)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = imageFile,
                    contentDescription = "Captured Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.6f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Preview",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                    )
                }
            }
        }

        LaunchedEffect(bitmap) {
            delay(3000)
            onDismiss()
        }
    }
}

fun createImageFile(bytes: ByteArray): PlatformFile {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val isoTimestamp = now.format(LocalDateTime.Formats.ISO)
    val file = PlatformFile(FileKit.cacheDir, "IMG_${isoTimestamp}.${ImageFormat.JPEG.extension}")
    CoroutineScope(Dispatchers.IO).launch {
        file.write(bytes)
    }
    return file
}

fun createTempImageFile(): PlatformFile {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val isoTimestamp = now.format(LocalDateTime.Formats.ISO)
    val file = PlatformFile(FileKit.cacheDir, "IMG_${isoTimestamp}.${ImageFormat.JPEG.extension}")
    return file
}