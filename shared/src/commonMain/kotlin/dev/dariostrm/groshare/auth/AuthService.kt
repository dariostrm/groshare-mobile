package dev.dariostrm.groshare.auth

import dev.dariostrm.groshare.Result
import dev.dariostrm.groshare.err
import dev.dariostrm.groshare.ok
import io.ktor.client.HttpClient

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

class AuthServiceImpl(
    val httpClient: HttpClient
) : AuthService {

    override suspend fun login(username: String, password: String): Result<Unit, String> {
        username.validateUsername()?.let { return@login err(it) }
        password.validatePassword()?.let { return@login err(it) }

        return ok()
    }

}