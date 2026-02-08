package com.polo.domain.functional

suspend inline fun <T> runSuspendCatching(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (throwable: Throwable) {
        Result.failure(throwable)
    }
}
