package com.example.gestordegastos.di

import com.example.gestordegastos.data.repository.CategoryRepositoryImpl
import com.example.gestordegastos.data.repository.OperationRepositoryImpl
import com.example.gestordegastos.data.repository.OperationTypeRepositoryImpl
import com.example.gestordegastos.domain.repository.CategoryRepository
import com.example.gestordegastos.domain.repository.OperationRepository
import com.example.gestordegastos.domain.repository.OperationTypeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        authRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindOperationRepository(
        authRepositoryImpl: OperationRepositoryImpl
    ): OperationRepository

    @Binds
    @Singleton
    abstract fun bindOperationTypeRepository(
        authRepositoryImpl: OperationTypeRepositoryImpl
    ): OperationTypeRepository
}