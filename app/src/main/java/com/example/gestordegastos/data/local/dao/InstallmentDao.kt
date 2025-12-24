package com.example.gestordegastos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gestordegastos.data.local.entity.InstallmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InstallmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(installment: InstallmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(installments: List<InstallmentEntity>)

    @Query("SELECT * FROM Installment WHERE operation_id = :operationId ORDER BY installment_number")
    fun getByOperationId(operationId: Int): Flow<List<InstallmentEntity>>

    @Query("SELECT * FROM Installment WHERE operation_id = :operationId ORDER BY installment_number")
    suspend fun getByOperationIdOneShot(operationId: Int): List<InstallmentEntity>

    @Query("""
        SELECT i.* FROM Installment i 
        INNER JOIN Operation o ON i.operation_id = o.id 
        WHERE o.type_operation = :typeOperationId 
        AND i.due_date >= :startDate 
        AND i.due_date <= :endDate 
        ORDER BY i.due_date ASC
    """)
    fun getByTypeAndDateRange(typeOperationId: Int, startDate: String, endDate: String): Flow<List<InstallmentEntity>>

    @Query("SELECT COUNT(*) FROM Installment WHERE operation_id = :operationId")
    suspend fun countByOperationId(operationId: Int): Int

    @Update
    suspend fun update(installment: InstallmentEntity): Int

    @Delete
    suspend fun delete(installment: InstallmentEntity): Int

    @Query("DELETE FROM Installment WHERE operation_id = :operationId")
    suspend fun deleteByOperationId(operationId: Int)
}
