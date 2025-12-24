package com.example.gestordegastos.domain.model

/**
 * Represents an item to display in the operations list.
 * This can be either a regular operation or an installment from an installment-based operation.
 */
data class OperationDisplayItem(
    val id: Int,
    val amount: Double,
    val date: String,
    val categoryId: Int,
    val typeOperationId: Int,
    val isInstallment: Boolean,
    // Installment-specific fields (null if not an installment)
    val installmentId: Int? = null,
    val installmentNumber: Int? = null,
    val totalInstallments: Int? = null,
    val isPaid: Boolean? = null,
    val parentOperationId: Int? = null
)
