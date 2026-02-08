package com.polo.data.repository

import com.polo.data.datasource.FirestoreDataSource
import com.polo.data.model.WarehouseDocument
import com.polo.domain.model.Warehouse
import com.polo.domain.repository.WarehouseRepository
import javax.inject.Inject

class WarehouseRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) : WarehouseRepository {

    override suspend fun getAllWarehouses(): Result<List<Warehouse>> {
        return firestoreDataSource.getAllWarehouses().map { warehouseDocuments -> warehouseDocuments.map { it.toDomain() } }
    }

    override suspend fun getWarehouse(warehouseUid: String): Result<Warehouse> {
        return firestoreDataSource.getWarehouse(warehouseUid).map { it.toDomain() }
    }

    private fun WarehouseDocument.toDomain(): Warehouse {
        return Warehouse(
            uid = uid,
            name = name
        )
    }
}
