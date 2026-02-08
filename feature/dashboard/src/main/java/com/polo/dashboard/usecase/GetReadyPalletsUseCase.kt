package com.polo.dashboard.usecase

import com.polo.dashboard.viewmodel.PalletListUiModel.PalletListUiBody
import com.polo.dashboard.viewmodel.PalletListUiModel.PalletListUiHeader
import com.polo.domain.model.Pallet
import com.polo.domain.model.PalletStatus.READY
import com.polo.domain.model.Product
import com.polo.domain.model.Warehouse
import com.polo.domain.repository.PalletRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GetReadyPalletsUseCase @Inject constructor(
    private val palletRepository: PalletRepository
) {

    suspend operator fun invoke(
        products: List<Product>,
        warehouses: List<Warehouse>
    ): Flow<Result<Map<PalletListUiHeader, List<PalletListUiBody>>>> {
        return withContext(Dispatchers.IO) {
            palletRepository.observePallets(READY)
                .map { result ->
                    result.mapCatching { pallets ->
                        createPalletListUiModel(
                            pallets,
                            warehouses,
                            products
                        )
                    }
                }
        }
    }

    private fun createPalletListUiModel(
        pallets: List<Pallet>,
        warehouses: List<Warehouse>,
        products: List<Product>
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

    private fun createPalletListUiBodies(palletsGroupedByProduct: Collection<List<Pallet>>, products: List<Product>): List<PalletListUiBody> {

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

    private fun getWarehouseName(warehouseUid: String, warehouses: List<Warehouse>): String {
        return warehouses.findLast { it.uid == warehouseUid }?.name ?: throw Exception("Unsupported warehouse uid $warehouseUid")
    }

    private fun getProductName(productUid: String, products: List<Product>): String {
        return products.findLast { it.uid == productUid }?.name ?: throw Exception("Unsupported product uid $productUid")
    }
}
