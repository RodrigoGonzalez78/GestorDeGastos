package com.example.gestordegastos.domain.repository

import com.example.gestordegastos.domain.model.Operation
import kotlinx.coroutines.flow.Flow

interface OperationRepository {
    fun getAll(): Flow<List<Operation>>
    suspend fun delete(id: Int)
    suspend fun insertOperation(operation: Operation): Long
}