package com.polo.domain.repository

import com.polo.domain.functional.Either

interface AuthenticationRepository {

    fun isSignedIn(): Boolean

    fun getUid(): String

    suspend fun updateName(name: String): Either<Exception, Unit>

    fun getName(): String
}
