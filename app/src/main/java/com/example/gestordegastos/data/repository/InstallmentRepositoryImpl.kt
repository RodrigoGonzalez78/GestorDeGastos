package com.example.gestordegastos.data.repository

import com.example.gestordegastos.data.local.dao.InstallmentDao
import com.example.gestordegastos.data.mappers.toInstallment
import com.example.gestordegastos.data.mappers.toInstallmentEntity
import com.example.gestordegastos.domain.model.Installment
import com.example.gestordegastos.domain.repository.InstallmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InstallmentRepositoryImpl @Inject constructor(
    private val installmentDao: InstallmentDao
) : InstallmentRepository {

    override fun getByOperationId(operationId: Int): Flow<List<Installment>> {
        return installmentDao.getByOperationId(operationId).map { entities ->
            entities.map { it.toInstallment() }
        }
    }

    override suspend fun getByOperationIdOneShot(operationId: Int): List<Installment> {
        return installmentDao.getByOperationIdOneShot(operationId).map { it.toInstallment() }
    }

    override fun getByTypeAndDateRange(
        typeOperationId: Int,
        startDate: String,
        endDate: String
    ): Flow<List<Installment>> {
        return installmentDao.getByTypeAndDateRange(typeOperationId, startDate, endDate).map { entities ->
            entities.map { it.toInstallment() }
        }
    }

    override suspend fun countByOperationId(operationId: Int): Int {
        return installmentDao.countByOperationId(operationId)
    }

    override suspend fun insertAll(installments: List<Installment>) {
        installmentDao.insertAll(installments.map { it.toInstallmentEntity() })
    }

    override suspend fun update(installment: Installment) {
        installmentDao.update(installment.toInstallmentEntity())
    }

    override suspend fun deleteByOperationId(operationId: Int) {
        installmentDao.deleteByOperationId(operationId)
    }
}
