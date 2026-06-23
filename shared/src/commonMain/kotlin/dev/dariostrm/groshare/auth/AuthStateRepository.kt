package dev.dariostrm.groshare.auth

import dev.dariostrm.groshare.settings.SecureSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val username: String,
    val email: String,
    val apartmentName: String?
)

sealed class AuthState {
    data object Loading : AuthState()
    data class SignedIn(val profile: Profile) : AuthState()
    data object SignedOut : AuthState()
}

interface AuthStateRepository{
    val state: StateFlow<AuthState>

    suspend fun onVerifiedToken(profile: Profile)
    suspend fun onLogin(token: String)
    suspend fun onLogout()
}

class AuthStateRepositoryImpl(
    private val secureSettings: SecureSettings
): AuthStateRepository {
    private val _state = MutableStateFlow<AuthState>(AuthState.Loading)
    override val state: StateFlow<AuthState> = _state.asStateFlow()

    override suspend fun onVerifiedToken(profile: Profile) {
        _state.update { AuthState.SignedIn(profile) }
    }

    override suspend fun onLogin(token: String) {
        secureSettings.authToken.set(token)
    }

    override suspend fun onLogout() {
        secureSettings.authToken.set(null)
        _state.update { AuthState.SignedOut }
    }


}