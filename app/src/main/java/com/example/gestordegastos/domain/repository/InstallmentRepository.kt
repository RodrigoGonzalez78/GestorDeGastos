package com.example.gestordegastos.domain.repository

import com.example.gestordegastos.domain.model.Installment
import kotlinx.coroutines.flow.Flow

interface InstallmentRepository {
    fun getByOperationId(operationId: Int): Flow<List<Installment>>
    suspend fun getByOperationIdOneShot(operationId: Int): List<Installment>
    fun getByTypeAndDateRange(typeOperationId: Int, startDate: String, endDate: String): Flow<List<Installment>>
    suspend fun countByOperationId(operationId: Int): Int
    suspend fun insertAll(installments: List<Installment>)
    suspend fun update(installment: Installment)
    suspend fun deleteByOperationId(operationId: Int)
}
