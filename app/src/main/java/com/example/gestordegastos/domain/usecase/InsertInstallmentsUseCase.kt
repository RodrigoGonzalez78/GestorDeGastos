package com.example.gestordegastos.domain.usecase

import com.example.gestordegastos.domain.model.Installment
import com.example.gestordegastos.domain.repository.InstallmentRepository
import javax.inject.Inject

class InsertInstallmentsUseCase @Inject constructor(
    private val installmentRepository: InstallmentRepository
) {
    suspend operator fun invoke(installments: List<Installment>) {
        installmentRepository.insertAll(installments)
    }
}
