package com.polo.firebase

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.polo.data.datasource.FirestoreDataSource
import com.polo.domain.repository.AuthenticationRepository
import com.polo.firebase.datasource.AuthenticationRepositoryImpl
import com.polo.firebase.datasource.FirestoreDataSourceImpl
import com.polo.firebase.datasource.PhoneVerificationServiceImpl
import com.polo.verification.PhoneVerificationService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseModule {

    @Binds
    abstract fun bindAuthenticationRepository(repository: AuthenticationRepositoryImpl): AuthenticationRepository

    @Binds
    abstract fun bindPhoneVerificationService(service: PhoneVerificationServiceImpl): PhoneVerificationService

    @Binds
    abstract fun bindFirestoreDataSource(firestoreDataSource: FirestoreDataSourceImpl): FirestoreDataSource

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
}
