package com.polo.data.datasource

import android.content.Context
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.polo.data.datasource.PhoneVerificationDataSource.PhoneVerificationState
import com.polo.data.functional.Either
import kotlinx.coroutines.flow.Flow

interface IPhoneVerificationDataSource {

    fun verifyPhoneNumber(
        context: Context,
        phoneNumber: String,
        token: ForceResendingToken? = null
    ): Flow<PhoneVerificationState>

    fun getCredential(
        verificationId: String,
        token: String
    ): PhoneAuthCredential

    suspend fun signIn(credential: PhoneAuthCredential): Either<Exception, Unit>
}