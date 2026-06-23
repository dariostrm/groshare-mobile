package dev.dariostrm.groshare.home

import dev.dariostrm.groshare.shared.MviViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::ProfileViewModel)
}

class HomeViewModel : MviViewModel<HomeState, HomeAction>() {
    override val initialState = HomeState

    override fun onAction(action: HomeAction) {

    }

}