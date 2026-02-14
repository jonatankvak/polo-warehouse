package com.polo.authentication.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polo.domain.repository.AuthenticationRepository
import com.polo.verification.PhoneVerificationService
import com.polo.verification.PhoneVerificationState
import com.polo.verification.PhoneVerificationState.CodeSent
import com.polo.verification.PhoneVerificationState.VerificationCompleted
import com.polo.verification.PhoneVerificationState.VerificationFailed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class VerificationViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val verificationService: PhoneVerificationService
): ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    private var verificationId: String = ""
    private var phoneNumber: String = ""

    fun verifyPhoneNumber(
        context: Context,
        phoneNumber: String
    ) {
        this.phoneNumber = phoneNumber
        viewModelScope.launch {

            _state.update {
                _state.value.copy(
                    isLoading = true,
                    isError = false,
                    errorMessage = "",
                    phoneNumber = phoneNumber
                )
            }

            verificationService
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

            verificationService
                .signIn(verificationId, token)
                .fold(
                    onSuccess = { handleSingInSuccess() },
                    onFailure = ::handleSignInError
                )
        }
    }

    fun resendToken(context: Context) {

        viewModelScope.launch {

            _state.update { _state.value.copy(isLoading = true, isError = false, errorMessage = "") }

            verificationService
                .resendToken(
                    context = context,
                    phoneNumber = phoneNumber
                )
                .collectLatest(::handleVerificationState)
        }
    }

    fun backToPhoneEntry() {
        _state.update {
            _state.value.copy(
                isCodeSent = false,
                isUserSignedIn = false,
                isError = false,
                errorMessage = "",
                isLoading = false
            )
        }
    }

    fun backToCodeEntry() {
        _state.update {
            _state.value.copy(
                isUserSignedIn = false,
                isCodeSent = true,
                isError = false,
                errorMessage = "",
                isLoading = false
            )
        }
    }

    fun saveName(
        firstName: String,
        lastName: String
    ) {

        viewModelScope.launch {

            _state.update { _state.value.copy(isLoading = true, isError = false, errorMessage = "") }

            authenticationRepository
                .updateName("$firstName $lastName")
                .fold(
                    onSuccess = { handleNameSuccess() },
                    onFailure = ::handleNameError
                )
        }
    }

    private suspend fun handleVerificationState(state: PhoneVerificationState) {
        when(state) {
            is CodeSent -> {
                verificationId = state.verificationId
                _state.update { _state.value.copy(isCodeSent = true, isLoading = false) }
            }
            is VerificationCompleted -> handleSingInSuccess()
            is VerificationFailed -> _state.update { _state.value.copy(isLoading = false, isError = true, errorMessage = state.exception.message ?: "") }
        }
    }

    private fun handleSingInSuccess() {

        val nameResult = authenticationRepository.getName()

        _state.update {
            _state.value.copy(isUserSignedIn = true, isLoading = false, isNameProvided = nameResult.isNotBlank())
        }
    }

    private fun handleSignInError(exception: Throwable) {

        _state.update {
            _state.value.copy(isLoading = false, isError = true, errorMessage = exception.message ?: "")
        }
    }

    private fun handleNameSuccess() {

        _state.update {
            _state.value.copy(isNameProvided = true, isLoading = false)
        }
    }

    private fun handleNameError(exception: Throwable) {

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
        val phoneNumber: String = "",
        val errorMessage: String = ""
    )
}
