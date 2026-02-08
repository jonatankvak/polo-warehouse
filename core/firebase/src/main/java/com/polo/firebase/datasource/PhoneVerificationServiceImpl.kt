package com.polo.firebase.datasource

import android.app.Activity
import android.content.Context
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.polo.domain.functional.runSuspendCatching
import com.polo.verification.PhoneVerificationService
import com.polo.verification.PhoneVerificationState
import com.polo.verification.PhoneVerificationState.CodeSent
import com.polo.verification.PhoneVerificationState.VerificationCompleted
import com.polo.verification.PhoneVerificationState.VerificationFailed
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class PhoneVerificationServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : PhoneVerificationService {

    private var resendToken: ForceResendingToken? = null

    override fun verifyPhoneNumber(
        context: Context,
        phoneNumber: String
    ) = callbackFlow {
        val activity = context as? Activity
        if (activity == null) {
            trySend(VerificationFailed(FirebaseException("Phone verification requires an Activity context.")))
            close()
            return@callbackFlow
        }

        val optionsBuilder = PhoneAuthOptions.newBuilder()
            .setTimeout(60L, SECONDS)
            .setActivity(activity)
            .setPhoneNumber(phoneNumber)
            .setCallbacks(
                object : OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        signInCredential(
                            credential = credential,
                            onError = { trySend(VerificationFailed(it)) },
                            onSuccess = { trySend(VerificationCompleted()) }
                        )
                    }

                    override fun onVerificationFailed(exception: FirebaseException) {

                        trySend(VerificationFailed(exception))
                    }

                    override fun onCodeSent(verificationId: String, token: ForceResendingToken) {
                        resendToken = token
                        trySend(CodeSent(verificationId))
                    }
                }
            )

        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())

        awaitClose()
    }

    override fun resendToken(context: Context, phoneNumber: String) = callbackFlow {
        val activity = context as? Activity
        if (activity == null) {
            trySend(VerificationFailed(FirebaseException("Phone verification requires an Activity context.")))
            close()
            return@callbackFlow
        }

        val token = resendToken
        if (token == null) {
            trySend(VerificationFailed(IllegalStateException("Resend token is not available")))
            close()
            return@callbackFlow
        }

        val options = PhoneAuthOptions.newBuilder()
            .setTimeout(60L, SECONDS)
            .setActivity(activity)
            .setPhoneNumber(phoneNumber)
            .setForceResendingToken(token)
            .setCallbacks(
                object : OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        signInCredential(
                            credential = credential,
                            onError = { trySend(VerificationFailed(it)) },
                            onSuccess = { trySend(VerificationCompleted()) }
                        )
                    }

                    override fun onVerificationFailed(exception: FirebaseException) {
                        trySend(VerificationFailed(exception))
                    }

                    override fun onCodeSent(verificationId: String, newToken: ForceResendingToken) {
                        resendToken = newToken
                        trySend(CodeSent(verificationId))
                    }
                }
            )
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        awaitClose()
    }

    override suspend fun signIn(verificationId: String, token: String): Result<Unit> {
        return runSuspendCatching {
            firebaseAuth
                .signInWithCredential(PhoneAuthProvider.getCredential(verificationId, token))
                .await()
            Unit
        }
    }

    private fun signInCredential(
        credential: PhoneAuthCredential,
        onError: (Exception) -> Unit,
        onSuccess: () -> Unit
    ) {
        firebaseAuth
            .signInWithCredential(credential)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
}
