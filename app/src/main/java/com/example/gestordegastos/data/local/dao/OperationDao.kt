package com.example.gestordegastos.data.local.dao

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

    @Update
    suspend fun update(operation: OperationEntity): Int

    @Delete
    suspend fun delete(operation: OperationEntity): Int
}