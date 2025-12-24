package com.example.gestordegastos.domain.usecase

import com.example.gestordegastos.domain.model.Installment
import com.example.gestordegastos.domain.repository.InstallmentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInstallmentsByOperationIdUseCase @Inject constructor(
    private val installmentRepository: InstallmentRepository
) {
    operator fun invoke(operationId: Int): Flow<List<Installment>> {
        return installmentRepository.getByOperationId(operationId)
    }
}
