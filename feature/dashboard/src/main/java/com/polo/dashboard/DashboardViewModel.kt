package com.polo.dashboard

import androidx.lifecycle.ViewModel
import com.polo.data.datasource.IAuthenticationDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authenticationDataSource: IAuthenticationDataSource
): ViewModel() {

    private val _state = MutableStateFlow(UiState(name = getName()))
    val state: StateFlow<UiState> = _state

    data class UiState(
        val name: String = ""
    )

    private fun getName(): String {

        val result = authenticationDataSource.getName()
        return if (result.isResult) result.result() else result.error().message ?: ""
    }
}