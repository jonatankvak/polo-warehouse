package com.polo.data.functional

sealed class WarehouseException : Exception() {

    data class Error(
        val error: Throwable
    ) : WarehouseException()

    override val message: String?
        get() = when (this) {
            is Error -> error.message
        }
}
