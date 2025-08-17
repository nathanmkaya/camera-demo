package dev.nathanmkaya.camera

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.vinceglb.filekit.PlatformFile

@Composable
fun CameraRoll(
    modifier: Modifier = Modifier,
    files: List<String>,
    onPreview: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    LazyRow(
        modifier = modifier
    ) {
        items(files.size) { index ->
            PreviewTile(
                imageFile = PlatformFile(files[index]),
                onPreview = { onPreview(files[index]) },
                onRemove = { onRemove(files[index]) }
            )
        }
    }
}