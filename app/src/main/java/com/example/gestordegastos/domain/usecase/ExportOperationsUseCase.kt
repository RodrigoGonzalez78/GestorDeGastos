package com.example.gestordegastos.domain.usecase

import com.example.gestordegastos.domain.model.OperationDisplayItem
import com.example.gestordegastos.domain.repository.InstallmentRepository
import com.example.gestordegastos.domain.repository.OperationRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case to export operations and installments to a list for CSV generation
 */
class ExportOperationsUseCase @Inject constructor(
    private val operationRepository: OperationRepository,
    private val installmentRepository: InstallmentRepository
) {
    suspend operator fun invoke(startDate: String, endDate: String): List<ExportItem> {
        val allOperations = operationRepository.getAll().first()
        
        val items = mutableListOf<ExportItem>()
        
        // Add regular operations (non-installment) within date range
        allOperations
            .filter { !it.isInstallment && it.date >= startDate && it.date <= endDate }
            .forEach { op ->
                items.add(
                    ExportItem(
                        date = op.date,
                        type = if (op.typeOperationId == 1) "Gasto" else "Ingreso",
                        amount = op.amount,
                        categoryId = op.categoryId,
                        isInstallment = false,
                        installmentNumber = null,
                        totalInstallments = null
                    )
                )
            }
        
        // Add installments within date range
        // Get all installments for installment operations
        val installmentOperations = allOperations.filter { it.isInstallment }
        
        for (op in installmentOperations) {
            val installments = installmentRepository.getByOperationIdOneShot(op.id)
            val totalInstallments = installments.size
            
            installments
                .filter { it.dueDate >= startDate && it.dueDate <= endDate }
                .forEach { inst ->
                    items.add(
                        ExportItem(
                            date = inst.dueDate,
                            type = if (op.typeOperationId == 1) "Gasto" else "Ingreso",
                            amount = inst.amount,
                            categoryId = op.categoryId,
                            isInstallment = true,
                            installmentNumber = inst.installmentNumber,
                            totalInstallments = totalInstallments
                        )
                    )
                }
        }
        
        return items.sortedBy { it.date }
    }
}

data class ExportItem(
    val date: String,
    val type: String,
    val amount: Double,
    val categoryId: Int,
    val isInstallment: Boolean,
    val installmentNumber: Int?,
    val totalInstallments: Int?
)
