package com.example.gestordegastos.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gestordegastos.data.local.AppDatabase
import com.example.gestordegastos.data.local.dao.CategoryDao
import com.example.gestordegastos.data.local.dao.InstallmentDao
import com.example.gestordegastos.data.local.dao.OperationDao
import com.example.gestordegastos.data.local.dao.TypeOperationDao
import com.example.gestordegastos.data.local.entity.CategoryEntity
import com.example.gestordegastos.data.local.entity.TypeOperationEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

val categories = listOf(
    CategoryEntity(description = "Salud", icon = "Health"),
    CategoryEntity(description = "Transporte", icon = "Transport"),
    CategoryEntity(description = "Hogar", icon = "Home"),
    CategoryEntity(description = "Entretenimiento", icon = "Entertainment"),
    CategoryEntity(description = "Educación", icon = "Education"),
    CategoryEntity(description = "Salario", icon = "Savings"),
    CategoryEntity(description = "Inversión", icon = "Investments"),
    CategoryEntity(description = "Compras", icon = "Shopping"),
    CategoryEntity(description = "Servicios", icon = "Bills"),
    CategoryEntity(description = "Ropa", icon = "Clothing"),
    CategoryEntity(description = "Tecnología", icon = "Technology")
)

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope =
        CoroutineScope(Dispatchers.IO)

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        scope: CoroutineScope
    ): AppDatabase {

        var dbInstance: AppDatabase? = null

        val callback = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)

                scope.launch {
                    val database = dbInstance
                        ?: throw IllegalStateException("DB no inicializada en callback")
                    val typeOperationDao = database.typeOperationDao()
                    val categoryDao = database.categoryDao()

                    // Insert TypeOperations if they don't exist
                    val existingTypes = typeOperationDao.getAllOneShot()
                    if (existingTypes.isEmpty()) {
                        typeOperationDao.insert(TypeOperationEntity(description = "Gasto"))
                        typeOperationDao.insert(TypeOperationEntity(description = "Ingreso"))
                    }

                    // Insert categories if they don't exist
                    val existingCategories = categoryDao.getAllOneShot()
                    if (existingCategories.isEmpty()) {
                        categories.forEach {
                            categoryDao.insert(it)
                        }
                    }
                }
            }
        }

        val instance = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
            .addCallback(callback)
            .fallbackToDestructiveMigration(true)
            .build()

        dbInstance = instance
        return instance
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

    @Provides
    @Singleton
    fun provideInstallmentDao(appDatabase: AppDatabase): InstallmentDao {
        return appDatabase.installmentDao()
    }

}