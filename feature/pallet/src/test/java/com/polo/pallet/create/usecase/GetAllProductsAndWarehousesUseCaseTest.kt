package com.polo.pallet.create.usecase

import com.polo.domain.functional.Either
import com.polo.domain.model.Product
import com.polo.domain.model.Warehouse
import com.polo.domain.repository.ProductRepository
import com.polo.domain.repository.WarehouseRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetAllProductsAndWarehousesUseCaseTest {

    @Test
    fun `maps domain products and warehouses to ui models`() = runTest {
        val products = listOf(
            Product(
                uid = "p1",
                idNumber = 10,
                name = "Widget",
                barCode = 123L,
                price = 9.99f,
                transportPackage = "Box"
            )
        )
        val warehouses = listOf(
            Warehouse(
                uid = "w1",
                name = "Main"
            )
        )

        val useCase = GetAllProductsAndWarehousesUseCase(
            productRepository = FakeProductRepository(products),
            warehouseRepository = FakeWarehouseRepository(warehouses)
        )

        val result = useCase()

        assertTrue(result is Either.Result)
        val (uiProducts, uiWarehouses) = (result as Either.Result).data
        assertEquals(1, uiProducts.size)
        assertEquals("Widget", uiProducts.first().name)
        assertEquals(1, uiWarehouses.size)
        assertEquals("Main", uiWarehouses.first().name)
    }

    private class FakeProductRepository(
        private val products: List<Product>
    ) : ProductRepository {
        override suspend fun getAllProducts(): Either<Exception, List<Product>> = Either.Result(products)
        override suspend fun getProduct(productUid: String): Either<Exception, Product> = throw UnsupportedOperationException()
        override suspend fun queryProductsByName(query: String): Either<Exception, List<Product>> = throw UnsupportedOperationException()
    }

    private class FakeWarehouseRepository(
        private val warehouses: List<Warehouse>
    ) : WarehouseRepository {
        override suspend fun getAllWarehouses(): Either<Exception, List<Warehouse>> = Either.Result(warehouses)
        override suspend fun getWarehouse(warehouseUid: String): Either<Exception, Warehouse> = throw UnsupportedOperationException()
    }
}
