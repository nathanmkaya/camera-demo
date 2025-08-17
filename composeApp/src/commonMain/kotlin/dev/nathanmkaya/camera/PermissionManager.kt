package dev.nathanmkaya.camera

import androidx.compose.runtime.Composable
import com.kashif.cameraK.permissions.Permissions

class PermissionManager(private val permissions: Permissions) {

    fun hasCameraPermission(): Boolean = permissions.hasCameraPermission()

    fun hasStoragePermission(): Boolean = permissions.hasStoragePermission()

    @Composable
    fun RequestCameraPermission(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        permissions.RequestCameraPermission(
            onGranted = onGranted,
            onDenied = onDenied
        )
    }

    @Composable
    fun RequestStoragePermission(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        permissions.RequestStoragePermission(
            onGranted = onGranted,
            onDenied = onDenied
        )
    }
}