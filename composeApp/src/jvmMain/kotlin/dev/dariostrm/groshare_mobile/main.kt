package dev.dariostrm.groshare_mobile

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        alwaysOnTop = true,
        title = "groshare_mobile",
    ) {
        App()
    }
}