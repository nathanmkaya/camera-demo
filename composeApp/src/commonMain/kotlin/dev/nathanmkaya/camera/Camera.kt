package dev.nathanmkaya.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashif.cameraK.enums.CameraLens
import com.kashif.cameraK.enums.Directory
import com.kashif.cameraK.enums.FlashMode
import com.kashif.cameraK.enums.ImageFormat
import com.kashif.cameraK.permissions.providePermissions
import com.kashif.cameraK.ui.CameraPreview
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher

@Composable
fun Camera(
    viewModel: CameraViewModel = viewModel()
) {
    val permissions = providePermissions()
    val permissionManager = remember { PermissionManager(permissions) }
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle permission requests
    if (!state.hasCameraPermission) {
        permissionManager.RequestCameraPermission(
            onGranted = { viewModel.updateCameraPermission(true) },
            onDenied = { viewModel.updateCameraPermission(false) }
        )
    }

    if (!state.hasStoragePermission) {
        permissionManager.RequestStoragePermission(
            onGranted = { viewModel.updateStoragePermission(true) },
            onDenied = { viewModel.updateStoragePermission(false) }
        )
    }

    // File picker for gallery images
    val launcher = rememberFilePickerLauncher(
        type = FileKitType.Image,
    ) { image ->
        viewModel.addImageFromGallery(image)
    }

    // Show error messages
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Camera Preview or Image Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.65f)
                    .clipToBounds()
            ) {
                state.currentImageFile?.let { imageFile ->
                    CapturedImagePreview(
                        imageFile = imageFile,
                        onDismiss = { viewModel.dismissImagePreview() }
                    )
                } ?: CameraPreview(
                    modifier = Modifier.matchParentSize(),
                    cameraConfiguration = {
                        setCameraLens(CameraLens.BACK)
                        setFlashMode(FlashMode.OFF)
                        setImageFormat(ImageFormat.JPEG)
                        setDirectory(Directory.PICTURES)
                    },
                    onCameraControllerReady = { controller ->
                        viewModel.setCameraController(controller)
                    }
                )
            }

            // Camera Roll
            CameraRoll(
                modifier = Modifier.padding(top = 16.dp).weight(1f),
                files = state.capturedImagePaths,
                onPreview = { imagePath -> viewModel.previewImage(imagePath) },
                onRemove = { imagePath -> viewModel.removeImage(imagePath) }
            )

            // Camera Controls
            CameraControls(
                onGalleryClick = { launcher.launch() },
                onCaptureClick = { viewModel.takePicture() },
                isLoading = state.isLoading
            )
        }
    }
}

