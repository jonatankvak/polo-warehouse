package com.polo.data.usecase

import com.polo.data.datasource.IFireStoreDataSource
import com.polo.data.functional.Either
import com.polo.data.model.Pallet
import com.polo.data.model.PalletDocument
import com.polo.data.model.ProductDocument
import com.polo.data.model.WarehouseDocument
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class GetPalletUseCase @Inject constructor(
    private val firebaseDataSource: IFireStoreDataSource
) {

    suspend operator fun invoke(palletUid: String): Either<Exception, Pallet> {
        return firebaseDataSource.getPallet(palletUid)
            .either(
                ::handleException,
                ::handleSuccess
            ) as Either<Exception, Pallet>
    }

    private suspend fun handleSuccess(
        palletDocument: PalletDocument
    ): Either<Exception, Pallet> = coroutineScope {

        val (productResult, warehouseResult) = listOf(
            async { firebaseDataSource.getProducts(palletDocument.productUid) },
            async { firebaseDataSource.getWarehouse(palletDocument.warehouseUid) }
        ).awaitAll()

        return@coroutineScope when {
            productResult.isResult && warehouseResult.isResult -> Either.Result(
                Pallet(
                    uid = palletDocument.uid,
                    date = palletDocument.date.toString(),
                    productName = (productResult.result() as ProductDocument).name,
                    productAmount = palletDocument.productAmount,
                    createdBy = palletDocument.createdBy,
                    warehouseName = (warehouseResult.result() as WarehouseDocument).name,
                    status = palletDocument.status
                )
            )
            productResult.isError -> Either.Error(productResult.error())
            warehouseResult.isError -> Either.Error(warehouseResult.error())
            else -> Either.Error(Exception("Something went wrong"))
        }
    }

    private fun handleException(exception: Exception): Either<Exception, PalletDocument> {
        return Either.Error(exception)
    }
}