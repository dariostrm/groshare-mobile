package dev.dariostrm.groshare.di

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext

actual val platformModule: PlatformModule = PlatformModule(
    settings = {
        val context = androidContext()
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        SharedPreferencesSettings(prefs)
    },
    secureSettings = {
        val context = androidContext()

        val securePrefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        SharedPreferencesSettings(securePrefs)

        /*
        val encPrefs = EncryptedSharedPreferences.create(...)
        SharedPreferencesSettings(encPrefs)
        */
    }
)