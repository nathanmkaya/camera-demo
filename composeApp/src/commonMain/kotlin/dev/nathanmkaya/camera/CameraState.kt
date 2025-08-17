package dev.nathanmkaya.camera

import com.kashif.cameraK.controller.CameraController
import io.github.vinceglb.filekit.PlatformFile

data class CameraState(
    val hasCameraPermission: Boolean = false,
    val hasStoragePermission: Boolean = false,
    val cameraController: CameraController? = null,
    val currentImageFile: PlatformFile? = null,
    val capturedImagePaths: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)