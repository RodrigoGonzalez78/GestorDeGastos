package com.example.gestordegastos.domain.model

data class CategoryIncome(
    val categoryId: Int,
    val categoryName: String,
    val totalAmount: Double,
    val percentage: Float,
    val operationCount: Int
)