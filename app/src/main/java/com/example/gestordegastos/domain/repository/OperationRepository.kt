package com.example.gestordegastos.domain.repository

import androidx.paging.PagingData
import com.example.gestordegastos.domain.model.Operation
import kotlinx.coroutines.flow.Flow

interface OperationRepository {
    fun getAll(): Flow<List<Operation>>
    fun getOperations(typeId: Int, startDate: String, endDate: String): Flow<PagingData<Operation>>
    suspend fun delete(id: Int)
    suspend fun insertOperation(operation: Operation): Long
}