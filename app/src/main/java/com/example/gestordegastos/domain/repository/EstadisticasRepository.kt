package com.example.gestordegastos.domain.repository

import com.example.gestordegastos.domain.model.Operation
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
    suspend fun getMonthlyExpensesByCategory(year: Int, month: Int): Flow<List<CategoryExpense>>
    suspend fun getMonthlyIncomesByCategory(year: Int, month: Int): Flow<List<CategoryIncome>>
    suspend fun getMonthlyTotals(year: Int): Flow<List<MonthlyTotal>>
    suspend fun getYearlyComparison(): Flow<List<YearlyComparison>>
    suspend fun getExpensesByDateRange(startDate: String, endDate: String): Flow<List<Operation>>
    suspend fun getIncomesByDateRange(startDate: String, endDate: String): Flow<List<Operation>>
    suspend fun getCategoryTrends(categoryId: Int, months: Int): Flow<List<CategoryTrend>>
}

data class CategoryExpense(
    val categoryId: Int,
    val categoryName: String,
    val totalAmount: Double,
    val percentage: Float,
    val operationCount: Int
)

data class CategoryIncome(
    val categoryId: Int,
    val categoryName: String,
    val totalAmount: Double,
    val percentage: Float,
    val operationCount: Int
)

data class MonthlyTotal(
    val month: Int,
    val year: Int,
    val totalIncome: Double,
    val totalExpenses: Double,
    val balance: Double
)

data class YearlyComparison(
    val year: Int,
    val totalIncome: Double,
    val totalExpenses: Double,
    val balance: Double
)

data class CategoryTrend(
    val month: String,
    val amount: Double
)
