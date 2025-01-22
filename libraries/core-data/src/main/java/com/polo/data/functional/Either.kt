package com.polo.data.functional

sealed class Either <out E, out R> {

    data class Error<out E>(val data: E) : Either<E, Nothing>()

    data class Result<out R>(val data: R) : Either<Nothing, R>()

    val isResult get() = this is Result<R>
    val isError get() = this is Error<E>

    fun error(): E = (this as Error<E>).data

    fun result(): R = (this as Result<R>).data

    fun onError(action: (E) -> Unit): Either<E, R> {
        if (this is Error) action(this.data)
        return this
    }

    fun onResult(action: (R) -> Unit): Either<E, R> {
        if (this is Result) action(this.data)
        return this
    }

    fun either(fnl: (E) -> Any, fnR: (R) -> Any): Any =
        when (this) {
            is Error -> fnl(data)
            is Result -> fnR(data)
        }

    fun either(fnl: (E) -> Any, fnR: () -> Any): Any =
        when (this) {
            is Error -> fnl(data)
            is Result -> fnR()
        }

    suspend fun either(fnl: (E) -> Any, fnR: suspend () -> Any): Any =
        when (this) {
            is Error -> fnl(data)
            is Result -> fnR()
        }

    suspend fun either(fnl: suspend (E) -> Any, fnR: suspend (R) -> Any): Any =
        when (this) {
            is Error -> fnl(data)
            is Result -> fnR(data)
        }

    suspend fun either(fnl: (E) -> Any, fnR: suspend (R) -> Any): Any =
        when (this) {
            is Error -> fnl(data)
            is Result -> fnR(data)
        }

    suspend fun either(fnl: suspend (E) -> Any, fnR: suspend () -> Any): Any =
        when (this) {
            is Error -> fnl(data)
            is Result -> fnR()
        }

    companion object
}

suspend inline fun <T> runCatchingEither(block: suspend () -> T): Either<Exception, T> {
    return try {
        Either.Result(block())
    } catch (e: Exception) {
        Either.Error(e)
    }
}