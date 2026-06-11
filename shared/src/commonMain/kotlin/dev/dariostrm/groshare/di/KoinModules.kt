package dev.dariostrm.groshare.di

import dev.dariostrm.groshare.auth.authModule
import org.koin.core.module.Module
import org.koin.dsl.module

val sharedModule = listOf(authModule)

expect val platformModule: Module