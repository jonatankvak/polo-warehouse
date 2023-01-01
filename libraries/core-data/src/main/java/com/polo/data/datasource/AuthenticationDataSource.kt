package com.polo.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.polo.data.functional.Either
import com.polo.data.functional.runCatchingEither
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class AuthenticationDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): IAuthenticationDataSource {

    override fun isSignedIn(): Boolean {

        return firebaseAuth.currentUser != null
    }

    override suspend fun updateName(name: String): Either<Exception, Unit> {

        return runCatchingEither {
            firebaseAuth.currentUser?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            )?.await() ?: Either.Error(Exception("User must be logged in"))
        }
    }

    override fun getName(): Either<Exception, String> {
        return firebaseAuth.currentUser?.displayName?.let {
            Either.Result(it)
        } ?: Either.Error(Exception("User doesn't have a name"))
    }
}