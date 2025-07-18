package com.example.gestordegastos.data.repository

import com.example.gestordegastos.data.local.dao.OperationDao
import com.example.gestordegastos.data.mappers.toOperation
import com.example.gestordegastos.data.mappers.toOperationEntity
import com.example.gestordegastos.domain.model.Operation
import com.example.gestordegastos.domain.repository.OperationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OperationRepositoryImpl @Inject constructor(
    private val operationDao: OperationDao
) : OperationRepository {

    override fun getAll(): Flow<List<Operation>> {
        return operationDao.getAll().map { entities ->
            entities.map { it.toOperation() }
        }
    }

    override suspend fun delete(id: Int) {
        val entity = operationDao.getAll().first().find { it.id == id }
        entity?.let {
            operationDao.delete(it)
        }
    }

    override suspend fun insertOperation(operation: Operation): Long {
        return operationDao.insert(operation.toOperationEntity())
    }
}