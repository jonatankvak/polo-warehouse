package com.polo.data

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.polo.data.datasource.AuthenticationDataSource
import com.polo.data.datasource.FirestoreDataSource
import com.polo.data.datasource.IAuthenticationDataSource
import com.polo.data.datasource.IFireStoreDataSource
import com.polo.data.datasource.IPhoneVerificationDataSource
import com.polo.data.datasource.PhoneVerificationDataSource
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
    abstract fun bindAuthenticationDataSource(dataSource: AuthenticationDataSource): IAuthenticationDataSource

    @Binds
    abstract fun bindPhoneVerificationDataSource(dataSource: PhoneVerificationDataSource): IPhoneVerificationDataSource

    @Binds
    abstract fun bindFireStoreDataSource(dataSource: FirestoreDataSource): IFireStoreDataSource
}