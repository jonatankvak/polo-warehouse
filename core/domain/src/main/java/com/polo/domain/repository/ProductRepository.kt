package com.polo.domain.repository

import com.polo.domain.model.Product

interface ProductRepository {
    suspend fun getAllProducts(): Result<List<Product>>

    suspend fun getProduct(productUid: String): Result<Product>

    suspend fun queryProductsByName(query: String): Result<List<Product>>
}
