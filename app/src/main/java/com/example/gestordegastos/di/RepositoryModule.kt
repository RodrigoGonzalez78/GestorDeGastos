package com.example.gestordegastos.di

import com.example.gestordegastos.data.repository.CategoryRepositoryImpl
import com.example.gestordegastos.data.repository.OperationRepositoryImpl
import com.example.gestordegastos.data.repository.OperationTypeRepositoryImpl
import com.example.gestordegastos.data.repository.StatisticsRepositoryImpl
import com.example.gestordegastos.domain.repository.CategoryRepository
import com.example.gestordegastos.domain.repository.OperationRepository
import com.example.gestordegastos.domain.repository.OperationTypeRepository
import com.example.gestordegastos.domain.repository.StatisticsRepository
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
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindOperationRepository(
        operationRepositoryImpl: OperationRepositoryImpl
    ): OperationRepository

    @Binds
    @Singleton
    abstract fun bindOperationTypeRepository(
        operationTypeRepositoryImpl: OperationTypeRepositoryImpl
    ): OperationTypeRepository


    @Binds
    @Singleton
    abstract fun bindStatisticsRepository(
        statisticsRepositoryImpl: StatisticsRepositoryImpl
    ): StatisticsRepository
}