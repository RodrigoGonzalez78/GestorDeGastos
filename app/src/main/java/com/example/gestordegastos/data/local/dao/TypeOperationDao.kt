package com.example.gestordegastos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gestordegastos.data.local.entity.TypeOperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TypeOperationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(type: TypeOperationEntity): Long

    @Query("SELECT * FROM TypeOperation")
    fun getAll(): Flow<List<TypeOperationEntity>>

    @Update
    suspend fun update(type: TypeOperationEntity): Int

    @Delete
    suspend fun delete(type: TypeOperationEntity): Int
}