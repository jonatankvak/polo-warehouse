package com.polo.firebase.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.polo.domain.functional.runSuspendCatching
import com.polo.domain.repository.AuthenticationRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class AuthenticationRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthenticationRepository {

    override fun isSignedIn(): Boolean {

        return firebaseAuth.currentUser != null
    }

    override fun getUid(): String {

        return firebaseAuth.currentUser?.uid ?: throw Exception("User must be logged in")
    }

    override suspend fun updateName(name: String): Result<Unit> {
        val currentUser = firebaseAuth.currentUser
            ?: return Result.failure(IllegalStateException("User must be logged in"))

        return runSuspendCatching {
            currentUser.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            ).await()
            Unit
        }
    }

    override fun getName(): String {
        return firebaseAuth.currentUser?.displayName.orEmpty()
    }
}
