package com.polo.data.datasource

import com.polo.data.functional.Either

interface IAuthenticationDataSource {

    fun isSignedIn(): Boolean

    suspend fun updateName(name: String): Either<Exception, Unit>

    fun getName(): Either<Exception, String>
}