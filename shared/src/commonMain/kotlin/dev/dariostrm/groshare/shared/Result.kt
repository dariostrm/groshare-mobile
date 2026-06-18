package dev.dariostrm.groshare.shared

sealed interface Result<out T, out TError> {
    data class Ok<out T>(val data: T) : Result<T, Nothing>
    data class Error<out TError>(val error: TError) : Result<Nothing, TError>
}

fun <E> err(error: E) = Result.Error(error)
fun <D> ok(data: D) = Result.Ok(data)
fun ok() = Result.Ok(Unit)

fun <T> Result<T, Any>.unwrap(): T {
    when (this) {
        is Result.Ok -> return data
        is Result.Error -> throw UnwrappedErrorException()
    }
}

class UnwrappedErrorException : Exception()

inline fun <T, E, R> Result<T, E>.map(transform: (T) -> R): Result<R, E> {
    return when (this) {
        is Result.Ok -> Result.Ok(transform(this.data))
        is Result.Error -> this
    }
}

inline fun <T, E, R> Result<T, E>.mapErr(transform: (E) -> R): Result<T, R> {
    return when (this) {
        is Result.Ok -> this
        is Result.Error -> Result.Error(transform(this.error))
    }
}

inline fun <T, E, R> Result<T, E>.match(onSuccess: (T) -> R, onError: (E) -> R): R {
    return when (this) {
        is Result.Ok -> onSuccess(this.data)
        is Result.Error -> onError(this.error)
    }
}

inline fun <T, E> Result<T, E>.ifError(block: (E) -> Unit): Result<T, E> {
    if (this is Result.Error) block(this.error)
    return this
}

inline fun <T, E> Result<T, E>.ifOk(block: (T) -> Unit): Result<T, E> {
    if (this is Result.Ok) block(this.data)
    return this
}

inline fun <T, E> Result<T, E>.then(block: () -> Unit): Result<T, E> {
    block()
    return this
}