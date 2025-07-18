package com.example.gestordegastos.di

import android.content.Context
import androidx.room.Room
import com.example.gestordegastos.data.local.AppDatabase
import com.example.gestordegastos.data.local.dao.CategoryDao
import com.example.gestordegastos.data.local.dao.OperationDao
import com.example.gestordegastos.data.local.dao.TypeOperationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao {
        return appDatabase.categoryDao()
    }

    @Provides
    @Singleton
    fun provideOperationDao(appDatabase: AppDatabase): OperationDao {
        return appDatabase.operationDao()
    }

    @Provides
    @Singleton
    fun provideTypeOperationDao(appDatabase: AppDatabase): TypeOperationDao {
        return appDatabase.typeOperationDao()
    }

}