package com.example.gestordegastos.domain.usecase

import com.example.gestordegastos.domain.model.TypeOperation
import com.example.gestordegastos.domain.repository.OperationTypeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllOperationTypesUseCase @Inject constructor(private val repository: OperationTypeRepository) {
    suspend operator fun invoke(): Flow<List<TypeOperation>> {
        return repository.getAllOperationTypes()
    }
}