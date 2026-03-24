package dev.dariostrm.groshare_mobile

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform