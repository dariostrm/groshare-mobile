package dev.dariostrm.groshare.auth

import dev.dariostrm.groshare.di.SettingsType
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authModule = module {
    single<AuthService> {
        AuthServiceImpl(
            get(),
            get(named(SettingsType.Secure))
        )
    }
    viewModelOf(::LoginViewModel)
}