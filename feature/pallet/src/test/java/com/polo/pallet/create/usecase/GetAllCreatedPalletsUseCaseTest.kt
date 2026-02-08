package com.polo.pallet.create.usecase

import com.polo.ui.model.UiProduct
import com.polo.ui.model.UiWarehouse
import com.polo.domain.model.Pallet
import com.polo.domain.model.PalletStatus
import com.polo.domain.repository.PalletRepository
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetAllCreatedPalletsUseCaseTest {

    @Test
    fun `maps pallets to ui models`() = runTest {
        val pallets = listOf(
            Pallet(
                uid = "p1",
                dateEpochMillis = Instant.parse("2024-01-01T00:00:00Z").toEpochMilli(),
                productUid = "prod1",
                productAmount = 5,
                createdBy = "Alex",
                warehouseUid = "w1",
                status = PalletStatus.CREATED
            )
        )
        val products = listOf(UiProduct(uid = "prod1", name = "Widget"))
        val warehouses = listOf(UiWarehouse(uid = "w1", name = "Main"))

        val useCase = GetAllCreatedPalletsUseCase(FakePalletRepository(pallets))

        val resultFlow = useCase(products, warehouses)
        val result = resultFlow.first()

        assertTrue(result.isSuccess)
        val uiPallets = result.getOrThrow()
        assertEquals(1, uiPallets.size)
        assertEquals("Widget", uiPallets.first().productName)
        assertEquals("Main", uiPallets.first().warehouseName)
        assertEquals(PalletStatus.CREATED, uiPallets.first().status)
    }

    private class FakePalletRepository(
        private val pallets: List<Pallet>
    ) : PalletRepository {
        override suspend fun observePallets(status: PalletStatus): Flow<Result<List<Pallet>>> {
            return flowOf(Result.success(pallets))
        }

        override suspend fun getPallet(palletUid: String): Result<Pallet> = throw UnsupportedOperationException()

        override suspend fun updateStatus(
            palletUid: String,
            status: PalletStatus,
            warehouseUid: String?
        ): Result<Unit> = throw UnsupportedOperationException()

        override suspend fun createPallet(pallet: com.polo.domain.model.CreatePallet): Result<Unit> = throw UnsupportedOperationException()

        override suspend fun deletePallet(palletUid: String): Result<Unit> = throw UnsupportedOperationException()
    }
}
