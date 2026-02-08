package com.polo.verification

import android.content.Context
import kotlinx.coroutines.flow.Flow

interface PhoneVerificationService {

    fun verifyPhoneNumber(context: Context, phoneNumber: String): Flow<PhoneVerificationState>

    fun resendToken(context: Context, phoneNumber: String): Flow<PhoneVerificationState>

    suspend fun signIn(verificationId: String, token: String): Result<Unit>
}

sealed class PhoneVerificationState {
    data class VerificationCompleted(val signedIn: Boolean = true) : PhoneVerificationState()
    data class VerificationFailed(val exception: Exception) : PhoneVerificationState()
    data class CodeSent(val verificationId: String) : PhoneVerificationState()
}
