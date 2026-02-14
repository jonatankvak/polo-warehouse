package com.polo.scanner.read

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScanPalletScreenTest {

    @Test
    fun `isDuplicateQuickRescan is true for same code inside time window`() {
        val isDuplicate = isDuplicateQuickRescan(
            normalizedCode = "PAL-001",
            lastScannedCode = "PAL-001",
            lastScannedAtMs = 1_000L,
            nowMs = 3_500L,
            thresholdMs = 3_000L
        )

        assertTrue(isDuplicate)
    }

    @Test
    fun `isDuplicateQuickRescan is false for same code outside time window`() {
        val isDuplicate = isDuplicateQuickRescan(
            normalizedCode = "PAL-001",
            lastScannedCode = "PAL-001",
            lastScannedAtMs = 1_000L,
            nowMs = 4_500L,
            thresholdMs = 3_000L
        )

        assertFalse(isDuplicate)
    }

    @Test
    fun `isDuplicateQuickRescan is false for different code`() {
        val isDuplicate = isDuplicateQuickRescan(
            normalizedCode = "PAL-002",
            lastScannedCode = "PAL-001",
            lastScannedAtMs = 1_000L,
            nowMs = 1_100L,
            thresholdMs = 3_000L
        )

        assertFalse(isDuplicate)
    }
}
