package com.polo.data.datasource

import com.polo.data.functional.Either
import com.polo.data.model.CreatePallet
import com.polo.data.model.CreatePallet.PalletStatus
import com.polo.data.model.CreatePallet.PalletStatus.CREATED
import com.polo.data.model.PalletDocument
import com.polo.data.model.ProductDocument
import com.polo.data.model.WarehouseDocument
import kotlinx.coroutines.flow.Flow

interface IFireStoreDataSource {

    suspend fun updatePalletStatus(palletUid: String, status: PalletStatus): Either<Exception, Unit>

    suspend fun updatePalletStatus(palletUid: String, status: PalletStatus, warehouseUid: String): Either<Exception, Unit>

    suspend fun getAllPallets(status: PalletStatus = CREATED): Flow<Either<Exception, List<PalletDocument>>>

    suspend fun getPallet(palletUid: String): Either<Exception, PalletDocument>

    suspend fun getProducts(productUid: String): Either<Exception, ProductDocument>

    suspend fun getWarehouse(warehouseUid: String): Either<Exception, WarehouseDocument>

    suspend fun createPallets(pallet: CreatePallet): Either<Exception, Unit>

    suspend fun deletePallet(palletUid: String): Either<Exception, Unit>

    suspend fun getAllProducts(): Either<Exception, List<ProductDocument>>

    suspend fun getAllWarehouses(): Either<Exception, List<WarehouseDocument>>

    suspend fun queryForProduct(query: String): Either<Exception, List<ProductDocument>>

    suspend fun getAllProductsAndWarehouses(): Either<Exception, Pair<List<ProductDocument>, List<WarehouseDocument>>>
}