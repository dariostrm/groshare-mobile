package dev.dariostrm.groshare.debts

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val debtsModule = module {
    single<DebtsService> { DebtsServiceImpl(get()) }
    viewModelOf(::DebtsViewModel)
}
