package com.polo.scanner.verify.viewmodel

import com.polo.core_ui.model.UiPallet
import com.polo.domain.functional.Either
import com.polo.domain.model.CreatePallet
import com.polo.domain.model.Pallet
import com.polo.domain.model.PalletStatus
import com.polo.domain.repository.PalletRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class VerifyPalletScannerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `onQrCodeScanned updates status and clears loading`() = runTest {
        val repository = FakePalletRepository()
        val viewModel = VerifyPalletScannerViewModel(repository)
        val pallet = UiPallet(uid = "123")

        viewModel.onQrCodeScanned("123", pallet)
        advanceUntilIdle()

        assertTrue(repository.updateCalled)
        assertFalse(viewModel.state.value.isLoading)
    }

    private class FakePalletRepository : PalletRepository {
        var updateCalled = false

        override suspend fun observePallets(status: PalletStatus): Flow<Either<Exception, List<Pallet>>> {
            throw UnsupportedOperationException()
        }

        override suspend fun getPallet(palletUid: String): Either<Exception, Pallet> {
            throw UnsupportedOperationException()
        }

        override suspend fun updateStatus(
            palletUid: String,
            status: PalletStatus,
            warehouseUid: String?
        ): Either<Exception, Unit> {
            updateCalled = true
            return Either.Result(Unit)
        }

        override suspend fun createPallet(pallet: CreatePallet): Either<Exception, Unit> {
            throw UnsupportedOperationException()
        }

        override suspend fun deletePallet(palletUid: String): Either<Exception, Unit> {
            throw UnsupportedOperationException()
        }
    }

    class MainDispatcherRule(
        private val dispatcher: CoroutineDispatcher = Dispatchers.Unconfined
    ) : TestWatcher() {
        override fun starting(description: Description) {
            Dispatchers.setMain(dispatcher)
        }

        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }
}
