@file:OptIn(ExperimentalTime::class)

package dev.nathanmkaya.camera

import com.kashif.cameraK.enums.ImageFormat
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class FileManager {
    
    suspend fun createImageFile(bytes: ByteArray): PlatformFile = withContext(Dispatchers.IO) {
        val file = createTempImageFile()
        file.write(bytes)
        file
    }

    fun createTempImageFile(): PlatformFile {
        val timestamp = generateTimestamp()
        return PlatformFile(FileKit.cacheDir, "IMG_${timestamp}.${ImageFormat.JPEG.extension}")
    }

    private fun generateTimestamp(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return now.format(LocalDateTime.Formats.ISO)
    }
}