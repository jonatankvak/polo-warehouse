package com.polo.warehouse.activity

import androidx.lifecycle.ViewModel
import com.polo.data.datasource.IAuthenticationDataSource
import com.polo.warehouse.navigation.AppDestination
import com.polo.warehouse.navigation.AuthenticationDestination
import com.polo.warehouse.navigation.DashboardDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authenticationDataSource: IAuthenticationDataSource
) : ViewModel() {

    fun getStartingDestination(): AppDestination {
        return when {
            authenticationDataSource.isSignedIn() -> DashboardDestination
            else -> AuthenticationDestination
        }
    }
}
