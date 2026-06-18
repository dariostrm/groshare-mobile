package dev.dariostrm.groshare.auth

import dev.dariostrm.groshare.Error
import dev.dariostrm.groshare.Result
import dev.dariostrm.groshare.SecureSettings
import dev.dariostrm.groshare.err
import dev.dariostrm.groshare.ok
import dev.dariostrm.groshare.ifError
import dev.dariostrm.groshare.safeRequest
import dev.dariostrm.groshare.unwrap
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import kotlinx.serialization.Serializable

interface AuthService {
    suspend fun login(username: String, password: String) : Result<Unit, String>
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
    private val secureSettings: SecureSettings
) : AuthService {

    override suspend fun login(username: String, password: String): Result<Unit, String> {
        username.validateUsername()?.let { return@login err(it) }
        password.validatePassword()?.let { return@login err(it) }

        val token = httpClient.safeRequest<Token, Error>(
            onException = { Error(it.message ?: "Unknown error") }
        ) {
            method = HttpMethod.Post
            url("login")
            setBody(LoginRequest(username, password))
        }.ifError { return@login err(it.error) }.unwrap()

        secureSettings.authToken.set(token.token)

        return ok()
    }

}