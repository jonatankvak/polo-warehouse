package com.polo.scanner.verify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polo.ui.model.UiPallet
import com.polo.domain.model.PalletStatus.READY
import com.polo.domain.repository.PalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class VerifyPalletScannerViewModel @Inject constructor(
    private val palletRepository: PalletRepository
): ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun onQrCodeScanned(
        qrCode: String,
        pallet: UiPallet
    ) {

        if (qrCode != pallet.uid) {
            _state.update { current -> current.copy(scannedIsCodeWrong = triggered) }
            return
        }

        viewModelScope.launch {

            _state.update { current -> current.copy(isLoading = true) }

            palletRepository.updateStatus(
                palletUid = pallet.uid,
                status = READY
            ).onSuccess {
                _state.update { current -> current.copy(isPalletStatusUpdated = triggered, isLoading = false) }
            }.onFailure {
                _state.update { current -> current.copy(isError = triggered(it.message), isLoading = false) }
            }
        }
    }

    fun scannedIsCodeWrongConsumed() {

        _state.update { current -> current.copy(scannedIsCodeWrong = consumed) }
    }

    fun scannedIsErrorConsumed() {

        _state.update { current -> current.copy(isError = consumed()) }
    }

    data class UiState(
        val isPalletStatusUpdated: StateEvent = consumed,
        val scannedIsCodeWrong: StateEvent = consumed,
        val isError: StateEventWithContent<String?> = consumed(),
        val isLoading: Boolean = false
    )
}
