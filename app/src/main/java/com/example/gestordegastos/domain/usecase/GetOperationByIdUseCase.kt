package com.example.gestordegastos.domain.usecase

import com.example.gestordegastos.domain.model.Operation
import com.example.gestordegastos.domain.repository.OperationRepository
import javax.inject.Inject

class GetOperationByIdUseCase @Inject constructor(
    private val repository: OperationRepository
) {
    suspend operator fun invoke(id: Int): Operation? {
        return repository.getById(id)
    }
}
