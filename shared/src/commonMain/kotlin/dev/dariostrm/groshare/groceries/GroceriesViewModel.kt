package dev.dariostrm.groshare.groceries

import androidx.lifecycle.viewModelScope
import dev.dariostrm.groshare.shared.MviViewModel
import dev.dariostrm.groshare.shared.ifError
import dev.dariostrm.groshare.shared.ifOk
import kotlinx.coroutines.launch
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

sealed interface GroceriesEvent {
}

val groceriesModule = module {
    viewModelOf(::GroceriesViewModel)
}

class GroceriesViewModel() : MviViewModel<GroceriesState, GroceriesAction, GroceriesEvent>() {

    override fun setInitialState(): GroceriesState = GroceriesState

    override fun onAction(action: GroceriesAction) {

    }

}