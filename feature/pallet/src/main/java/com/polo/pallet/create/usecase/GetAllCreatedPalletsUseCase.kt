package com.polo.pallet.create.usecase

import com.polo.ui.model.UiPallet
import com.polo.ui.model.UiProduct
import com.polo.ui.model.UiWarehouse
import com.polo.domain.model.Pallet
import com.polo.domain.model.PalletStatus.CREATED
import com.polo.domain.repository.PalletRepository
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle.SHORT
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GetAllCreatedPalletsUseCase @Inject constructor(
    private val palletRepository: PalletRepository
) {

    suspend operator fun invoke(
        products: List<UiProduct>,
        warehouses: List<UiWarehouse>
    ): Flow<Result<List<UiPallet>>> {
        return withContext(Dispatchers.IO) {
            palletRepository.observePallets(CREATED)
                .map { response -> response.map { pallets -> pallets.map { mapToUi(it, products, warehouses) } } }
        }
    }

    private fun mapToUi(
        pallet: Pallet,
        products: List<UiProduct>,
        warehouses: List<UiWarehouse>
    ): UiPallet {
        return UiPallet(
            uid = pallet.uid,
            date = java.time.Instant.ofEpochMilli(pallet.dateEpochMillis)
                .atZone(ZoneId.systemDefault()).format(
                    DateTimeFormatter.ofLocalizedDateTime(SHORT)
                ),
            productName = products.findLast { it.uid == pallet.productUid }?.name
                ?: "",
            productAmount = pallet.productAmount,
            createdBy = pallet.createdBy,
            warehouseName = warehouses.findLast { it.uid == pallet.warehouseUid }?.name
                ?: "",
            status = pallet.status
        )
    }
}
