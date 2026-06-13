package dev.dariostrm.groshare.auth

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {
    single<AuthService> {
        AuthServiceImpl(
            get(),
            get()
        )
    }
    viewModelOf(::LoginViewModel)
}