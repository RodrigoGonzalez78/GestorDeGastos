package com.example.gestordegastos.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gestordegastos.data.local.entity.OperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OperationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(operation: OperationEntity): Long

    @Query("SELECT * FROM Operation")
    fun getAll(): Flow<List<OperationEntity>>

    @Query("SELECT * FROM Operation WHERE type_operation = :typeId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC, id DESC")
    fun getOperationsByTypeAndDate(typeId: Int, startDate: String, endDate: String): PagingSource<Int, OperationEntity>

    @Query("SELECT * FROM operation WHERE date BETWEEN :startDate AND :endDate AND type_operation = :typeId")
    suspend fun getOperationsByDateRangeAndType(startDate: String, endDate: String, typeId: Int): List<OperationEntity>

    @Update
    suspend fun update(operation: OperationEntity): Int

    @Delete
    suspend fun delete(operation: OperationEntity): Int

    @Query("DELETE FROM Operation WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM operation WHERE date BETWEEN :startDate AND :endDate AND category = :categoryId")
    suspend fun getOperationsByDateRangeAndCategory(startDate: String, endDate: String, categoryId: Int): List<OperationEntity>

    @Query("SELECT * FROM Operation WHERE id = :id")
    suspend fun getById(id: Int): OperationEntity?

}
