package dev.dariostrm.groshare.auth

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import dev.dariostrm.groshare.MviViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

sealed interface LoginEvent {
    data class LoggedIn(val email: String, val password: String) : LoginEvent
}

class LoginViewModel : MviViewModel<LoginState, LoginAction, LoginEvent>() {

    override fun setInitialState(): LoginState = LoginState()

    fun validate(): Boolean {
        val usernameError = state.value.username.text.toString().validateUsername()
        val passwordError = state.value.password.text.toString().validatePassword()
        if (usernameError != null || passwordError != null) {
            updateState { copy(usernameError = usernameError, passwordError = passwordError) }
            return false
        }
        return true
    }

    fun validateUsername() {
        val error = state.value.username.text.toString().validateUsername()
        if (error != null) {
            updateState { copy(usernameError = error) }
        }
    }

    fun validatePassword() {
        val error = state.value.password.text.toString().validatePassword()
        if (error != null) {
            updateState { copy(passwordError = error) }
        }
    }

    fun String.validatePassword(): String? {
        if (this.isBlank())
            return "The password is required."
        if (this.length < 8)
            return "The password must contain at least 8 characters"
        if (this.length > 64)
            return "The password may not exceed 64 characters."
        return null
    }
    private val USERNAME_REGEX = "^[a-zA-Z0-9._-]+$".toRegex()
    fun String.validateUsername(): String? {
        if (this.isBlank())
            return "The username is required."
        if (this.length < 3)
            return "The username must contain at least 3 characters"
        if (this.length > 30)
            return "The username may not exceed 30 characters."
        if (!this.matches(USERNAME_REGEX))
            return "Only letters, numbers, underscores (_), dots (.) and hyphens (-) are allowed"
        return null
    }

    override fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.Login -> {
                if (!validate())
                    return
                updateState { copy(isLoading = true) }
                viewModelScope.launch {
                    delay(3000.milliseconds) //login
                    updateState { copy(isLoading = false) }
                }
            }
            LoginAction.OnPasswordLostFocus -> validatePassword()
            LoginAction.OnUsernameLostFocus -> validateUsername()
        }
    }

    init {
        // 1. Clear Username error on edit
        snapshotFlow { state.value.username.text }
            .drop(1) // Ignore the initial empty load
            .onEach {
                if (state.value.usernameError != null) {
                    updateState { copy(usernameError = null) }
                }
            }
            .launchIn(viewModelScope)

        // 2. Clear Password error on edit
        snapshotFlow { state.value.password.text }
            .drop(1)
            .onEach {
                if (state.value.passwordError != null) {
                    updateState { copy(passwordError = null) }
                }
            }
            .launchIn(viewModelScope)
    }

}