package com.polo.data.repository

import com.polo.data.datasource.IFireStoreDataSource
import com.polo.data.model.WarehouseDocument
import com.polo.domain.functional.Either
import com.polo.domain.model.Warehouse
import com.polo.domain.repository.WarehouseRepository
import javax.inject.Inject

class WarehouseRepositoryImpl @Inject constructor(
    private val firestoreDataSource: IFireStoreDataSource
) : WarehouseRepository {

    override suspend fun getAllWarehouses(): Either<Exception, List<Warehouse>> {
        return when (val response = firestoreDataSource.getAllWarehouses()) {
            is Either.Result -> Either.Result(response.data.map { it.toDomain() })
            is Either.Error -> Either.Error(response.data)
        }
    }

    override suspend fun getWarehouse(warehouseUid: String): Either<Exception, Warehouse> {
        return when (val response = firestoreDataSource.getWarehouse(warehouseUid)) {
            is Either.Result -> Either.Result(response.data.toDomain())
            is Either.Error -> Either.Error(response.data)
        }
    }

    private fun WarehouseDocument.toDomain(): Warehouse {
        return Warehouse(
            uid = uid,
            name = name
        )
    }
}
