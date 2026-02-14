package com.polo.pallet.create.usecase

import com.polo.ui.model.UiProduct
import com.polo.ui.model.UiWarehouse
import com.polo.domain.model.Product
import com.polo.domain.model.Warehouse
import com.polo.domain.repository.ProductRepository
import com.polo.domain.repository.WarehouseRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.koin.core.annotation.Factory

@Factory
class GetAllProductsAndWarehousesUseCase(
    private val productRepository: ProductRepository,
    private val warehouseRepository: WarehouseRepository
) {

    suspend operator fun invoke(): Result<Pair<List<UiProduct>, List<UiWarehouse>>> {
        return coroutineScope {
            val productsDeferred = async { productRepository.getAllProducts() }
            val warehousesDeferred = async { warehouseRepository.getAllWarehouses() }

            val productsResult = productsDeferred.await()
            val warehousesResult = warehousesDeferred.await()

            productsResult.fold(
                onSuccess = { products ->
                    warehousesResult.map { warehouses ->
                        Pair(
                            first = products.map { it.toUi() },
                            second = warehouses.map { it.toUi() }
                        )
                    }
                },
                onFailure = { throwable ->
                    Result.failure(throwable)
                }
            )
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
