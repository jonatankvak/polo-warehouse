package com.polo.authentication.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.polo.data.datasource.IAuthenticationDataSource
import com.polo.data.datasource.IPhoneVerificationDataSource
import com.polo.data.datasource.PhoneVerificationDataSource.PhoneVerificationState
import com.polo.data.datasource.PhoneVerificationDataSource.PhoneVerificationState.CodeSent
import com.polo.data.datasource.PhoneVerificationDataSource.PhoneVerificationState.VerificationCompleted
import com.polo.data.datasource.PhoneVerificationDataSource.PhoneVerificationState.VerificationFailed
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val authenticationDataSource: IAuthenticationDataSource,
    private val verificationDataSource: IPhoneVerificationDataSource
): ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    private var verificationId: String = ""
    private var resendToken: ForceResendingToken? = null
    private var phoneNumber: String = ""

    fun verifyPhoneNumber(
        context: Context,
        phoneNumber: String
    ) {
        this.phoneNumber = phoneNumber
        viewModelScope.launch {

            _state.update { _state.value.copy(isLoading = true, isError = false, errorMessage = "") }

            verificationDataSource
                .verifyPhoneNumber(
                    context = context,
                    phoneNumber = phoneNumber
                )
                .collectLatest(::handleVerificationState)
        }
    }

    fun sendOtpToken(token: String) {

        viewModelScope.launch {

            _state.update { _state.value.copy(isLoading = true, isError = false, errorMessage = "") }

            val credential = verificationDataSource.getCredential(verificationId, token)
            verificationDataSource
                .signIn(credential)
                .either(::handleSignInError, ::handleSingInSuccess)
        }
    }

    fun resendToken(context: Context) {

        viewModelScope.launch {

            _state.update { _state.value.copy(isLoading = true, isError = false, errorMessage = "") }

            verificationDataSource
                .verifyPhoneNumber(
                    context = context,
                    phoneNumber = phoneNumber,
                    token = resendToken
                )
                .collectLatest(::handleVerificationState)
        }
    }

    fun saveName(
        firstName: String,
        lastName: String
    ) {

        viewModelScope.launch {

            _state.update { _state.value.copy(isLoading = true, isError = false, errorMessage = "") }

            authenticationDataSource
                .updateName("$firstName $lastName")
                .either(
                    ::handleNameError,
                    ::handleNameSuccess
                )
        }
    }

    private suspend fun handleVerificationState(state: PhoneVerificationState) {
        when(state) {
            is CodeSent -> {
                verificationId = state.verificationId
                resendToken = state.token
                _state.update { _state.value.copy(isCodeSent = true, isLoading = false) }
            }
            is VerificationCompleted -> verificationDataSource
                .signIn(state.credential)
                .either(::handleSignInError, ::handleSingInSuccess)
            is VerificationFailed -> _state.update { _state.value.copy(isLoading = false, isError = true, errorMessage = state.exception.message ?: "") }
        }
    }

    private fun handleSingInSuccess() {

        val nameResult = authenticationDataSource.getName()

        _state.update {
            _state.value.copy(isUserSignedIn = true, isLoading = false, isNameProvided = nameResult.isResult)
        }
    }

    private fun handleSignInError(exception: Exception) {

        _state.update {
            _state.value.copy(isLoading = false, isError = true, errorMessage = exception.message ?: "")
        }
    }

    private fun handleNameSuccess() {

        _state.update {
            _state.value.copy(isNameProvided = true, isLoading = false)
        }
    }

    private fun handleNameError(exception: Exception) {

        _state.update {
            _state.value.copy(isLoading = false, isError = true, errorMessage = exception.message ?: "")
        }
    }

    data class UiState(
        val isLoading : Boolean = false,
        val isCodeSent : Boolean = false,
        val isError: Boolean = false,
        val isUserSignedIn : Boolean = false,
        val isNameProvided: Boolean = false,
        val errorMessage: String = ""
    )
}