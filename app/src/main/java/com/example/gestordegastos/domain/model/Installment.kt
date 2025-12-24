package com.example.gestordegastos.domain.model

data class Installment(
    val id: Int = 0,
    val operationId: Int,
    val amount: Double,
    val dueDate: String,
    val isPaid: Boolean = false,
    val installmentNumber: Int
)
