package dev.dariostrm.groshare.shared

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface NetworkHealthStore {
    val networkHealth: StateFlow<Boolean>

    fun reportOnline()
    fun reportOffline()
}

class NetworkHealthStoreImpl : NetworkHealthStore {
    private val _networkHealth = MutableStateFlow(true)
    override val networkHealth = _networkHealth.asStateFlow()

    override fun reportOnline() {
        _networkHealth.value = true
    }

    override fun reportOffline() {
        _networkHealth.value = false
    }
}