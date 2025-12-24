package com.example.gestordegastos.domain.model

data class Operation(
    val id: Int = 0,
    val amount: Double,
    val date: String,
    val categoryId: Int,
    val typeOperationId: Int,
    val isInstallment: Boolean = false
)