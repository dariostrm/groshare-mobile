package dev.dariostrm.groshare.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.getValue

abstract class MviViewModel<S, A> : ViewModel() {

    protected abstract val initialState: S

    private val _state by lazy { MutableStateFlow(initialState) }
    val state: StateFlow<S> by lazy { _state.asStateFlow() }

    abstract fun onAction(action: A)

    protected fun updateState(reducer: S.() -> S) {
        _state.update(reducer)
    }
}