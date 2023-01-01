package com.polo.warehouse.activity

import androidx.lifecycle.ViewModel
import com.polo.data.datasource.IAuthenticationDataSource
import com.polo.warehouse.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authenticationDataSource: IAuthenticationDataSource
): ViewModel() {

    fun getStartingDestination(): Int {

        return when {
            authenticationDataSource.isSignedIn() -> com.polo.core.R.id.dashboard_flow
            else -> com.polo.core.R.id.authentication_flow
        }
    }
}