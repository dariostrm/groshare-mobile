package dev.dariostrm.groshare

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable

fun getHttpClient(secureSettings: SecureSettings): HttpClient {
    return HttpClient() {
        install(Auth) {
            bearer {
                loadTokens {
                    val token = secureSettings.authToken.value ?: return@loadTokens null
                    BearerTokens(token, null)
                }
            }
        }
        install(ContentNegotiation) {
            json()
        }
        expectSuccess = false
        defaultRequest {
            url("https://groshare.dariostrm.dev/api/v1/")
            contentType(ContentType.Application.Json)
        }
    }
}

@Serializable
data class Error(val error: String)

suspend inline fun <reified T, reified E> HttpClient.safeRequest(
    onException: (Throwable) -> E,
    block: HttpRequestBuilder.() -> Unit
): Result<T, E> {
    return try {
        val response = request { block() }

        if (response.status.isSuccess()) {
            ok(response.body<T>())
        } else {
            err(response.body<E>())
        }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        err(onException(e))
    }
}
