package dev.dariostrm.groshare.home

import dev.dariostrm.groshare.shared.MviViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

sealed interface HomeEvent {

}

val homeModule = module {
    viewModelOf(::HomeViewModel)
}

class HomeViewModel : MviViewModel<HomeState, HomeAction, HomeEvent>() {
    override fun setInitialState(): HomeState = HomeState

    override fun onAction(action: HomeAction) {

    }

}