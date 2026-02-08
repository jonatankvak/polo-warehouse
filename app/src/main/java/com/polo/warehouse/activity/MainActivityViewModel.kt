package com.polo.warehouse.activity

import androidx.lifecycle.ViewModel
import com.polo.domain.repository.AuthenticationRepository
import com.polo.warehouse.navigation.AppDestination
import com.polo.warehouse.navigation.AuthenticationDestination
import com.polo.warehouse.navigation.DashboardDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    fun getStartingDestination(): AppDestination {
        return when {
            authenticationRepository.isSignedIn() -> DashboardDestination
            else -> AuthenticationDestination
        }
    }
}
