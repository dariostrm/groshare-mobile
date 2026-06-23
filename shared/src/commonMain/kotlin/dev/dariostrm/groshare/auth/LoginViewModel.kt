package dev.dariostrm.groshare.auth

import androidx.lifecycle.viewModelScope
import dev.dariostrm.groshare.shared.MviViewModel
import dev.dariostrm.groshare.shared.ifError
import dev.dariostrm.groshare.shared.ifOk
import kotlinx.coroutines.launch

data class LoginState(
    val username: String = "",
    val usernameError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val loginError: String? = null,
)

class LoginViewModel(
    private val authService: AuthService,
) : MviViewModel<LoginState, LoginAction>() {

    override val initialState = LoginState()

    override fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.Reset -> updateState { initialState }
            is LoginAction.Login -> {
                val uError = state.value.username.validateUsername()
                val pError = state.value.password.validatePassword()
                updateState {
                    copy(usernameError = uError, passwordError = pError)
                }
                if (uError != null || pError != null) return

                updateState { copy(isLoading = true, loginError = null) }
                viewModelScope.launch {
                    authService.login(state.value.username, state.value.password)
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