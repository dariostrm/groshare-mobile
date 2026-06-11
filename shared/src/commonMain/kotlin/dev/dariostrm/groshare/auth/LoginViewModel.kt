package dev.dariostrm.groshare.auth

import androidx.lifecycle.viewModelScope
import dev.dariostrm.groshare.MviViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

sealed interface LoginEvent {
    data class LoggedIn(val email: String, val password: String) : LoginEvent
}

class LoginViewModel : MviViewModel<LoginState, LoginAction, LoginEvent>() {

    override fun setInitialState(): LoginState = LoginState()

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
                val uError = state.value.username.validateUsername()
                val pError = state.value.password.validatePassword()
                updateState {
                    copy(usernameError = uError, passwordError = pError)
                }
                if (uError != null || pError != null) return

                updateState { copy(isLoading = true) }
                viewModelScope.launch {
                    delay(3000.milliseconds) //login
                    updateState { copy(isLoading = false) }
                }
            }
            is LoginAction.PasswordChanged -> updateState { copy(password = action.password, passwordError = null) }
            LoginAction.PasswordLostFocus -> updateState { copy(passwordError = this.password.validatePassword()) }
            is LoginAction.UsernameChanged -> updateState { copy(username = action.username, usernameError = null) }
            LoginAction.UsernameLostFocus -> updateState { copy(usernameError = this.username.validateUsername()) }
        }
    }

}