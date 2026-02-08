package com.polo.domain.repository

import com.polo.domain.model.Warehouse

interface WarehouseRepository {
    suspend fun getAllWarehouses(): Result<List<Warehouse>>

    suspend fun getWarehouse(warehouseUid: String): Result<Warehouse>
}
