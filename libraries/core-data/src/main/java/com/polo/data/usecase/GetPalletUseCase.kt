package com.polo.data.usecase

import com.polo.data.datasource.IFireStoreDataSource
import com.polo.domain.functional.Either
import com.polo.data.model.Pallet
import com.polo.data.model.PalletDocument
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetPalletUseCase @Inject constructor(
    private val firebaseDataSource: IFireStoreDataSource
) {

    suspend operator fun invoke(palletUid: String): Either<Exception, Pallet> {
        return when (val palletResult = firebaseDataSource.getPallet(palletUid)) {
            is Either.Error -> Either.Error(palletResult.data)
            is Either.Result -> handleSuccess(palletResult.data)
        }
    }

    private suspend fun handleSuccess(
        palletDocument: PalletDocument
    ): Either<Exception, Pallet> = coroutineScope {

        val productResult = async { firebaseDataSource.getProducts(palletDocument.productUid) }.await()
        val warehouseResult = async { firebaseDataSource.getWarehouse(palletDocument.warehouseUid) }.await()

        return@coroutineScope when {
            productResult.isResult && warehouseResult.isResult -> Either.Result(
                Pallet(
                    uid = palletDocument.uid,
                    date = palletDocument.date.toString(),
                    productName = productResult.result().name,
                    productAmount = palletDocument.productAmount,
                    createdBy = palletDocument.createdBy,
                    warehouseName = warehouseResult.result().name,
                    status = palletDocument.status
                )
            )
            productResult.isError -> Either.Error(productResult.error())
            warehouseResult.isError -> Either.Error(warehouseResult.error())
            else -> Either.Error(Exception("Something went wrong"))
        }
    }

}
