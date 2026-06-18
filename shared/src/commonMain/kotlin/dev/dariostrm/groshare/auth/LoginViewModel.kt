package dev.dariostrm.groshare.auth

import androidx.lifecycle.viewModelScope
import dev.dariostrm.groshare.MviViewModel
import dev.dariostrm.groshare.ifError
import dev.dariostrm.groshare.ifOk
import dev.dariostrm.groshare.then
import kotlinx.coroutines.launch

sealed interface LoginEvent {
    data class LoggedIn(val username: String) : LoginEvent
}

class LoginViewModel(
    private val authService: AuthService,
) : MviViewModel<LoginState, LoginAction, LoginEvent>() {

    override fun setInitialState(): LoginState = LoginState()

    override fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.Login -> {
//                val uError = state.value.username.validateUsername()
//                val pError = state.value.password.validatePassword()
//                updateState {
//                    copy(usernameError = uError, passwordError = pError)
//                }
//                if (uError != null || pError != null) return

                updateState { copy(isLoading = true, loginError = null) }
                viewModelScope.launch {
                    authService.login(state.value.username, state.value.password)
                        .ifOk {
                            updateState { copy(isLoading = false, loginError = null) }
                            sendEvent(LoginEvent.LoggedIn(state.value.username))
                        }
                        .ifError { error -> updateState { copy(isLoading = false, loginError = error) } }
                }
            }
            is LoginAction.PasswordChanged -> updateState { copy(password = action.password, passwordError = null) }
            is LoginAction.PasswordLostFocus -> updateState { copy(passwordError = this.password.validatePassword()) }
            is LoginAction.UsernameChanged -> updateState { copy(username = action.username, usernameError = null) }
            is LoginAction.UsernameLostFocus -> updateState { copy(usernameError = this.username.validateUsername()) }
        }
    }

}