package dev.dariostrm.groshare.di

import android.content.Context
import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeConfig
import org.koin.android.ext.koin.androidContext

actual val platformModule: PlatformModule = PlatformModule(
    ksafe = {
        val context = androidContext()
        val appContext = context.applicationContext
        KSafe(appContext, config = KSafeConfig(appNamespace = "dev.dariostrm.groshare"))
    },
)