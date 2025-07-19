package com.example.gestordegastos.domain.model

data class MonthlyTotal(
    val month: Int,
    val year: Int,
    val totalIncome: Double,
    val totalExpenses: Double,
    val balance: Double
)
