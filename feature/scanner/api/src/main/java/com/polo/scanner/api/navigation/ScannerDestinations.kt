package com.polo.scanner.api.navigation

import androidx.navigation3.runtime.NavKey
import com.polo.ui.model.ScanningPallet
import kotlinx.serialization.Serializable

@Serializable
data object EditPalletScannerDestination : NavKey

@Serializable
data class VerifyScannerDestination(val pallet: ScanningPallet) : NavKey
