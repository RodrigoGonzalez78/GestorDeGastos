package com.example.gestordegastos.domain.repository

import com.example.gestordegastos.domain.model.CategoryExpense
import com.example.gestordegastos.domain.model.CategoryIncome
import com.example.gestordegastos.domain.model.CategoryTrend
import com.example.gestordegastos.domain.model.DailyTotal
import com.example.gestordegastos.domain.model.MonthlyTotal
import com.example.gestordegastos.domain.model.Operation
import com.example.gestordegastos.domain.model.YearlyComparison
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
    suspend fun getMonthlyExpensesByCategory(year: Int, month: Int): Flow<List<CategoryExpense>>
    suspend fun getMonthlyIncomesByCategory(year: Int, month: Int): Flow<List<CategoryIncome>>
    suspend fun getMonthlyTotals(year: Int): Flow<List<MonthlyTotal>>
    suspend fun getYearlyComparison(): Flow<List<YearlyComparison>>
    suspend fun getExpensesByDateRange(startDate: String, endDate: String): Flow<List<Operation>>
    suspend fun getIncomesByDateRange(startDate: String, endDate: String): Flow<List<Operation>>
    suspend fun getCategoryTrends(categoryId: Int, months: Int): Flow<List<CategoryTrend>>
    
    // New date range methods
    suspend fun getExpensesByCategoryInRange(startDate: String, endDate: String): Flow<List<CategoryExpense>>
    suspend fun getIncomesByCategoryInRange(startDate: String, endDate: String): Flow<List<CategoryIncome>>
    suspend fun getDailyTotalsInRange(startDate: String, endDate: String): Flow<List<DailyTotal>>
    suspend fun getTotalsInRange(startDate: String, endDate: String): Flow<Pair<Double, Double>>
}
