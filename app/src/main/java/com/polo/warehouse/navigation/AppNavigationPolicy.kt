package com.polo.warehouse.navigation

import androidx.navigation3.runtime.NavKey
import com.polo.dashboard.api.navigation.DashboardDestination
import com.polo.pallet.api.navigation.CreatePalletDestination
import com.polo.scanner.api.navigation.EditPalletScannerDestination

internal enum class MainTab {
    Dashboard,
    Scan,
    Create
}

internal fun selectedMainTab(lastDestination: NavKey?): MainTab? {
    return when (lastDestination) {
        DashboardDestination -> MainTab.Dashboard
        EditPalletScannerDestination -> MainTab.Scan
        CreatePalletDestination -> MainTab.Create
        else -> null
    }
}

internal fun navigateAfterSignIn(backStack: MutableList<NavKey>) {
    backStack.clear()
    backStack.add(DashboardDestination)
}

internal fun navigateToTopLevel(
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
