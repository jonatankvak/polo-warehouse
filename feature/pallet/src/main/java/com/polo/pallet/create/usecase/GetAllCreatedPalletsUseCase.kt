package com.polo.pallet.create.usecase

import com.polo.core_ui.model.UiPallet
import com.polo.core_ui.model.UiProduct
import com.polo.core_ui.model.UiWarehouse
import com.polo.data.datasource.IFireStoreDataSource
import com.polo.data.functional.Either
import com.polo.data.functional.Either.Error
import com.polo.data.functional.Either.Result
import com.polo.data.model.CreatePallet.PalletStatus.CREATED
import com.polo.data.model.PalletDocument
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle.SHORT
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GetAllCreatedPalletsUseCase @Inject constructor(
    private val firestoreDataSource: IFireStoreDataSource
) {

    suspend operator fun invoke(
        products: List<UiProduct>,
        warehouses: List<UiWarehouse>
    ): Flow<Either<Exception, List<UiPallet>>> {
        return withContext(Dispatchers.IO) {
            firestoreDataSource.getAllPallets(CREATED)
                .map { response ->
                    when(response) {
                        is Result -> Result(response.data.map { map(it, products, warehouses) })
                        is Error -> response
                    }
                }
        }
    }

    private fun map(
        palletDocument: PalletDocument,
        products: List<UiProduct>,
        warehouses: List<UiWarehouse>
    ): UiPallet {
        return UiPallet(
            uid = palletDocument.uid,
            date = palletDocument.date.toDate().toInstant()
                .atZone(ZoneId.systemDefault()).format(
                    DateTimeFormatter.ofLocalizedDateTime(SHORT)
                ),
            productName = products.findLast { it.uid == palletDocument.productUid }?.name
                ?: "",
            productAmount = palletDocument.productAmount,
            createdBy = palletDocument.createdBy,
            warehouseName = warehouses.findLast { it.uid == palletDocument.warehouseUid }?.name
                ?: "",
            status = palletDocument.status
        )
    }
}