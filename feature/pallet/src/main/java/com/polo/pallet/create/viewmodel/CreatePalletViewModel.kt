package com.polo.pallet.create.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polo.core_ui.model.UiPallet
import com.polo.core_ui.model.UiProduct
import com.polo.core_ui.model.UiWarehouse
import com.polo.data.datasource.IAuthenticationDataSource
import com.polo.data.datasource.IFireStoreDataSource
import com.polo.data.model.CreatePallet
import com.polo.pallet.create.usecase.GetAllCreatedPalletsUseCase
import com.polo.pallet.create.usecase.GetAllProductsAndWarehousesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CreatePalletViewModel @Inject constructor(
    private val firestoreDataSource: IFireStoreDataSource,
    private val getAllProductsAndWarehousesUseCase: GetAllProductsAndWarehousesUseCase,
    private val getAllCreatedPalletsUseCase: GetAllCreatedPalletsUseCase,
    private val authenticationDataSource: IAuthenticationDataSource
): ViewModel() {

    private val _state = MutableStateFlow(UiState(isLoading = true))
    val state: StateFlow<UiState> = _state

    fun getAllProductsAndWarehouses() {

        viewModelScope.launch(Dispatchers.IO) {
            getAllProductsAndWarehousesUseCase()
                .onResult { pair ->

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

                }.onError { exception ->

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

            firestoreDataSource.createPallets(
                CreatePallet(
                    productUid = product.uid,
                    warehouseUid = warehouse.uid,
                    productAmount = amount,
                    createdBy = authenticationDataSource.getName()
                )
            ).onResult {
                _state.update { current -> current.copy(isLoading = false, isPalletCreated = triggered) }
            }.onError { exception ->
                Log.e("FIRESTORE_APP", exception.message, exception)
            }
        }
    }

    fun deletePallet(
        pallet: UiPallet
    ) {

        viewModelScope.launch {

            _state.update { current -> current.copy(isLoading = true) }

            firestoreDataSource.deletePallet(pallet.uid)
                .onResult {
                    _state.update { current -> current.copy(isLoading = false) }
                }.onError { exception ->
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
                response.onResult { pallets ->
                    _state.update { current -> current.copy(pallets = pallets, isLoading = false) }
                }.onError { exception ->
                    Log.e("FIRESTORE_APP", exception.message, exception)
                }
            }
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val products: List<UiProduct> = emptyList(),
        val warehouses: List<UiWarehouse> = emptyList(),
        val pallets: List<UiPallet> = emptyList(),
        val isPalletCreated: StateEvent = consumed
    )
}