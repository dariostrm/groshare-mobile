package dev.dariostrm.groshare.di

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.NSUserDefaultsSettings
import platform.Foundation.NSUserDefaults

@OptIn(ExperimentalSettingsImplementation::class)
actual val platformModule: PlatformModule = PlatformModule(
    settings = { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) },
    secureSettings = { KeychainSettings() }
)