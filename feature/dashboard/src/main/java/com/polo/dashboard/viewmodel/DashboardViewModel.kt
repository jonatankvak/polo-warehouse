package com.polo.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polo.dashboard.usecase.GetReadyPalletsUseCase
import com.polo.dashboard.viewmodel.PalletListUiModel.PalletListUiBody
import com.polo.dashboard.viewmodel.PalletListUiModel.PalletListUiHeader
import com.polo.data.datasource.IAuthenticationDataSource
import com.polo.data.datasource.IFireStoreDataSource
import com.polo.data.model.ProductDocument
import com.polo.data.model.WarehouseDocument
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authenticationDataSource: IAuthenticationDataSource,
    private val firestoreDataSource: IFireStoreDataSource,
    private val getPalletsUseCase: GetReadyPalletsUseCase
): ViewModel() {

    private var loadedProducts: List<ProductDocument> = emptyList()
    private var loadedWarehouses: List<WarehouseDocument> = emptyList()

    private val _state = MutableStateFlow(UiState(name = getName()))
    val state: StateFlow<UiState> = _state

    fun getAllReadyPallets() {

        _state.update { current -> current.copy(isLoading = true) }

        viewModelScope.launch {

            getAllProductsAndWarehouses()

            getPalletsUseCase(loadedProducts, loadedWarehouses)
                .collectLatest { response ->
                    response.either(
                        ::handleError,
                        ::handleResponse
                    )
                }
        }
    }

    private fun handleResponse(response: Map<PalletListUiHeader, List<PalletListUiBody>>) {

        _state.update { current ->
            current.copy(
                palletsUiModels = response,
                isLoading = false
            )
        }
    }

    private suspend fun handleError(exception: Exception) {

        _state.update { current ->
            current.copy(
                onError = triggered(exception.message ?: "Something went wrong"),
                isLoading = false
            )
        }
    }

    private suspend fun getAllProductsAndWarehouses() {

        firestoreDataSource.getAllProductsAndWarehouses()
            .onResult {
                loadedProducts = it.first
                loadedWarehouses = it.second
            }
    }

    private fun getName(): String {

        return authenticationDataSource.getName()
    }

    data class UiState(
        val name: String = "",
        val isLoading : Boolean = false,
        val palletsUiModels: Map<PalletListUiHeader, List<PalletListUiBody>> = mapOf(),
        val onError: StateEventWithContent<String> = consumed()
    )
}