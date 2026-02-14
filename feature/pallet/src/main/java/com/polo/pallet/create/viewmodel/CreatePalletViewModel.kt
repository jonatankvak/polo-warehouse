package com.polo.pallet.create.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polo.ui.model.UiPallet
import com.polo.ui.model.UiProduct
import com.polo.ui.model.UiWarehouse
import com.polo.domain.model.CreatePallet
import com.polo.domain.repository.AuthenticationRepository
import com.polo.domain.repository.PalletRepository
import com.polo.pallet.create.usecase.GetAllCreatedPalletsUseCase
import com.polo.pallet.create.usecase.GetAllProductsAndWarehousesUseCase
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class CreatePalletViewModel(
    private val palletRepository: PalletRepository,
    private val getAllProductsAndWarehousesUseCase: GetAllProductsAndWarehousesUseCase,
    private val getAllCreatedPalletsUseCase: GetAllCreatedPalletsUseCase,
    private val authenticationRepository: AuthenticationRepository
): ViewModel() {

    private val _state = MutableStateFlow(UiState(isLoading = true))
    val state: StateFlow<UiState> = _state

    fun getAllProductsAndWarehouses() {

        viewModelScope.launch(Dispatchers.IO) {
            getAllProductsAndWarehousesUseCase()
                .onSuccess { pair ->

                    _state.update { current -> current.copy(
                            products = pair.first,
                            warehouses = pair.second,
                            isLoading = false
                        )
                    }

                    viewModelScope.launch(Dispatchers.IO) {
                        getAllPallets(
                            products = pair.first,
                            warehouses = pair.second
                        )
                    }

                }.onFailure { exception ->
                    _state.update { current -> current.copy(isLoading = false, isError = triggered(exception.message)) }
                    Log.e("FIRESTORE_APP", exception.message, exception)
                }
        }
    }

    fun createPallet(
        product: UiProduct,
        warehouse: UiWarehouse,
        amount: Int
    ) {

        viewModelScope.launch {

            _state.update { current -> current.copy(isLoading = true) }

            palletRepository.createPallet(
                CreatePallet(
                    productUid = product.uid,
                    warehouseUid = warehouse.uid,
                    productAmount = amount,
                    createdBy = authenticationRepository.getName()
                )
            ).onSuccess {
                _state.update { current -> current.copy(isLoading = false, isPalletCreated = triggered) }
            }.onFailure { exception ->
                _state.update { current -> current.copy(isLoading = false, isError = triggered(exception.message)) }
                Log.e("FIRESTORE_APP", exception.message, exception)
            }
        }
    }

    fun deletePallet(
        pallet: UiPallet
    ) {

        viewModelScope.launch {

            _state.update { current -> current.copy(isLoading = true) }

            palletRepository.deletePallet(pallet.uid)
                .onSuccess {
                    _state.update { current -> current.copy(isLoading = false) }
                }.onFailure { exception ->
                    _state.update { current -> current.copy(isLoading = false, isError = triggered(exception.message)) }
                    Log.e("FIRESTORE_APP", exception.message, exception)
                }
        }
    }

    private suspend fun getAllPallets(
        products: List<UiProduct>,
        warehouses: List<UiWarehouse>
    ) {

        viewModelScope.launch(Dispatchers.IO) {

            _state.update { current -> current.copy(isLoading = true) }

            getAllCreatedPalletsUseCase(
                products,
                warehouses
            ).collectLatest { response ->
                response.onSuccess { pallets ->
                    _state.update { current -> current.copy(pallets = pallets, isLoading = false) }
                }.onFailure { exception ->
                    _state.update { current -> current.copy(isLoading = false, isError = triggered(exception.message)) }
                    Log.e("FIRESTORE_APP", exception.message, exception)
                }
            }
        }
    }

    fun errorConsumed() {
        _state.update { current -> current.copy(isError = consumed()) }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val products: List<UiProduct> = emptyList(),
        val warehouses: List<UiWarehouse> = emptyList(),
        val pallets: List<UiPallet> = emptyList(),
        val isPalletCreated: StateEvent = consumed,
        val isError: StateEventWithContent<String?> = consumed()
    )
}
