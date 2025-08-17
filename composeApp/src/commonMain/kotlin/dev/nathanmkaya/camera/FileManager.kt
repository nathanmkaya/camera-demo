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
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FileManager {

    suspend fun createImageFile(bytes: ByteArray): PlatformFile = withContext(Dispatchers.IO) {
        val file = createTempImageFile()
        file.write(bytes)
        file
    }

    @OptIn(ExperimentalUuidApi::class)
    fun createTempImageFile(): PlatformFile {
        return PlatformFile(FileKit.cacheDir, "${Uuid.random()}.${ImageFormat.JPEG.extension}")
    }
}