package com.polo.domain.repository

import com.polo.domain.functional.Either
import com.polo.domain.model.Warehouse

interface WarehouseRepository {
    suspend fun getAllWarehouses(): Either<Exception, List<Warehouse>>

    suspend fun getWarehouse(warehouseUid: String): Either<Exception, Warehouse>
}
