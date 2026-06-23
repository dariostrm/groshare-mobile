package dev.dariostrm.groshare.debts

import androidx.lifecycle.viewModelScope
import dev.dariostrm.groshare.shared.MviViewModel
import dev.dariostrm.groshare.shared.ifError
import dev.dariostrm.groshare.shared.ifOk
import kotlinx.coroutines.launch

data class DebtsState(
    val debts: Debts? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

sealed interface DebtsAction {
    data object Refresh : DebtsAction
    data class SettleDebt(val roommateId: Long, val amountCents: Long) : DebtsAction
    data object ErrorShown : DebtsAction
}

class DebtsViewModel(
    private val debtsService: DebtsService
) : MviViewModel<DebtsState, DebtsAction>() {

    override val initialState = DebtsState()

    init {
        loadDebts()
    }

    private fun loadDebts(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            if (isRefreshing) {
                updateState { copy(isRefreshing = true) }
            } else {
                updateState { copy(isLoading = true) }
            }

            debtsService.getDebts()
                .ifOk { data ->
                    updateState { copy(debts = data, isLoading = false, isRefreshing = false, error = null) }
                }
                .ifError { err ->
                    updateState { copy(error = err, isLoading = false, isRefreshing = false) }
                }
        }
    }

    override fun onAction(action: DebtsAction) {
        when (action) {
            is DebtsAction.Refresh -> loadDebts(isRefreshing = true)
            is DebtsAction.SettleDebt -> {
                viewModelScope.launch {
                    updateState { copy(isRefreshing = true) }
                    debtsService.settleDebt(action.roommateId, action.amountCents)
                        .ifOk {
                            loadDebts(isRefreshing = true)
                        }
                        .ifError { err ->
                            updateState { copy(error = err, isRefreshing = false) }
                        }
                }
            }
            is DebtsAction.ErrorShown -> updateState { copy(error = null) }
        }
    }
}
