package dev.dariostrm.groshare_mobile

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

class Greeting {
    private val client = HttpClient()

    suspend fun greet(): String {
        val response = client.get("http://localhost:8080/api/hello")
        return response.bodyAsText()
    }
}