package com.polo.scanner.verify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polo.core_ui.model.UiPallet
import com.polo.data.datasource.IFireStoreDataSource
import com.polo.data.model.CreatePallet.PalletStatus.READY
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
    private val firestoreDataSource: IFireStoreDataSource
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

            firestoreDataSource.updatePalletStatus(
                palletUid = pallet.uid,
                status = READY
            ).onResult {
                _state.update { current -> current.copy(isPalletStatusUpdated = triggered) }
            }.onError {
                _state.update { current -> current.copy(isError = triggered(it.message)) }
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