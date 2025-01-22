package com.polo.warehouse.activity

import androidx.lifecycle.ViewModel
import com.polo.core.R
import com.polo.data.datasource.IAuthenticationDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authenticationDataSource: IAuthenticationDataSource
): ViewModel() {

    fun getStartingDestination(): Int {

        return when {
            authenticationDataSource.isSignedIn() -> R.id.dashboard_flow
            else -> R.id.authentication_flow
        }
    }
}