package dev.dariostrm.groshare.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import groshare.shared.generated.resources.Res
import groshare.shared.generated.resources.ic_visibility_filled
import groshare.shared.generated.resources.ic_visibility_off_filled
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

sealed interface LoginAction {
    data class UsernameChanged(val username: String) : LoginAction
    data object UsernameLostFocus : LoginAction
    data class PasswordChanged(val password: String) : LoginAction
    object PasswordLostFocus : LoginAction
    data object Login : LoginAction
}

@Composable
fun LoginView(
    viewModel: LoginViewModel = koinViewModel(),
    onLoggedIn: (username: String) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LoginComponent(
        state = state,
        onAction = viewModel::onAction,
        onLoggedIn = onLoggedIn,
    )
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    val state = LoginState(
        username = "",
        password = "",
    )

    LoginComponent(state, onAction = {}, onLoggedIn = {})
}

@Composable
fun LoginComponent(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    onLoggedIn: (username: String) -> Unit,
) {
    LaunchedEffect(state.loggedInAs) {
        if (state.loggedInAs != null) onLoggedIn(state.loggedInAs)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(buildAnnotatedString {
            append("Don't have an account? ")
            withLink(
                LinkAnnotation.Url("https://groshare.dariostrm.dev/pages/register.html")
            ) {
                append("Sign up")
            }
        })

        var usernameEverFocused by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = state.username,
            onValueChange = { onAction(LoginAction.UsernameChanged(it)) },
            label = { Text(text = "Username") },
            isError = state.usernameError != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.onFocusChanged { e ->
                if (e.isFocused) usernameEverFocused = true
                else if (usernameEverFocused) onAction(LoginAction.UsernameLostFocus)
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
        var passwordEverFocused by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = state.password,
            onValueChange = { onAction(LoginAction.PasswordChanged(it)) },
            label = { Text(text = "Password") },
            modifier = Modifier.onFocusChanged { e ->
                if (e.isFocused) passwordEverFocused = true
                else if (passwordEverFocused) onAction(LoginAction.PasswordLostFocus)
            },
            isError = state.passwordError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onAction(LoginAction.Login) }),
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
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
                        modifier = Modifier.size(24.dp),
                        contentDescription = "Toggle password visibility"
                    )
                }
            }
        )

        Button(
            onClick = { onAction(LoginAction.Login) },
            enabled = !state.isLoading &&
                    state.username.isNotBlank() &&
                    state.password.isNotBlank() &&
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