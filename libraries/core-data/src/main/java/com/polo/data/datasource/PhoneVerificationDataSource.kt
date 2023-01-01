package com.polo.data.datasource

import android.app.Activity
import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Phone
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.polo.data.datasource.PhoneVerificationDataSource.PhoneVerificationState.CodeSent
import com.polo.data.datasource.PhoneVerificationDataSource.PhoneVerificationState.VerificationCompleted
import com.polo.data.datasource.PhoneVerificationDataSource.PhoneVerificationState.VerificationFailed
import com.polo.data.functional.Either
import com.polo.data.functional.runCatchingEither
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class PhoneVerificationDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
): IPhoneVerificationDataSource {

    override fun verifyPhoneNumber(
        context: Context,
        phoneNumber: String,
        token: ForceResendingToken?
    ) = callbackFlow {

        val optionsBuilder = PhoneAuthOptions.newBuilder()
            .setTimeout(60L, SECONDS)
            .setActivity(context as Activity)
            .setPhoneNumber(phoneNumber)
            .setCallbacks(
                object : OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                        trySend(VerificationCompleted(credential))
                    }

                    override fun onVerificationFailed(exception: FirebaseException) {

                        trySend(VerificationFailed(exception))
                    }

                    override fun onCodeSent(verificationId: String, token: ForceResendingToken) {

                        trySend(CodeSent(verificationId, token))
                    }
                }
            )

        token?.let {
            optionsBuilder
                .setForceResendingToken(it)
        }

        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())

        awaitClose()
    }

    override fun getCredential(verificationId: String, token: String): PhoneAuthCredential {

        return PhoneAuthProvider.getCredential(verificationId, token)
    }

    override suspend fun signIn(credential: PhoneAuthCredential): Either<Exception, Unit> {

        return runCatchingEither {
            firebaseAuth.signInWithCredential(credential).await()
        }
    }

    sealed class PhoneVerificationState {
        data class VerificationCompleted(val credential: PhoneAuthCredential): PhoneVerificationState()
        data class VerificationFailed(val exception: FirebaseException): PhoneVerificationState()
        data class CodeSent(val verificationId: String, val token: ForceResendingToken): PhoneVerificationState()
    }
}