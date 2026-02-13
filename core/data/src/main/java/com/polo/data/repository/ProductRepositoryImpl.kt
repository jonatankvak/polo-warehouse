package com.polo.data.repository

import com.polo.data.datasource.FirestoreDataSource
import com.polo.data.model.ProductDocument
import com.polo.domain.model.Product
import com.polo.domain.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) : ProductRepository {

    override suspend fun getAllProducts(): Result<List<Product>> {
        return firestoreDataSource.getAllProducts().map { productDocuments -> productDocuments.map { it.toDomain() } }
    }

    override suspend fun getProduct(productUid: String): Result<Product> {
        return firestoreDataSource.getProducts(productUid).map { it.toDomain() }
    }

    override suspend fun queryProductsByName(query: String): Result<List<Product>> {
        return firestoreDataSource.queryForProduct(query).map { productDocuments -> productDocuments.map { it.toDomain() } }
    }

    private fun ProductDocument.toDomain(): Product {
        return Product(
            uid = uid,
            idNumber = idNumber,
            name = name,
            barCode = barCode,
            price = price?.takeIf { it > 0f },
            transportPackage = transportPackage
        )
    }
}
