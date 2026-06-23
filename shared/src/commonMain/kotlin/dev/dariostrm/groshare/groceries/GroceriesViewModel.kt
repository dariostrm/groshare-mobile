package dev.dariostrm.groshare.groceries

import androidx.lifecycle.viewModelScope
import dev.dariostrm.groshare.shared.MviViewModel
import dev.dariostrm.groshare.shared.ifError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GroceriesState(
    val groceries: List<Grocery> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val groceriesError: String? = null,
    val selectedGroceries: Set<Grocery> = emptySet(),
)

class GroceriesViewModel(
    private val groceriesService: GroceriesService
) : MviViewModel<GroceriesState, GroceriesAction>() {

    override val initialState = GroceriesState()

    init {
        groceriesService.groceries.onEach { groceries ->
            if (groceries == null) return@onEach
            updateState {
                copy(
                    groceries = groceries,
                    selectedGroceries = selectedGroceries.filter { it in groceries }.toSet(),
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }.launchIn(viewModelScope)
        groceriesService.error.onEach { error ->
            updateState { copy(groceriesError = error, isRefreshing = false, isLoading = if (error != null) false else isLoading) }
        }.launchIn(viewModelScope)
        groceriesService.startPolling()
    }

    override fun onCleared() {
        super.onCleared()
        groceriesService.stopPolling()
    }

    override fun onAction(action: GroceriesAction) {
        when (action) {
            is GroceriesAction.DeleteGrocery -> {
                viewModelScope.launch {
                    updateState { copy(isRefreshing = true) }
                    groceriesService.deleteGrocery(action.id)
                        .ifError { err -> updateState { copy(groceriesError = err) } }
                    updateState { copy(isRefreshing = false) }
                }
            }
            is GroceriesAction.Refresh -> {
                viewModelScope.launch {
                    updateState { copy(isRefreshing = true) }
                    groceriesService.refreshGroceries()
                    updateState { copy(isRefreshing = false) }
                }
            }
            is GroceriesAction.AddGrocery -> {
                viewModelScope.launch {
                    updateState { copy(isRefreshing = true) }
                    groceriesService.addGrocery(action.name)
                        .ifError { err -> updateState { copy(groceriesError = err) } }
                    updateState { copy(isRefreshing = false) }
                }
            }
            is GroceriesAction.GroceriesErrorShown -> updateState { copy(groceriesError = null) }
            is GroceriesAction.ToggleGrocerySelection -> {
                updateState {
                    copy(
                        selectedGroceries = if (action.grocery in selectedGroceries) {
                            selectedGroceries - action.grocery
                        } else {
                            selectedGroceries + action.grocery
                        }
                    )
                }
            }
            is GroceriesAction.BuyGroceries -> {
                viewModelScope.launch {
                    updateState { copy(isRefreshing = true) }
                    groceriesService.buyGroceries(action.purchases)
                        .ifError { err -> updateState { copy(groceriesError = err) } }
                    updateState { copy(isRefreshing = false) }
                }
            }
        }
    }
}