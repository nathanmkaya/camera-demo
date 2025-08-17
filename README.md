# Camera Sample App

A Kotlin Multiplatform Compose sample application demonstrating the integration of camera functionality and file management using modern libraries.

## Overview

This sample showcases how to build a cross-platform camera application with proper architecture patterns, state management, and file handling. The app demonstrates camera capture, image preview, gallery import, and local image management.

## Libraries Used

### 🎥 [CameraK](https://github.com/kashif-mehmood-km/CameraK)
- **Purpose**: Cross-platform camera functionality for KMP
- **Features**: Camera preview, image capture, permission handling
- **Version**: `0.0.12`

### 📁 [FileKit](https://github.com/vinceglb/FileKit)
- **Purpose**: Cross-platform file operations for KMP
- **Features**: File picker, file operations, platform file abstraction
- **Version**: `0.10.0`

## Architecture

The app follows **MVVM (Model-View-ViewModel)** architecture with clean separation of concerns:

```
├── CameraState.kt          # Centralized state management
├── CameraViewModel.kt      # Business logic & state management
├── PermissionManager.kt    # Permission handling abstraction
├── FileManager.kt          # File operations
├── CapturedImagePreview.kt # Image preview UI component
├── CameraControls.kt       # Camera controls UI component
└── Camera.kt               # Main camera composable
```

### Key Features

- **📸 Camera Functionality**: Live camera preview and image capture
- **🖼️ Gallery Import**: Import images from device gallery
- **💾 File Management**: Save and manage captured images
- **🔐 Permission Handling**: Proper camera and storage permissions
- **⚡ State Management**: Reactive UI with StateFlow
- **🎨 Modern UI**: Material 3 design with Compose Multiplatform
- **🚫 Error Handling**: User-friendly error messages via Snackbar

## Platform Support

- ✅ **Android** (API 24+)
- ✅ **iOS** (iOS 12+)

## Dependencies

```kotlin
// Camera functionality
implementation("io.github.kashif-mehmood-km:camerak:0.0.12")

// File operations
implementation("io.github.vinceglb:filekit:0.10.0")
implementation("io.github.vinceglb:filekit-compose:0.10.0")
implementation("io.github.vinceglb:filekit-coil:0.10.0")

// UI & Architecture
implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.9.2")
```

## Getting Started

1. **Clone the repository**
   ```bash
   git clone git@github.com:nathanmkaya/camera-demo.git
   cd camera-demo
   ```

2. **Build and run**
   ```bash
   # Android
   ./gradlew installDebug
   
   # iOS (requires Xcode)
   ./gradlew iosApp:iosSimulatorArm64Run
   ```

## Code Highlights

### State Management
```kotlin
data class CameraState(
    val hasCameraPermission: Boolean = false,
    val hasStoragePermission: Boolean = false,
    val cameraController: CameraController? = null,
    val currentImageFile: PlatformFile? = null,
    val capturedImagePaths: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### File Operations
```kotlin
class FileManager {
    suspend fun createImageFile(bytes: ByteArray): PlatformFile = withContext(Dispatchers.IO) {
        val file = createTempImageFile()
        file.write(bytes)
        file
    }
    
    fun createTempImageFile(): PlatformFile {
        return PlatformFile(FileKit.cacheDir, "${Uuid.random()}.${ImageFormat.JPEG.extension}")
    }
}
```

### Camera Integration
```kotlin
CameraPreview(
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
```

## Project Structure

This is a Kotlin Multiplatform project targeting Android and iOS:

* [/composeApp](./composeApp/src) contains the shared camera application code
  - [commonMain](./composeApp/src/commonMain/kotlin) - shared camera implementation
  - Platform-specific folders for any platform-specific implementations

* [/iosApp](./iosApp/iosApp) contains the iOS application entry point

## Learn More

- [CameraK Documentation](https://github.com/kashif-mehmood-km/CameraK)
- [FileKit Documentation](https://github.com/vinceglb/FileKit)
- [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)

## License

This sample project is for educational purposes. Please refer to individual library licenses for their respective terms.