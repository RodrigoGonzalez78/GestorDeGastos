package com.example.gestordegastos.domain.usecase

import com.example.gestordegastos.domain.repository.OperationRepository
import javax.inject.Inject

class DeleteOperationUseCase @Inject constructor(private val repo: OperationRepository) {
    suspend operator fun invoke(id: Int) = repo.delete(id)
}
