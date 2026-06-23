package dev.dariostrm.groshare.home

import androidx.lifecycle.viewModelScope
import dev.dariostrm.groshare.auth.AuthService
import dev.dariostrm.groshare.auth.AuthState
import dev.dariostrm.groshare.auth.Profile
import dev.dariostrm.groshare.settings.Settings
import dev.dariostrm.groshare.settings.value
import dev.dariostrm.groshare.shared.MviViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class ProfileState(
    val profile: Profile? = null,
    val isLoading: Boolean = true,
    val isDarkMode: Boolean? = null
)

sealed interface ProfileAction {
    data class ToggleDarkMode(val enabled: Boolean?) : ProfileAction
    data object Logout : ProfileAction
}

class ProfileViewModel(
    private val authService: AuthService,
    private val settings: Settings
) : MviViewModel<ProfileState, ProfileAction>() {

    override val initialState = ProfileState(
        isDarkMode = settings.isDarkMode.value
    )

    init {
        authService.state.onEach { authState ->
            when (authState) {
                is AuthState.SignedIn -> updateState { copy(profile = authState.profile, isLoading = false) }
                else -> updateState { copy(profile = null, isLoading = authState is AuthState.Loading) }
            }
        }.launchIn(viewModelScope)

        settings.isDarkMode.state.onEach { darkMode ->
            updateState { copy(isDarkMode = darkMode) }
        }.launchIn(viewModelScope)
    }

    override fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.ToggleDarkMode -> {
                viewModelScope.launch {
                    settings.isDarkMode.set(action.enabled)
                }
            }
            is ProfileAction.Logout -> {
                viewModelScope.launch {
                    authService.logout()
                }
            }
        }
    }
}
