package dev.dariostrm.groshare.auth

import dev.dariostrm.groshare.shared.Result
import dev.dariostrm.groshare.shared.err
import dev.dariostrm.groshare.shared.ok
import dev.dariostrm.groshare.shared.ifError
import dev.dariostrm.groshare.safeRequest
import dev.dariostrm.groshare.shared.andThen
import dev.dariostrm.groshare.shared.ifOk
import dev.dariostrm.groshare.shared.map
import dev.dariostrm.groshare.shared.unwrap
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import kotlinx.serialization.Serializable

interface AuthService: AuthStateRepository {
    suspend fun verifySession(): Result<Profile, String>
    suspend fun login(username: String, password: String) : Result<Unit, String>
    suspend fun logout()
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

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class Token(val token: String)

class AuthServiceImpl(
    private val httpClient: HttpClient,
    state: AuthStateRepository,
) : AuthService, AuthStateRepository by state {


    override suspend fun verifySession(): Result<Profile, String> {
        return getProfile()
            .ifOk { profile -> onVerifiedToken(profile) }
            .ifError { onLogout() }
    }

    private suspend fun getProfile(): Result<Profile, String> {
        return httpClient.safeRequest<Profile>() {
            method = HttpMethod.Get
            url("profile")
        }
    }

    override suspend fun login(username: String, password: String): Result<Unit, String> {
        username.validateUsername()?.let { return@login err(it) }
        password.validatePassword()?.let { return@login err(it) }

        return httpClient.safeRequest<Token>() {
            method = HttpMethod.Post
            url("login")
            setBody(LoginRequest(username, password))
        }
            .ifOk { token -> onLogin(token.token) }
            .andThen { getProfile() }
            .ifOk { profile -> onVerifiedToken(profile) }
            .map { }
    }

    override suspend fun logout() {
        httpClient.safeRequest<Unit> {
            method = HttpMethod.Post
            url("logout")
        }

        onLogout()
    }

}