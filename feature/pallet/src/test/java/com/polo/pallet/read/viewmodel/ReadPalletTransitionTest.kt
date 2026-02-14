package com.polo.pallet.read.viewmodel

import com.polo.domain.model.PalletStatus.CREATED
import com.polo.domain.model.PalletStatus.READY
import com.polo.domain.model.PalletStatus.TRANSPORT
import com.polo.domain.model.WarehouseIds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadPalletTransitionTest {

    @Test
    fun `resolveStatusTransition marks dissolve for final destination`() {
        val transition = resolveStatusTransition(
            currentStatus = READY,
            currentWarehouseUid = WarehouseIds.ZABLACE,
            isFinalDestination = true
        )

        assertTrue(transition.shouldDissolve)
        assertEquals(READY, transition.status)
        assertEquals(WarehouseIds.ZABLACE, transition.warehouseUid)
    }

    @Test
    fun `resolveStatusTransition maps created to ready in same warehouse`() {
        val transition = resolveStatusTransition(
            currentStatus = CREATED,
            currentWarehouseUid = WarehouseIds.PRISLONICA,
            isFinalDestination = false
        )

        assertFalse(transition.shouldDissolve)
        assertEquals(READY, transition.status)
        assertEquals(WarehouseIds.PRISLONICA, transition.warehouseUid)
    }

    @Test
    fun `resolveStatusTransition maps ready to transport in same warehouse`() {
        val transition = resolveStatusTransition(
            currentStatus = READY,
            currentWarehouseUid = WarehouseIds.PRISLONICA,
            isFinalDestination = false
        )

        assertFalse(transition.shouldDissolve)
        assertEquals(TRANSPORT, transition.status)
        assertEquals(WarehouseIds.PRISLONICA, transition.warehouseUid)
    }

    @Test
    fun `resolveStatusTransition maps transport to ready in zablace`() {
        val transition = resolveStatusTransition(
            currentStatus = TRANSPORT,
            currentWarehouseUid = WarehouseIds.PRISLONICA,
            isFinalDestination = false
        )

        assertFalse(transition.shouldDissolve)
        assertEquals(READY, transition.status)
        assertEquals(WarehouseIds.ZABLACE, transition.warehouseUid)
    }
}
