package dev.dariostrm.groshare.di

import dev.dariostrm.groshare.SecureSettings
import dev.dariostrm.groshare.Settings
import dev.dariostrm.groshare.auth.authModule
import dev.dariostrm.groshare.getHttpClient
import eu.anifantakis.lib.ksafe.KSafe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.scope.Scope
import org.koin.dsl.module

val appModule = module {
    single { CoroutineScope(SupervisorJob() + Dispatchers.Default) } //appScope
    single { getHttpClient(get()) }
    single { SecureSettings(get(), get()) }
    single { Settings(get(), get()) }
}

val sharedModule = listOf(authModule, appModule)

expect val platformModule: PlatformModule

data class PlatformModule (
    val ksafe: Scope.() -> KSafe,
)

fun initializePlatform() = module {
    single<KSafe>() { platformModule.ksafe(this) }
}