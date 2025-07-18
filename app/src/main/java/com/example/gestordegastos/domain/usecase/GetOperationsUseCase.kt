package com.example.gestordegastos.domain.usecase

import com.example.gestordegastos.domain.repository.OperationRepository
import javax.inject.Inject

class GetOperationsUseCase @Inject constructor(private val repo: OperationRepository) {
    operator fun invoke() = repo.getAll()
}