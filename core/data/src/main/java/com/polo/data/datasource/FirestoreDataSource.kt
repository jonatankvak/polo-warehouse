package com.polo.data.datasource

import com.polo.data.model.CreatePallet
import com.polo.data.model.CreatePallet.PalletStatus
import com.polo.data.model.CreatePallet.PalletStatus.CREATED
import com.polo.data.model.PalletDocument
import com.polo.data.model.ProductDocument
import com.polo.data.model.WarehouseDocument
import kotlinx.coroutines.flow.Flow

interface FirestoreDataSource {

    suspend fun updatePalletStatus(palletUid: String, status: PalletStatus): Result<Unit>

    suspend fun updatePalletStatus(palletUid: String, status: PalletStatus, warehouseUid: String): Result<Unit>

    suspend fun getAllPallets(status: PalletStatus = CREATED): Flow<Result<List<PalletDocument>>>

    suspend fun getPallet(palletUid: String): Result<PalletDocument>

    suspend fun getProducts(productUid: String): Result<ProductDocument>

    suspend fun getWarehouse(warehouseUid: String): Result<WarehouseDocument>

    suspend fun createPallets(pallet: CreatePallet): Result<Unit>

    suspend fun deletePallet(palletUid: String): Result<Unit>

    suspend fun getAllProducts(): Result<List<ProductDocument>>

    suspend fun getAllWarehouses(): Result<List<WarehouseDocument>>

    suspend fun queryForProduct(query: String): Result<List<ProductDocument>>

    suspend fun getAllProductsAndWarehouses(): Result<Pair<List<ProductDocument>, List<WarehouseDocument>>>
}
