package dev.nathanmkaya.camera

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform