package com.polo.data.repository

import com.polo.data.datasource.FirestoreDataSource
import com.polo.data.model.CreatePallet
import com.polo.data.model.PalletDocument
import com.polo.domain.model.CreatePallet as DomainCreatePallet
import com.polo.domain.model.Pallet
import com.polo.domain.model.PalletStatus
import com.polo.domain.repository.PalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single(binds = [PalletRepository::class])
class PalletRepositoryImpl(
    private val firestoreDataSource: FirestoreDataSource
) : PalletRepository {

    override suspend fun observePallets(status: PalletStatus): Flow<Result<List<Pallet>>> {
        return firestoreDataSource.getAllPallets(status.toDataStatus())
            .map { response -> response.map { palletDocuments -> palletDocuments.map { it.toDomain() } } }
    }

    override suspend fun getPallet(palletUid: String): Result<Pallet> {
        return firestoreDataSource.getPallet(palletUid).map { it.toDomain() }
    }

    override suspend fun updateStatus(
        palletUid: String,
        status: PalletStatus,
        warehouseUid: String?
    ): Result<Unit> {
        return if (warehouseUid.isNullOrBlank()) {
            firestoreDataSource.updatePalletStatus(palletUid, status.toDataStatus())
        } else {
            firestoreDataSource.updatePalletStatus(palletUid, status.toDataStatus(), warehouseUid)
        }
    }

    override suspend fun createPallet(pallet: DomainCreatePallet): Result<Unit> {
        return firestoreDataSource.createPallets(pallet.toDataCreate())
    }

    override suspend fun deletePallet(palletUid: String): Result<Unit> {
        return firestoreDataSource.deletePallet(palletUid)
    }

    private fun PalletDocument.toDomain(): Pallet {
        return Pallet(
            uid = uid,
            dateEpochMillis = date.toDate().time,
            productUid = productUid,
            productAmount = productAmount,
            createdBy = createdBy,
            warehouseUid = warehouseUid,
            status = status.toDomainStatus()
        )
    }

    private fun DomainCreatePallet.toDataCreate(): CreatePallet {
        return CreatePallet(
            productUid = productUid,
            productAmount = productAmount,
            createdBy = createdBy,
            warehouseUid = warehouseUid,
            status = status.toDataStatus()
        )
    }

    private fun PalletStatus.toDataStatus(): CreatePallet.PalletStatus {
        return when (this) {
            PalletStatus.CREATED -> CreatePallet.PalletStatus.CREATED
            PalletStatus.READY -> CreatePallet.PalletStatus.READY
            PalletStatus.TRANSPORT -> CreatePallet.PalletStatus.TRANSPORT
        }
    }

    private fun CreatePallet.PalletStatus.toDomainStatus(): PalletStatus {
        return when (this) {
            CreatePallet.PalletStatus.CREATED -> PalletStatus.CREATED
            CreatePallet.PalletStatus.READY -> PalletStatus.READY
            CreatePallet.PalletStatus.TRANSPORT -> PalletStatus.TRANSPORT
        }
    }
}
