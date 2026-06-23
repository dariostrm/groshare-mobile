package dev.dariostrm.groshare

import dev.dariostrm.groshare.auth.AuthStateRepository
import dev.dariostrm.groshare.settings.SecureSettings
import dev.dariostrm.groshare.settings.value
import dev.dariostrm.groshare.shared.NetworkHealthStore
import dev.dariostrm.groshare.shared.Result
import dev.dariostrm.groshare.shared.err
import dev.dariostrm.groshare.shared.ok
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
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
import kotlinx.serialization.SerializationException

fun getHttpClient(
    secureSettings: SecureSettings,
    authStateRepository: AuthStateRepository,
    networkHealthStore: NetworkHealthStore
): HttpClient {
    return HttpClient() {
        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->
                when (exception) {
                    is SocketTimeoutException,
                    is ConnectTimeoutException,
                    is HttpRequestTimeoutException -> networkHealthStore.reportOffline()
                }
            }
            validateResponse { response ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    authStateRepository.onLogout()
                } else if (response.status.isSuccess()) {
                    networkHealthStore.reportOnline()
                }
            }
        }
        install(Auth) {
            bearer {
                loadTokens {
                    val token = secureSettings.authToken.value ?: return@loadTokens null
                    BearerTokens(token, null)
                }
                sendWithoutRequest { request ->
                    request.url.host == "groshare.dariostrm.dev"
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

suspend inline fun <reified T> HttpClient.safeRequest(
    onException: (Throwable) -> String = { "The server was unreachable" },
    block: HttpRequestBuilder.() -> Unit
): Result<T, String> {
    return try {
        val response = request { block() }

        if (response.status.isSuccess()) {
            ok(response.body<T>())
        }
        else {
            try {
                err(response.body<Error>().error)
            } catch (_: SerializationException) {
                err("HTTP Error ${response.status.value}: ${response.status.description}")
            }
        }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        err(onException(e))
    }
}
