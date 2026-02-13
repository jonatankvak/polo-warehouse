package com.polo.warehouse.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.polo.authentication.api.navigation.AuthenticationDestination
import com.polo.authentication.view.VerificationRoute
import com.polo.dashboard.api.navigation.DashboardDestination
import com.polo.dashboard.view.DashboardRoute
import com.polo.pallet.api.navigation.CreatePalletDestination
import com.polo.pallet.api.navigation.ReadPalletDestination
import com.polo.pallet.create.view.CreatePalletRoute
import com.polo.pallet.read.view.ReadPalletRoute
import com.polo.scanner.api.navigation.EditPalletScannerDestination
import com.polo.scanner.api.navigation.VerifyScannerDestination
import com.polo.scanner.read.ScanPalletRoute
import com.polo.scanner.verify.view.VerifyPalletScannerRoute
import com.polo.ui.R as CoreUiR

@Composable
fun PoloWarehouseNavHost(
    startDestination: NavKey
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
        navigateToTopLevel(
            backStack = backStack,
            destination = DashboardDestination
        )
    }

    val goToCreate: () -> Unit = {
        navigateToTopLevel(
            backStack = backStack,
            destination = CreatePalletDestination
        )
    }

    val goToScan: () -> Unit = {
        navigateToTopLevel(
            backStack = backStack,
            destination = EditPalletScannerDestination
        )
    }

    val selectedTab = when (backStack.lastOrNull()) {
        DashboardDestination -> MainTab.Dashboard
        EditPalletScannerDestination -> MainTab.Scan
        CreatePalletDestination -> MainTab.Create
        else -> null
    }

    Scaffold(
        bottomBar = {
            if (selectedTab != null) {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == MainTab.Dashboard,
                        onClick = {
                            if (selectedTab != MainTab.Dashboard) {
                                goToDashboard()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null
                            )
                        },
                        label = { Text(text = stringResource(id = CoreUiR.string.nav_dashboard)) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == MainTab.Scan,
                        onClick = {
                            if (selectedTab != MainTab.Scan) {
                                goToScan()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null
                            )
                        },
                        label = { Text(text = stringResource(id = CoreUiR.string.nav_scan)) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == MainTab.Create,
                        onClick = {
                            if (selectedTab != MainTab.Create) {
                                goToCreate()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                        },
                        label = { Text(text = stringResource(id = CoreUiR.string.nav_create)) }
                    )
                }
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding)
        ) {
            NavDisplay(
                backStack = backStack,
                transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(durationMillis = 260)
                    ) + fadeIn(animationSpec = tween(durationMillis = 220)) togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth / 4 },
                            animationSpec = tween(durationMillis = 260)
                        ) + fadeOut(animationSpec = tween(durationMillis = 220))
                },
                popTransitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth / 3 },
                        animationSpec = tween(durationMillis = 260)
                    ) + fadeIn(animationSpec = tween(durationMillis = 220)) togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(durationMillis = 260)
                        ) + fadeOut(animationSpec = tween(durationMillis = 220))
                },
                entryProvider = entryProvider {
                    entry<AuthenticationDestination> {
                        VerificationRoute(onSignedIn = goToDashboard)
                    }
                    entry<DashboardDestination> {
                        DashboardRoute()
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
    }
}

private enum class MainTab {
    Dashboard,
    Scan,
    Create
}

private fun navigateToTopLevel(
    backStack: MutableList<NavKey>,
    destination: NavKey
) {
    val topLevelDestinations: Set<NavKey> = setOf(
        DashboardDestination,
        EditPalletScannerDestination,
        CreatePalletDestination
    )

    val existingIndex = backStack.indexOfLast { it == destination }
    if (existingIndex >= 0) {
        while (backStack.lastIndex > existingIndex) {
            backStack.removeAt(backStack.lastIndex)
        }
        return
    }

    val last = backStack.lastOrNull()
    if (backStack.size > 1 && last in topLevelDestinations) {
        backStack.removeAt(backStack.lastIndex)
    }

    backStack.add(destination)
}
