package com.polo.pallet.read.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polo.core_ui.model.UiPallet
import com.polo.data.datasource.IFireStoreDataSource
import com.polo.data.model.CreatePallet.PalletStatus.CREATED
import com.polo.data.model.CreatePallet.PalletStatus.READY
import com.polo.data.model.CreatePallet.PalletStatus.TRANSPORT
import com.polo.data.model.Warehouse.ZABLACE
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ReadPalletViewModel @Inject constructor(
    private val firestoreDataSource: IFireStoreDataSource
): ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun getPallet(palletUid: String) {

        viewModelScope.launch(Dispatchers.IO) {

            _state.update { current -> current.copy(isLoading = false) }

            firestoreDataSource.getPallet(palletUid)
                .onResult { pallet ->
                    viewModelScope.launch(Dispatchers.IO) {
                        val warehouse = firestoreDataSource.getWarehouse(pallet.warehouseUid).result()
                        val product = firestoreDataSource.getProducts(pallet.productUid).result()
                        _state.update { current -> current.copy(
                                isLoading = false,
                                pallet = UiPallet(
                                    uid = pallet.uid,
                                    date = pallet.date.toDate().toString(),
                                    productName = product.name,
                                    productAmount = pallet.productAmount,
                                    createdBy = pallet.createdBy,
                                    warehouseUid = pallet.warehouseUid,
                                    warehouseName = warehouse.name,
                                    status = pallet.status
                                )
                            )
                        }
                    }
                }.onError {
                    _state.update { current -> current.copy(
                            isLoading = false,
                            isError = triggered
                        )
                    }
                }
        }
    }

    fun changeStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { current -> current.copy(isLoading = true) }

            if (_state.value.isFinalDestination) {

                state.value.pallet?.uid?.let {
                    firestoreDataSource.deletePallet(it)
                        .onResult {
                            _state.update { current -> current.copy(isLoading = false, isDissolved = true) }
                        }.onError {
                            _state.update { current -> current.copy(isLoading = false) }
                        }
                }
            }

            val (toStatus, toWarehouse) = when(_state.value.pallet?.status) {
                READY -> Pair(TRANSPORT, state.value.pallet?.warehouseUid.orEmpty())
                TRANSPORT -> Pair(READY, ZABLACE.id)
                CREATED -> Pair(READY, state.value.pallet?.warehouseUid.orEmpty())
                else -> {
                    _state.update { current -> current.copy(isLoading = false) }
                    return@launch
                }
            }

            firestoreDataSource.updatePalletStatus(_state.value.pallet?.uid.toString(), toStatus, toWarehouse)
                .onResult {
                    _state.update { current -> current.copy(isLoading = false) }
                }.onError {
                    _state.update { current -> current.copy(isLoading = false) }
                }

            state.value.pallet?.uid?.let {
                getPallet(it)
            }
        }
    }

    fun errorConsumed() {
        _state.update { current -> current.copy(
                isError = consumed
            )
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val pallet: UiPallet? = null,
        val isError: StateEvent = consumed,
        val isDissolved: Boolean = false
    ) {

        val isFinalDestination: Boolean
            get() = (pallet?.warehouseUid == ZABLACE.id) and (pallet?.status == READY)
    }
}