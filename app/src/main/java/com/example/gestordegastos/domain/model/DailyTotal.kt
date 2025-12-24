package com.example.gestordegastos.domain.model

data class DailyTotal(
    val date: String,
    val dayLabel: String,
    val totalIncome: Double,
    val totalExpenses: Double,
    val balance: Double
)
