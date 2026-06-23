package dev.dariostrm.groshare.debts

import dev.dariostrm.groshare.safeRequest
import dev.dariostrm.groshare.shared.*
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

@Serializable
data class Roommate(
    val id: Long,
    val username: String
)

@Serializable
data class Debt(
    val roommate: Roommate,
    val amountCents: Long
)

@Serializable
data class Debts(
    val totalNetCents: Long,
    val debts: List<Debt>
)

@Serializable
data class SettleDebtRequest(
    val recipientId: Long,
    val amountInCents: Long
)

interface DebtsService {
    suspend fun getDebts(): Result<Debts, String>
    suspend fun settleDebt(recipientId: Long, amountInCents: Long): Result<Unit, String>
}

class DebtsServiceImpl(
    private val httpClient: HttpClient
) : DebtsService {
    override suspend fun getDebts(): Result<Debts, String> {
        return httpClient.safeRequest<Debts> {
            method = HttpMethod.Get
            url("apartment/debts")
        }
    }

    override suspend fun settleDebt(recipientId: Long, amountInCents: Long): Result<Unit, String> {
        return httpClient.safeRequest<Unit> {
            method = HttpMethod.Post
            url("apartment/debts/settle")
            setBody(SettleDebtRequest(recipientId, amountInCents))
        }
    }
}
