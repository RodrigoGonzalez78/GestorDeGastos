package com.example.gestordegastos.domain.model

data class YearlyComparison(
    val year: Int,
    val totalIncome: Double,
    val totalExpenses: Double,
    val balance: Double
)