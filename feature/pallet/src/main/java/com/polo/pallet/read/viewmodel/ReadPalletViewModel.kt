package com.polo.pallet.read.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polo.ui.model.UiPallet
import com.polo.domain.model.PalletStatus.CREATED
import com.polo.domain.model.PalletStatus.READY
import com.polo.domain.model.PalletStatus.TRANSPORT
import com.polo.domain.model.WarehouseIds
import com.polo.domain.repository.PalletRepository
import com.polo.domain.repository.ProductRepository
import com.polo.domain.repository.WarehouseRepository
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
    private val palletRepository: PalletRepository,
    private val productRepository: ProductRepository,
    private val warehouseRepository: WarehouseRepository
): ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun getPallet(palletUid: String) {

        viewModelScope.launch(Dispatchers.IO) {

            _state.update { current -> current.copy(isLoading = true) }

            val pallet = palletRepository.getPallet(palletUid).getOrElse {
                _state.update { current -> current.copy(isLoading = false, isError = triggered) }
                return@launch
            }

            val product = productRepository.getProduct(pallet.productUid).getOrElse {
                _state.update { current -> current.copy(isLoading = false, isError = triggered) }
                return@launch
            }

            val warehouse = warehouseRepository.getWarehouse(pallet.warehouseUid).getOrElse {
                _state.update { current -> current.copy(isLoading = false, isError = triggered) }
                return@launch
            }

            _state.update { current -> current.copy(
                    isLoading = false,
                    pallet = UiPallet(
                        uid = pallet.uid,
                        date = java.time.Instant.ofEpochMilli(pallet.dateEpochMillis).toString(),
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
    }

    fun changeStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { current -> current.copy(isLoading = true) }

            val currentPallet = state.value.pallet ?: run {
                _state.update { current -> current.copy(isLoading = false) }
                return@launch
            }

            if (_state.value.isFinalDestination) {
                palletRepository.deletePallet(currentPallet.uid)
                    .onSuccess {
                        _state.update { current -> current.copy(isLoading = false, isDissolved = true) }
                    }.onFailure {
                        _state.update { current -> current.copy(isLoading = false) }
                    }
                return@launch
            }

            val (toStatus, toWarehouse) = when(currentPallet.status) {
                READY -> Pair(TRANSPORT, currentPallet.warehouseUid)
                TRANSPORT -> Pair(READY, WarehouseIds.ZABLACE)
                CREATED -> Pair(READY, currentPallet.warehouseUid)
            }

            palletRepository.updateStatus(currentPallet.uid, toStatus, toWarehouse)
                .onSuccess {
                    _state.update { current -> current.copy(isLoading = false) }
                }.onFailure {
                    _state.update { current -> current.copy(isLoading = false) }
                }

            getPallet(currentPallet.uid)
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
            get() = (pallet?.warehouseUid == WarehouseIds.ZABLACE) and (pallet?.status == READY)
    }
}
