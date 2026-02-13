package com.polo.data

import com.polo.data.repository.PalletRepositoryImpl
import com.polo.data.repository.ProductRepositoryImpl
import com.polo.data.repository.WarehouseRepositoryImpl
import com.polo.domain.repository.PalletRepository
import com.polo.domain.repository.ProductRepository
import com.polo.domain.repository.WarehouseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindPalletRepository(repository: PalletRepositoryImpl): PalletRepository

    @Binds
    abstract fun bindProductRepository(repository: ProductRepositoryImpl): ProductRepository

    @Binds
    abstract fun bindWarehouseRepository(repository: WarehouseRepositoryImpl): WarehouseRepository
}
