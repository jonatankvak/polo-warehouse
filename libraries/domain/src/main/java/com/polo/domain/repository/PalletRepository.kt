package com.polo.domain.repository

import com.polo.domain.functional.Either
import com.polo.domain.model.CreatePallet
import com.polo.domain.model.Pallet
import com.polo.domain.model.PalletStatus
import kotlinx.coroutines.flow.Flow

interface PalletRepository {
    suspend fun observePallets(status: PalletStatus): Flow<Either<Exception, List<Pallet>>>

    suspend fun getPallet(palletUid: String): Either<Exception, Pallet>

    suspend fun updateStatus(
        palletUid: String,
        status: PalletStatus,
        warehouseUid: String? = null
    ): Either<Exception, Unit>

    suspend fun createPallet(pallet: CreatePallet): Either<Exception, Unit>

    suspend fun deletePallet(palletUid: String): Either<Exception, Unit>
}
