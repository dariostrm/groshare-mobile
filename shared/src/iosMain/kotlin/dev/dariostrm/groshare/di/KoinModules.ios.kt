package dev.dariostrm.groshare.di

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeConfig

actual val platformModule: PlatformModule = PlatformModule(
    ksafe = { KSafe(config = KSafeConfig(appNamespace = "dev.dariostrm.groshare")) }
)