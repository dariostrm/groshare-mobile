package dev.dariostrm.groshare.groceries

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val groceriesModule = module {
    single<GroceriesService> { GroceriesServiceImpl(get()) }
    viewModelOf(::GroceriesViewModel)
}