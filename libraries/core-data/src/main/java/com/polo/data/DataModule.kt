package com.polo.data

import com.google.firebase.auth.FirebaseAuth
import com.polo.data.datasource.AuthenticationDataSource
import com.polo.data.datasource.IAuthenticationDataSource
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
            return FirebaseAuth.getInstance().apply {
                this.setLanguageCode("rs")
            }
        }
    }

    @Binds
    abstract fun bindAuthenticationDataSource(dataSource: AuthenticationDataSource): IAuthenticationDataSource

    @Binds
    abstract fun bindPhoneVerificationDataSource(dataSource: PhoneVerificationDataSource): IPhoneVerificationDataSource
}