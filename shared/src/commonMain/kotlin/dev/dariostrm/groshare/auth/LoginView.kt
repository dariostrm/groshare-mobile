package dev.dariostrm.groshare.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import groshare.shared.generated.resources.Res
import groshare.shared.generated.resources.ic_visibility_filled
import groshare.shared.generated.resources.ic_visibility_off_filled
import org.jetbrains.compose.resources.painterResource

data class LoginState(
    val username: TextFieldState = TextFieldState(),
    val usernameError: String? = null,
    val password: TextFieldState = TextFieldState(),
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val loginError: String? = null,
)

sealed interface LoginAction {
    data object OnUsernameLostFocus : LoginAction
    data object OnPasswordLostFocus : LoginAction
    data object Login : LoginAction
}

@Composable
fun LoginView(
    viewModel: LoginViewModel,
    onLoggedIn: (username: String) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    LoginComponent(
        state = state,
        onAction = viewModel::onAction,
        onLoggedIn = onLoggedIn
    )
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    val state = LoginState(
        password = TextFieldState("test"),
    )

    LoginComponent(state, onAction = {}, onLoggedIn = {})
}

@Composable
fun LoginComponent(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    onLoggedIn: (username: String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.titleLarge,
        )

        OutlinedTextField(
            state = state.username,
            label = { Text(text = "Username") },
            isError = state.usernameError != null,
            modifier = Modifier.onFocusChanged { e ->
                if (!e.isFocused)
                    onAction(LoginAction.OnUsernameLostFocus)
            },
            supportingText = {
                if (state.usernameError != null) {
                    Text(
                        text = state.usernameError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )

        var showPassword by remember { mutableStateOf(false) }
        OutlinedSecureTextField(
            state = state.password,
            label = { Text(text = "Password") },
            modifier = Modifier.onFocusChanged { e ->
                if (!e.isFocused)
                    onAction(LoginAction.OnPasswordLostFocus)
            },
            isError = state.passwordError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textObfuscationMode =
                if (showPassword) {
                    TextObfuscationMode.Visible
                } else {
                    TextObfuscationMode.RevealLastTyped
                },
            supportingText = {
                if (state.passwordError != null) {
                    Text(
                        text = state.passwordError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = { showPassword = !showPassword },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                ) {
                    Icon(
                        painter = painterResource(
                            if (showPassword) Res.drawable.ic_visibility_filled
                            else Res.drawable.ic_visibility_off_filled
                        ),
                        contentDescription = "Toggle password visibility"
                    )
                }
            }
        )

        Button(
            onClick = { onAction(LoginAction.Login) },
            enabled = state.loginError == null &&
                    state.usernameError == null &&
                    state.passwordError == null,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        trackColor = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(text = "Login")
            }
        }

        if (state.loginError != null) {
            Text(text = state.loginError, color = MaterialTheme.colorScheme.error)
        }
    }
}