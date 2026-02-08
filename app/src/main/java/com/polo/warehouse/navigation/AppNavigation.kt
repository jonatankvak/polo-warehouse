package com.polo.warehouse.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.polo.authentication.view.VerificationRoute
import com.polo.core_ui.model.ScanningPallet
import com.polo.dashboard.view.DashboardRoute
import com.polo.pallet.create.view.CreatePalletRoute
import com.polo.pallet.read.view.ReadPalletRoute
import com.polo.scanner.read.ScanPalletRoute
import com.polo.scanner.verify.view.VerifyPalletScannerRoute
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppDestination : NavKey

@Serializable
data object AuthenticationDestination : AppDestination

@Serializable
data object DashboardDestination : AppDestination

@Serializable
data object CreatePalletDestination : AppDestination

@Serializable
data object EditPalletScannerDestination : AppDestination

@Serializable
data class VerifyScannerDestination(val pallet: ScanningPallet) : AppDestination

@Serializable
data class ReadPalletDestination(val palletUid: String) : AppDestination

@Composable
fun PoloWarehouseNavHost(
    startDestination: AppDestination
) {
    val backStack = rememberNavBackStack(startDestination)

    val entryDecorators: List<NavEntryDecorator<NavKey>> = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator<NavKey>()
    )

    val goBack: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    val goToDashboard: () -> Unit = {
        backStack.clear()
        backStack.add(DashboardDestination)
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<AuthenticationDestination> {
                VerificationRoute(onSignedIn = goToDashboard)
            }
            entry<DashboardDestination> {
                DashboardRoute(
                    onCreatePallet = { backStack.add(CreatePalletDestination) },
                    onEditPallet = { backStack.add(EditPalletScannerDestination) }
                )
            }
            entry<CreatePalletDestination> {
                CreatePalletRoute(
                    onVerifyByScan = { pallet ->
                        backStack.add(VerifyScannerDestination(pallet))
                    }
                )
            }
            entry<EditPalletScannerDestination> {
                ScanPalletRoute(
                    onPalletScanned = { palletUid ->
                        backStack.add(ReadPalletDestination(palletUid))
                    }
                )
            }
            entry<VerifyScannerDestination> { key ->
                VerifyPalletScannerRoute(
                    pallet = key.pallet,
                    onBack = goBack
                )
            }
            entry<ReadPalletDestination> { key ->
                ReadPalletRoute(
                    palletUid = key.palletUid,
                    onBack = goBack
                )
            }
        },
        onBack = goBack,
        entryDecorators = entryDecorators
    )
}
