package com.polo.pallet.create.usecase

import com.polo.core_ui.model.UiProduct
import com.polo.core_ui.model.UiWarehouse
import com.polo.domain.functional.Either
import com.polo.domain.model.Product
import com.polo.domain.model.Warehouse
import com.polo.domain.repository.ProductRepository
import com.polo.domain.repository.WarehouseRepository
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetAllProductsAndWarehousesUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val warehouseRepository: WarehouseRepository
) {

    suspend operator fun invoke(): Either<Exception, Pair<List<UiProduct>, List<UiWarehouse>>> {
        return coroutineScope {
            val productsDeferred = async { productRepository.getAllProducts() }
            val warehousesDeferred = async { warehouseRepository.getAllWarehouses() }

            val productsResult = productsDeferred.await()
            val warehousesResult = warehousesDeferred.await()

            when {
                productsResult is Either.Error -> Either.Error(productsResult.data)
                warehousesResult is Either.Error -> Either.Error(warehousesResult.data)
                else -> {
                    val products = (productsResult as Either.Result).data
                    val warehouses = (warehousesResult as Either.Result).data
                    Either.Result(
                        Pair(
                            first = products.map { it.toUi() },
                            second = warehouses.map { it.toUi() }
                        )
                    )
                }
            }
        }
    }

    private fun Product.toUi(): UiProduct {
        return UiProduct(
            uid = uid,
            idNumber = idNumber,
            name = name,
            barCode = barCode,
            price = price,
            transportPackage = transportPackage
        )
    }

    private fun Warehouse.toUi(): UiWarehouse {
        return UiWarehouse(
            uid = uid,
            name = name
        )
    }
}
