package com.polo.pallet.create.usecase

import com.polo.core_ui.model.UiProduct
import com.polo.core_ui.model.UiWarehouse
import com.polo.data.datasource.IFireStoreDataSource
import com.polo.data.functional.Either
import com.polo.data.model.ProductDocument
import com.polo.data.model.WarehouseDocument
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetAllProductsAndWarehousesUseCase @Inject constructor(
    private val firestoreDataSource: IFireStoreDataSource
) {

    suspend operator fun invoke(): Either<Exception, Pair<List<UiProduct>, List<UiWarehouse>>> {
        return withContext(Dispatchers.IO) {
            firestoreDataSource.getAllProductsAndWarehouses()
                .either(
                    ::handleException,
                    ::handleSuccess
                ) as Either<Exception, Pair<List<UiProduct>, List<UiWarehouse>>>
        }
    }

    private suspend fun handleSuccess(
        response: Pair<List<ProductDocument>, List<WarehouseDocument>>
    ): Either<Exception, Pair<List<UiProduct>, List<UiWarehouse>>> {
        return withContext(Dispatchers.Default) {
            Either.Result(
                Pair(
                    first = response.first.map {
                        UiProduct(
                            uid = it.uid,
                            idNumber = it.idNumber,
                            name = it.name,
                            barCode = it.barCode,
                            price = it.price,
                            transportPackage = it.transportPackage
                        )
                    },
                    second = response.second.map {
                        UiWarehouse(
                            uid = it.uid,
                            name = it.name
                        )
                    }
                )
            )
        }
    }

    private suspend fun handleException(
        exception: Exception
    ): Either<Exception, Pair<List<UiProduct>, List<UiWarehouse>>> {

        return Either.Error(exception)
    }
}