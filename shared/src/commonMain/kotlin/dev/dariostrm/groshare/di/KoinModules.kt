package dev.dariostrm.groshare.di

import dev.dariostrm.groshare.settings.SecureSettings
import dev.dariostrm.groshare.settings.Settings
import dev.dariostrm.groshare.auth.authModule
import dev.dariostrm.groshare.debts.debtsModule
import dev.dariostrm.groshare.getHttpClient
import dev.dariostrm.groshare.groceries.groceriesModule
import dev.dariostrm.groshare.home.homeModule
import dev.dariostrm.groshare.shared.NetworkHealthStore
import dev.dariostrm.groshare.shared.NetworkHealthStoreImpl
import eu.anifantakis.lib.ksafe.KSafe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.scope.Scope
import org.koin.dsl.module

val appModule = module {
    single { CoroutineScope(SupervisorJob() + Dispatchers.Default) } //appScope
    single { getHttpClient(get(), get(), get()) }
    single { SecureSettings(get(), get()) }
    single { Settings(get(), get()) }
    single<NetworkHealthStore> { NetworkHealthStoreImpl() }
}

val sharedModule = listOf(
    homeModule,
    authModule,
    groceriesModule,
    debtsModule,
    appModule
)

expect val platformModule: PlatformModule

data class PlatformModule (
    val ksafe: Scope.() -> KSafe,
)

fun initializePlatform() = module {
    single<KSafe>() { platformModule.ksafe(this) }
}