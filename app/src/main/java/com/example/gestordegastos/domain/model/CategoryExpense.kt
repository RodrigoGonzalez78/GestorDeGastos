package com.example.gestordegastos.domain.model

data class CategoryExpense(
    val categoryId: Int,
    val categoryName: String,
    val totalAmount: Double,
    val percentage: Float,
    val operationCount: Int
)