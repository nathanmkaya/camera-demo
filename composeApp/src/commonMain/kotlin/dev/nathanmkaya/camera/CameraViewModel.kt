package dev.nathanmkaya.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashif.cameraK.controller.CameraController
import com.kashif.cameraK.result.ImageCaptureResult
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.copyTo
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel : ViewModel() {
    private val _state = MutableStateFlow(CameraState())
    val state: StateFlow<CameraState> = _state.asStateFlow()

    private val fileManager = FileManager()

    fun updateCameraPermission(granted: Boolean) {
        _state.update { it.copy(hasCameraPermission = granted) }
    }

    fun updateStoragePermission(granted: Boolean) {
        _state.update { it.copy(hasStoragePermission = granted) }
    }

    fun setCameraController(controller: CameraController) {
        _state.update { it.copy(cameraController = controller) }
    }

    fun takePicture() {
        val controller = _state.value.cameraController ?: return

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = controller.takePicture()
            when (result) {
                is ImageCaptureResult.Success -> {
                    try {
                        val imageFile = fileManager.createImageFile(result.byteArray)
                        _state.update { currentState ->
                            currentState.copy(
                                currentImageFile = imageFile,
                                capturedImagePaths = currentState.capturedImagePaths + imageFile.path,
                                isLoading = false
                            )
                        }
                    } catch (e: Exception) {
                        _state.update {
                            it.copy(
                                error = "Failed to save image: ${e.message}",
                                isLoading = false
                            )
                        }
                    }
                }

                is ImageCaptureResult.Error -> {
                    _state.update {
                        it.copy(
                            error = "Image capture failed: ${result.exception.message}",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun addImageFromGallery(imageFile: PlatformFile?) {
        imageFile ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tempImage = fileManager.createTempImageFile()
                imageFile.copyTo(tempImage)
                _state.update { currentState ->
                    currentState.copy(
                        currentImageFile = tempImage,
                        capturedImagePaths = currentState.capturedImagePaths + tempImage.path
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to import image: ${e.message}"
                    )
                }
            }
        }
    }

    fun previewImage(imagePath: String) {
        _state.update { it.copy(currentImageFile = PlatformFile(imagePath)) }
    }

    fun dismissImagePreview() {
        _state.update { it.copy(currentImageFile = null) }
    }

    fun removeImage(imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                PlatformFile(imagePath).delete()
                _state.update { currentState ->
                    currentState.copy(capturedImagePaths = currentState.capturedImagePaths.filter { it != imagePath })
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to delete image: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}