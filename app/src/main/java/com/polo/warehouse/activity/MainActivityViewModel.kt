package com.polo.warehouse.activity

import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavKey
import com.polo.authentication.api.navigation.AuthenticationDestination
import com.polo.dashboard.api.navigation.DashboardDestination
import com.polo.domain.repository.AuthenticationRepository
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class MainActivityViewModel(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    fun getStartingDestination(): NavKey {
        return when {
            authenticationRepository.isSignedIn() -> DashboardDestination
            else -> AuthenticationDestination
        }
    }
}
