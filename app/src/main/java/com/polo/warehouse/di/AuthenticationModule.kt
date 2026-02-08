package com.polo.warehouse.di

import com.polo.data.datasource.AuthenticationDataSource
import com.polo.domain.repository.AuthenticationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthenticationModule {

    @Binds
    abstract fun bindAuthenticationRepository(dataSource: AuthenticationDataSource): AuthenticationRepository
}
