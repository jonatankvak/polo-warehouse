package com.polo.dashboard.usecase

import com.polo.dashboard.viewmodel.PalletListUiModel.PalletListUiBody
import com.polo.dashboard.viewmodel.PalletListUiModel.PalletListUiHeader
import com.polo.data.datasource.IFireStoreDataSource
import com.polo.data.functional.Either
import com.polo.data.model.CreatePallet.PalletStatus.READY
import com.polo.data.model.PalletDocument
import com.polo.data.model.ProductDocument
import com.polo.data.model.WarehouseDocument
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GetReadyPalletsUseCase @Inject constructor(
    private val firestoreDataSource: IFireStoreDataSource
) {

    suspend operator fun invoke(
        products: List<ProductDocument>,
        warehouses: List<WarehouseDocument>
    ): Flow<Either<Exception, Map<PalletListUiHeader, List<PalletListUiBody>>>> {
        return withContext(Dispatchers.IO) {
            firestoreDataSource.getAllPallets(READY)
                .map {
                    map(it, warehouses, products)
                }
        }
    }

    private fun map(
        response: Either<Exception, List<PalletDocument>>,
        warehouses: List<WarehouseDocument>,
        products: List<ProductDocument>
    ): Either<Exception, Map<PalletListUiHeader, List<PalletListUiBody>>> {

        if (response.isError) return response as Either.Error

        return Either.Result(
                createPalletListUiModel(
                response.result(),
                warehouses,
                products
            )
        )
    }

    private fun createPalletListUiModel(
        pallets: List<PalletDocument>,
        warehouses: List<WarehouseDocument>,
        products: List<ProductDocument>
    ): Map<PalletListUiHeader, List<PalletListUiBody>> {

        val palletListUiModelMap = mutableMapOf<PalletListUiHeader, List<PalletListUiBody>>()
        val palletsGroupedByWarehouse = pallets.groupBy { it.warehouseUid }

        for ((warehouseUid, palletsInWarehouse) in palletsGroupedByWarehouse) {
            val warehouseName = getWarehouseName(warehouseUid, warehouses)
            val header = PalletListUiHeader(warehouseName)

            val palletsGroupedByProduct = palletsInWarehouse.groupBy { it.productUid }.values
            val bodies = createPalletListUiBodies(palletsGroupedByProduct, products)

            palletListUiModelMap[header] = bodies
        }
        return palletListUiModelMap
    }

    private fun createPalletListUiBodies(palletsGroupedByProduct: Collection<List<PalletDocument>>, products: List<ProductDocument>): List<PalletListUiBody> {

        val bodies = mutableListOf<PalletListUiBody>()

        for (palletsWithSameProduct in palletsGroupedByProduct) {

            val productUid = palletsWithSameProduct.first().productUid
            val productName = getProductName(productUid, products)
            val amount = palletsWithSameProduct.first().productAmount
            val count = palletsWithSameProduct.size
            val body = PalletListUiBody(productName, amount, count)

            bodies.add(body)
        }

        return bodies
    }

    private fun getWarehouseName(warehouseUid: String, warehouses: List<WarehouseDocument>): String {
        return warehouses.findLast { it.uid == warehouseUid }?.name ?: throw Exception("Unsupported warehouse uid $warehouseUid")
    }

    private fun getProductName(productUid: String, products: List<ProductDocument>): String {
        return products.findLast { it.uid == productUid }?.name ?: throw Exception("Unsupported product uid $productUid")
    }
}