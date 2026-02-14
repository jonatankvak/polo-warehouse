package com.polo.warehouse.navigation

import com.polo.dashboard.api.navigation.DashboardDestination
import com.polo.pallet.api.navigation.CreatePalletDestination
import com.polo.pallet.api.navigation.ReadPalletDestination
import com.polo.scanner.api.navigation.EditPalletScannerDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppNavigationPolicyTest {

    @Test
    fun `selectedMainTab maps top-level destinations`() {
        assertEquals(MainTab.Dashboard, selectedMainTab(DashboardDestination))
        assertEquals(MainTab.Scan, selectedMainTab(EditPalletScannerDestination))
        assertEquals(MainTab.Create, selectedMainTab(CreatePalletDestination))
    }

    @Test
    fun `selectedMainTab returns null for non-tab destination`() {
        assertNull(selectedMainTab(ReadPalletDestination("PAL-001")))
    }

    @Test
    fun `navigateAfterSignIn clears stack and opens dashboard`() {
        val backStack = mutableListOf(
            ReadPalletDestination("PAL-002"),
            EditPalletScannerDestination,
            CreatePalletDestination
        )

        navigateAfterSignIn(backStack)

        assertEquals(listOf(DashboardDestination), backStack)
    }

    @Test
    fun `navigateToTopLevel pops back to existing tab destination`() {
        val backStack = mutableListOf(
            DashboardDestination,
            EditPalletScannerDestination,
            CreatePalletDestination,
            ReadPalletDestination("PAL-003")
        )

        navigateToTopLevel(backStack, EditPalletScannerDestination)

        assertEquals(
            listOf(DashboardDestination, EditPalletScannerDestination),
            backStack
        )
    }

    @Test
    fun `navigateToTopLevel replaces top-level tab when destination not in stack`() {
        val backStack = mutableListOf(
            DashboardDestination,
            CreatePalletDestination
        )

        navigateToTopLevel(backStack, EditPalletScannerDestination)

        assertEquals(
            listOf(DashboardDestination, EditPalletScannerDestination),
            backStack
        )
    }

    @Test
    fun `navigateToTopLevel appends destination when current top is non-tab`() {
        val backStack = mutableListOf(
            DashboardDestination,
            ReadPalletDestination("PAL-004")
        )

        navigateToTopLevel(backStack, EditPalletScannerDestination)

        assertEquals(
            listOf(
                DashboardDestination,
                ReadPalletDestination("PAL-004"),
                EditPalletScannerDestination
            ),
            backStack
        )
    }
}
