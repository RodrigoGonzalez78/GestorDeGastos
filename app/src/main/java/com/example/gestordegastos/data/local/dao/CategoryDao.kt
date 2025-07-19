package com.example.gestordegastos.data.local.dao

import androidx.room.*
import com.example.gestordegastos.data.local.entity.CategoryEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Query("SELECT * FROM Category")
    fun getAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM Category WHERE id = :id")
    suspend fun getById(id: Int): CategoryEntity?

    @Update
    suspend fun update(category: CategoryEntity): Int

    @Delete
    suspend fun delete(category: CategoryEntity): Int


}



