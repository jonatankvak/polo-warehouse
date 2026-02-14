package com.polo.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PalletCardUiTest {

    @Test
    fun `shouldShowPalletActions returns false when both callbacks are null`() {
        assertFalse(
            shouldShowPalletActions(
                onScanControl = null,
                onDeleteControl = null
            )
        )
    }

    @Test
    fun `shouldShowPalletActions returns true when scan callback is provided`() {
        assertTrue(
            shouldShowPalletActions(
                onScanControl = {},
                onDeleteControl = null
            )
        )
    }

    @Test
    fun `shouldShowPalletActions returns true when delete callback is provided`() {
        assertTrue(
            shouldShowPalletActions(
                onScanControl = null,
                onDeleteControl = {}
            )
        )
    }
}
