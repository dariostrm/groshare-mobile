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

abstract class MviViewModel<S, A, E> : ViewModel() {

    protected abstract fun setInitialState(): S

    private val _state by lazy { MutableStateFlow(setInitialState()) }
    val state: StateFlow<S> by lazy { _state.asStateFlow() }

    private val _events = MutableSharedFlow<E>()
    val events: SharedFlow<E> = _events.asSharedFlow()

    abstract fun onAction(action: A)

    protected fun updateState(reducer: S.() -> S) {
        _state.update(reducer)
    }

    protected fun emitEvent(event: E) {
        viewModelScope.launch {
            sendEvent(event)
        }
    }

    protected suspend fun sendEvent(event: E) {
        _events.emit(event)
    }
}