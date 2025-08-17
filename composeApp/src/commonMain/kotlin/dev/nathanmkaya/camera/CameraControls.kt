package dev.nathanmkaya.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CameraControls(
    onGalleryClick: () -> Unit,
    onCaptureClick: () -> Unit,
    isLoading: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            IconButton(onClick = onGalleryClick) {
                Icon(
                    modifier = Modifier.size(70.dp),
                    imageVector = Icons.Default.Photo,
                    contentDescription = "Open Gallery"
                )
            }
        }
        
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            ShutterButton(
                onClick = onCaptureClick,
                enabled = !isLoading
            )
        }
        
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            // Space for additional controls (e.g., camera switch, flash toggle)
        }
    }
}