package dev.dariostrm.groshare.groceries

import androidx.lifecycle.viewModelScope
import dev.dariostrm.groshare.shared.MviViewModel
import dev.dariostrm.groshare.shared.ifError
import dev.dariostrm.groshare.shared.ifOk
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

data class GroceriesState(
    val groceries: List<Grocery> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val networkError: String? = null,
)

class GroceriesViewModel(
    private val groceriesService: GroceriesService
) : MviViewModel<GroceriesState, GroceriesAction>() {

    override val initialState = GroceriesState()

    init {
        groceriesService.groceries.onEach { groceries ->
            updateState { copy(groceries = groceries, isLoading = false, isRefreshing = false, networkError = null) }
        }.launchIn(viewModelScope)
        groceriesService.errors.onEach { error ->
            updateState { copy(networkError = error, isLoading = false, isRefreshing = false) }
        }.launchIn(viewModelScope)
        groceriesService.startPolling()
    }

    override fun onCleared() {
        super.onCleared()
        groceriesService.stopPolling()
    }

    override fun onAction(action: GroceriesAction) {
        when (action) {
            is GroceriesAction.DeleteGrocery -> TODO()
            is GroceriesAction.Refresh -> {
                viewModelScope.launch {
                    updateState { copy(isRefreshing = true) }
                    groceriesService.refreshGroceries()
                    updateState { copy(isRefreshing = false) }
                }
            }
        }
    }

}