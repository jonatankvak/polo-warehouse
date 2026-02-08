package com.polo.pallet.api.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object CreatePalletDestination : NavKey

@Serializable
data class ReadPalletDestination(val palletUid: String) : NavKey
