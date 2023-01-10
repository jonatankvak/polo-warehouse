package com.polo.data.datasource

import com.polo.data.functional.Either

interface IAuthenticationDataSource {

    fun isSignedIn(): Boolean

    fun getUid(): String

    suspend fun updateName(name: String): Either<Exception, Unit>

    fun getName(): String
}