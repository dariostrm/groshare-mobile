package dev.dariostrm.groshare.di

import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences

actual val platformModule: PlatformModule = PlatformModule(
    settings = { PreferencesSettings(Preferences.userRoot().node("dev.dariostrm.groshare.settings")) },
    secureSettings = { PreferencesSettings(Preferences.userRoot().node("dev.dariostrm.groshare.secure")) }
)