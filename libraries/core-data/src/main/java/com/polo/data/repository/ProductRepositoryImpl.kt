package com.polo.data.repository

import com.polo.data.datasource.IFireStoreDataSource
import com.polo.data.model.ProductDocument
import com.polo.domain.functional.Either
import com.polo.domain.model.Product
import com.polo.domain.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val firestoreDataSource: IFireStoreDataSource
) : ProductRepository {

    override suspend fun getAllProducts(): Either<Exception, List<Product>> {
        return when (val response = firestoreDataSource.getAllProducts()) {
            is Either.Result -> Either.Result(response.data.map { it.toDomain() })
            is Either.Error -> Either.Error(response.data)
        }
    }

    override suspend fun getProduct(productUid: String): Either<Exception, Product> {
        return when (val response = firestoreDataSource.getProducts(productUid)) {
            is Either.Result -> Either.Result(response.data.toDomain())
            is Either.Error -> Either.Error(response.data)
        }
    }

    override suspend fun queryProductsByName(query: String): Either<Exception, List<Product>> {
        return when (val response = firestoreDataSource.queryForProduct(query)) {
            is Either.Result -> Either.Result(response.data.map { it.toDomain() })
            is Either.Error -> Either.Error(response.data)
        }
    }

    private fun ProductDocument.toDomain(): Product {
        return Product(
            uid = uid,
            idNumber = idNumber,
            name = name,
            barCode = barCode,
            price = price,
            transportPackage = transportPackage
        )
    }
}
