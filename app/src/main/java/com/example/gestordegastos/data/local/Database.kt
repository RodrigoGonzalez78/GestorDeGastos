package com.example.gestordegastos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gestordegastos.data.local.dao.CategoryDao
import com.example.gestordegastos.data.local.dao.OperationDao
import com.example.gestordegastos.data.local.dao.TypeOperationDao
import com.example.gestordegastos.data.local.entity.CategoryEntity
import com.example.gestordegastos.data.local.entity.OperationEntity
import com.example.gestordegastos.data.local.entity.TypeOperationEntity


@Database(
    entities = [CategoryEntity::class, TypeOperationEntity::class, OperationEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun typeOperationDao(): TypeOperationDao
    abstract fun operationDao(): OperationDao
}