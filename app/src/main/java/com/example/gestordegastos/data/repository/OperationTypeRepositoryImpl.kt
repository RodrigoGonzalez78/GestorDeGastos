package com.example.gestordegastos.data.repository

import com.example.gestordegastos.data.local.dao.TypeOperationDao
import com.example.gestordegastos.data.mappers.toTypeOperation
import com.example.gestordegastos.domain.model.TypeOperation
import com.example.gestordegastos.domain.repository.OperationTypeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OperationTypeRepositoryImpl @Inject constructor(
    private val operationTypeDao: TypeOperationDao
) : OperationTypeRepository {

    override suspend fun getAllOperationTypes(): Flow<List<TypeOperation>> {
        return operationTypeDao.getAll().map {entities -> entities.map { it.toTypeOperation() } }
    }
}