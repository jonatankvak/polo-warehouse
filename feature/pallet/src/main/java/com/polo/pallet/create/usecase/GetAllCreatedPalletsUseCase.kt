package com.polo.pallet.create.usecase

import com.polo.core_ui.model.UiPallet
import com.polo.core_ui.model.UiProduct
import com.polo.core_ui.model.UiWarehouse
import com.polo.domain.functional.Either
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
    ): Flow<Either<Exception, List<UiPallet>>> {
        return withContext(Dispatchers.IO) {
            palletRepository.observePallets(CREATED)
                .map { response ->
                    when(response) {
                        is Either.Result -> Either.Result(response.data.map { map(it, products, warehouses) })
                        is Either.Error -> response
                    }
                }
        }
    }

    private fun map(
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
