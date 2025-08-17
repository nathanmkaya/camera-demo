package dev.nathanmkaya.camera

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.vinceglb.filekit.PlatformFile

@Composable
fun PreviewTile(
    imageFile: PlatformFile,
    onPreview: () -> Unit,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .padding(4.dp)
    ) {
        Box(
            Modifier
                .clickable(onClick = onPreview)
                .matchParentSize()
                .clip(RoundedCornerShape(8.dp))
                .border(2.dp, Color.LightGray, RoundedCornerShape(8.dp)),
        ) {
            AsyncImage(
                model = imageFile,
                contentDescription = "Captured Image",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        val crossInset = 0.dp
        FilledIconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = -crossInset, y = crossInset)
                .size(28.dp),
            shape = RoundedCornerShape(999.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color.Black.copy(alpha = 0.6f),
                contentColor = Color.White
            )
        ) {
            Icon(
                contentDescription = "Remove image",
                modifier = Modifier.size(18.dp),
                tint = Color.White,
                imageVector = Icons.Default.Close
            )
        }
    }
}