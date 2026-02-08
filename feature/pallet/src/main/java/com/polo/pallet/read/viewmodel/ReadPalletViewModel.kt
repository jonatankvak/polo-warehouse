package com.polo.pallet.read.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polo.core_ui.model.UiPallet
import com.polo.domain.functional.Either
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

            when (val palletResult = palletRepository.getPallet(palletUid)) {
                is Either.Error -> {
                    _state.update { current -> current.copy(
                            isLoading = false,
                            isError = triggered
                        )
                    }
                }
                is Either.Result -> {
                    val pallet = palletResult.data
                    when (val productResult = productRepository.getProduct(pallet.productUid)) {
                        is Either.Error -> _state.update { current -> current.copy(
                                isLoading = false,
                                isError = triggered
                            )
                        }
                        is Either.Result -> {
                            when (val warehouseResult = warehouseRepository.getWarehouse(pallet.warehouseUid)) {
                                is Either.Error -> _state.update { current -> current.copy(
                                        isLoading = false,
                                        isError = triggered
                                    )
                                }
                                is Either.Result -> _state.update { current -> current.copy(
                                        isLoading = false,
                                        pallet = UiPallet(
                                            uid = pallet.uid,
                                            date = java.time.Instant.ofEpochMilli(pallet.dateEpochMillis).toString(),
                                            productName = productResult.data.name,
                                            productAmount = pallet.productAmount,
                                            createdBy = pallet.createdBy,
                                            warehouseUid = pallet.warehouseUid,
                                            warehouseName = warehouseResult.data.name,
                                            status = pallet.status
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
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
                    .onResult {
                        _state.update { current -> current.copy(isLoading = false, isDissolved = true) }
                    }.onError {
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
                .onResult {
                    _state.update { current -> current.copy(isLoading = false) }
                }.onError {
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
