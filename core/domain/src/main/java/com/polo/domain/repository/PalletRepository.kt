package com.polo.domain.repository

import com.polo.domain.model.CreatePallet
import com.polo.domain.model.Pallet
import com.polo.domain.model.PalletStatus
import kotlinx.coroutines.flow.Flow

interface PalletRepository {
    suspend fun observePallets(status: PalletStatus): Flow<Result<List<Pallet>>>

    suspend fun getPallet(palletUid: String): Result<Pallet>

    suspend fun updateStatus(
        palletUid: String,
        status: PalletStatus,
        warehouseUid: String? = null
    ): Result<Unit>

    suspend fun createPallet(pallet: CreatePallet): Result<Unit>

    suspend fun deletePallet(palletUid: String): Result<Unit>
}
