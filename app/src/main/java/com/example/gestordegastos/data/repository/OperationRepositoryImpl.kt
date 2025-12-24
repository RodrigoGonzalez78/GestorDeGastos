package com.example.gestordegastos.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.gestordegastos.data.local.dao.OperationDao
import com.example.gestordegastos.data.mappers.toOperation
import com.example.gestordegastos.data.mappers.toOperationEntity
import com.example.gestordegastos.domain.model.Operation
import com.example.gestordegastos.domain.repository.OperationRepository
import kotlinx.coroutines.flow.Flow
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

    override fun getOperations(typeId: Int, startDate: String, endDate: String): Flow<PagingData<Operation>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { operationDao.getOperationsByTypeAndDate(typeId, startDate, endDate) }
        ).flow.map { pagingData ->
            pagingData.map { it.toOperation() }
        }
    }

    override suspend fun delete(id: Int) {
        operationDao.deleteById(id)
    }

    override suspend fun insertOperation(operation: Operation): Long {
        return operationDao.insert(operation.toOperationEntity())
    }

    override suspend fun getById(id: Int): Operation? {
        return operationDao.getById(id)?.toOperation()
    }

    override suspend fun updateOperation(operation: Operation) {
        operationDao.update(operation.toOperationEntity())
    }
}