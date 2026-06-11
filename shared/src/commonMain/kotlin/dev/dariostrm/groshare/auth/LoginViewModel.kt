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