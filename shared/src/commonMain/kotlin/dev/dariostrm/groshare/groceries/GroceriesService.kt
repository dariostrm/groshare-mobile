package dev.dariostrm.groshare.groceries

import dev.dariostrm.groshare.safeRequest
import dev.dariostrm.groshare.shared.*
import io.ktor.client.*
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class Grocery(
    val id: Long,
    val name: String,
    val addedByUsername: String,
)

interface GroceriesService {
    val groceries: StateFlow<List<Grocery>>
    val error: StateFlow<String?>
    suspend fun refreshGroceries()
    fun startPolling()
    fun stopPolling()
    suspend fun addGrocery(groceryName: String): Result<Unit, String>
    suspend fun deleteGrocery(groceryId: Long): Result<Unit, String>
}

class GroceriesServiceImpl(
    private val httpClient: HttpClient,
    private val pollingInterval: Duration = 10.seconds,
) : GroceriesService {
    private val _groceries = MutableStateFlow<List<Grocery>>(emptyList())
    override val groceries = _groceries.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error = _error.asStateFlow()

    private var isPolling = false
    private var pollingJob: Job? = null
    private val pollingScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override suspend fun refreshGroceries() {
        httpClient.safeRequest<List<Grocery>>() {
            method = HttpMethod.Get
            url("apartment/groceries")
        }
            .ifOk { groceries ->
                _groceries.update { groceries }
                _error.update { null }
            }
            .ifError { _error.update { it } }
        restartPollingTimer()
    }

    private fun restartPollingTimer() {
        if (!isPolling) return

        pollingJob?.cancel()

        pollingJob = pollingScope.launch {
            delay(pollingInterval)
            refreshGroceries()
        }
    }

    override fun startPolling() {
        if (isPolling) return
        isPolling = true

        pollingJob?.cancel()

        pollingJob = pollingScope.launch {
            refreshGroceries()
        }
    }

    override fun stopPolling() {
        isPolling = false
        pollingJob?.cancel()
    }

    @Serializable
    data class AddGroceryRequest(val name: String)

    override suspend fun addGrocery(groceryName: String): Result<Unit, String> {
        httpClient.safeRequest<Unit> {
            method = HttpMethod.Post
            url("apartment/groceries")
            setBody(AddGroceryRequest(groceryName))
        }.ifError { return@addGrocery err(it) }
        refreshGroceries()
        return ok()
    }

    override suspend fun deleteGrocery(groceryId: Long): Result<Unit, String> {
        httpClient.safeRequest<Unit> {
            method = HttpMethod.Delete
            url("apartment/groceries/$groceryId")
        }.ifError { return@deleteGrocery err(it) }
        refreshGroceries()
        return ok()
    }

}