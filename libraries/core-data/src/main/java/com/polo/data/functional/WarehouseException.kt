package com.polo.data.functional

import java.lang.Exception

sealed class WarehouseException : Exception() {

    data class Error(
        val error: Throwable
    ) : WarehouseException()

    override val message: String?
        get() = when (this) {
            is Error -> error.message
        }
}
