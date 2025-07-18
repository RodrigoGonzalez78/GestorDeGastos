package com.example.gestordegastos.domain.repository

import com.example.gestordegastos.domain.model.TypeOperation
import kotlinx.coroutines.flow.Flow

interface OperationTypeRepository {
    suspend fun getAllOperationTypes(): Flow<List<TypeOperation>>

}
