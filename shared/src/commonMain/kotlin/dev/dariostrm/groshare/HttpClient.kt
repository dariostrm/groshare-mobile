package dev.dariostrm.groshare

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer

fun getHttpClient(loadToken: () -> String?): HttpClient {
    return HttpClient() {
        install(Auth) {
            bearer {
                loadTokens {
                    val token = loadToken() ?: return@loadTokens null
                    BearerTokens(token, null)
                }
            }
        }
    }
}
