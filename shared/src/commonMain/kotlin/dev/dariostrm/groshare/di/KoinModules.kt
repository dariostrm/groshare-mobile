package dev.dariostrm.groshare.di

import com.russhwolf.settings.Settings
import dev.dariostrm.groshare.auth.authModule
import dev.dariostrm.groshare.getHttpClient
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

val appModule = module {
    single { getHttpClient(get(named(SettingsType.Secure))) }
}

val sharedModule = listOf(authModule, appModule)

expect val platformModule: PlatformModule

enum class SettingsType {
    Default,
    Secure
}
data class PlatformModule (
    val settings: Scope.() -> Settings,
    val secureSettings: Scope.() -> Settings,
)

fun initializePlatform() = module {
    single<Settings>(named(SettingsType.Default)) { platformModule.settings(this) }
    single<Settings>(named(SettingsType.Secure)) { platformModule.secureSettings(this) }
}