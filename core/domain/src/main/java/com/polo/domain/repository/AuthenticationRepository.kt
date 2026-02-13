package com.polo.domain.repository

interface AuthenticationRepository {

    fun isSignedIn(): Boolean

    fun getUid(): String

    suspend fun updateName(name: String): Result<Unit>

    fun getName(): String
}
