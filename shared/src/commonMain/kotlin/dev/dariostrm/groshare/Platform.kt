package dev.dariostrm.groshare

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform