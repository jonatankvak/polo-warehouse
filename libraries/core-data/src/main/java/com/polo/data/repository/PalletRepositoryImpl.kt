package com.polo.data.repository

import com.polo.data.datasource.IFireStoreDataSource
import com.polo.data.model.CreatePallet
import com.polo.data.model.PalletDocument
import com.polo.domain.functional.Either
import com.polo.domain.model.CreatePallet as DomainCreatePallet
import com.polo.domain.model.Pallet
import com.polo.domain.model.PalletStatus
import com.polo.domain.repository.PalletRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PalletRepositoryImpl @Inject constructor(
    private val firestoreDataSource: IFireStoreDataSource
) : PalletRepository {

    override suspend fun observePallets(status: PalletStatus): Flow<Either<Exception, List<Pallet>>> {
        return firestoreDataSource.getAllPallets(status.toDataStatus())
            .map { response ->
                when (response) {
                    is Either.Result -> Either.Result(response.data.map { it.toDomain() })
                    is Either.Error -> Either.Error(response.data)
                }
            }
    }

    override suspend fun getPallet(palletUid: String): Either<Exception, Pallet> {
        return when (val response = firestoreDataSource.getPallet(palletUid)) {
            is Either.Result -> Either.Result(response.data.toDomain())
            is Either.Error -> Either.Error(response.data)
        }
    }

    override suspend fun updateStatus(
        palletUid: String,
        status: PalletStatus,
        warehouseUid: String?
    ): Either<Exception, Unit> {
        return if (warehouseUid.isNullOrBlank()) {
            firestoreDataSource.updatePalletStatus(palletUid, status.toDataStatus())
        } else {
            firestoreDataSource.updatePalletStatus(palletUid, status.toDataStatus(), warehouseUid)
        }
    }

    override suspend fun createPallet(pallet: DomainCreatePallet): Either<Exception, Unit> {
        return firestoreDataSource.createPallets(pallet.toDataCreate())
    }

    override suspend fun deletePallet(palletUid: String): Either<Exception, Unit> {
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
