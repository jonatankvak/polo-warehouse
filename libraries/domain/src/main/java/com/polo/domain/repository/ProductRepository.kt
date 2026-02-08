package com.polo.domain.repository

import com.polo.domain.functional.Either
import com.polo.domain.model.Product

interface ProductRepository {
    suspend fun getAllProducts(): Either<Exception, List<Product>>

    suspend fun getProduct(productUid: String): Either<Exception, Product>

    suspend fun queryProductsByName(query: String): Either<Exception, List<Product>>
}
