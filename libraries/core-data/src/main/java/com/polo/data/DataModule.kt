package com.polo.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.polo.data.datasource.FirestoreDataSource
import com.polo.data.datasource.IFireStoreDataSource
import com.polo.data.datasource.IPhoneVerificationDataSource
import com.polo.data.datasource.PhoneVerificationDataSource
import com.polo.data.repository.PalletRepositoryImpl
import com.polo.data.repository.ProductRepositoryImpl
import com.polo.data.repository.WarehouseRepositoryImpl
import com.polo.domain.repository.PalletRepository
import com.polo.domain.repository.ProductRepository
import com.polo.domain.repository.WarehouseRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    companion object {

        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth {
            return Firebase.auth
        }

        @Provides
        @Singleton
        fun provideFirebaseFirestore(): FirebaseFirestore {
            return Firebase.firestore
        }
    }

    @Binds
    abstract fun bindPhoneVerificationDataSource(dataSource: PhoneVerificationDataSource): IPhoneVerificationDataSource

    @Binds
    abstract fun bindFireStoreDataSource(dataSource: FirestoreDataSource): IFireStoreDataSource

    @Binds
    abstract fun bindPalletRepository(repository: PalletRepositoryImpl): PalletRepository

    @Binds
    abstract fun bindProductRepository(repository: ProductRepositoryImpl): ProductRepository

    @Binds
    abstract fun bindWarehouseRepository(repository: WarehouseRepositoryImpl): WarehouseRepository
}
