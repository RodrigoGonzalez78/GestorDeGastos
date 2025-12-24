package com.example.gestordegastos.domain.usecase

import com.example.gestordegastos.domain.model.Installment
import com.example.gestordegastos.domain.model.Operation
import com.example.gestordegastos.domain.model.OperationDisplayItem
import com.example.gestordegastos.domain.repository.InstallmentRepository
import com.example.gestordegastos.domain.repository.OperationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case that returns a combined list of operations and installments for display.
 * - Regular operations (non-installment) are shown as-is
 * - Installment operations show their individual installments based on due date
 */
class GetDisplayOperationsUseCase @Inject constructor(
    private val operationRepository: OperationRepository,
    private val installmentRepository: InstallmentRepository
) {
    operator fun invoke(
        typeOperationId: Int,
        startDate: String,
        endDate: String
    ): Flow<List<OperationDisplayItem>> {
        // Get all operations to use as a lookup for installment parent operations
        val allOperationsFlow: Flow<List<Operation>> = operationRepository.getAll()

        // Get regular operations (non-installment) within date range
        val regularOperationsFlow: Flow<List<OperationDisplayItem>> = allOperationsFlow.map { operations: List<Operation> ->
            operations
                .filter { op: Operation -> 
                    op.typeOperationId == typeOperationId && 
                    !op.isInstallment && 
                    op.date >= startDate && 
                    op.date <= endDate 
                }
                .map { op: Operation ->
                    OperationDisplayItem(
                        id = op.id,
                        amount = op.amount,
                        date = op.date,
                        categoryId = op.categoryId,
                        typeOperationId = op.typeOperationId,
                        isInstallment = false
                    )
                }
        }

        // Get installments within date range and resolve categoryId from parent operation
        val installmentsFlow: Flow<List<OperationDisplayItem>> = combine(
            installmentRepository.getByTypeAndDateRange(typeOperationId, startDate, endDate),
            allOperationsFlow
        ) { installments: List<Installment>, operations: List<Operation> ->
            // Create a lookup map for parent operations
            val operationsMap: Map<Int, Operation> = operations.associateBy { op: Operation -> op.id }
            
            installments.map { inst: Installment ->
                val parentOperation: Operation? = operationsMap[inst.operationId]
                OperationDisplayItem(
                    id = inst.operationId,
                    amount = inst.amount,
                    date = inst.dueDate,
                    categoryId = parentOperation?.categoryId ?: 0,
                    typeOperationId = parentOperation?.typeOperationId ?: typeOperationId,
                    isInstallment = true,
                    installmentId = inst.id,
                    installmentNumber = inst.installmentNumber,
                    totalInstallments = null,
                    isPaid = inst.isPaid,
                    parentOperationId = inst.operationId
                )
            }
        }

        // Combine both flows and sort by date
        return combine(regularOperationsFlow, installmentsFlow) { regularOps: List<OperationDisplayItem>, installmentOps: List<OperationDisplayItem> ->
            (regularOps + installmentOps).sortedByDescending { item: OperationDisplayItem -> item.date }
        }
    }
}
