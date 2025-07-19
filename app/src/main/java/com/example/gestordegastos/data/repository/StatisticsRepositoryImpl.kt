package com.example.gestordegastos.data.repository

import android.annotation.SuppressLint
import com.example.gestordegastos.data.local.dao.CategoryDao
import com.example.gestordegastos.data.local.dao.OperationDao
import com.example.gestordegastos.data.mappers.toOperation
import com.example.gestordegastos.domain.model.Operation
import com.example.gestordegastos.domain.repository.CategoryExpense
import com.example.gestordegastos.domain.repository.CategoryIncome
import com.example.gestordegastos.domain.repository.CategoryTrend
import com.example.gestordegastos.domain.repository.MonthlyTotal
import com.example.gestordegastos.domain.repository.StatisticsRepository
import com.example.gestordegastos.domain.repository.YearlyComparison
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.Calendar
import java.util.Locale


class StatisticsRepositoryImpl @Inject constructor(
    private val operationDao: OperationDao,
    private val categoryDao: CategoryDao
) : StatisticsRepository {

    @SuppressLint("DefaultLocale")
    override suspend fun getMonthlyExpensesByCategory(year: Int, month: Int): Flow<List<CategoryExpense>> {
        return flow {
            val startDate = String.format("%04d-%02d-01", year, month)
            val endDate = String.format("%04d-%02d-31", year, month)

            val operations = operationDao.getOperationsByDateRangeAndType(startDate, endDate, 1)
            val categories = categoryDao.getAll().first()

            val expensesByCategory = operations.groupBy { it.category }
                .map { (categoryId, ops) ->
                    val category = categories.find { it.id == categoryId }
                    val totalAmount = ops.sumOf { it.amount }
                    CategoryExpense(
                        categoryId = categoryId,
                        categoryName = category?.description ?: "Sin categoría",
                        totalAmount = totalAmount,
                        percentage = 0f,
                        operationCount = ops.size
                    )
                }

            val totalExpenses = expensesByCategory.sumOf { it.totalAmount }
            val expensesWithPercentage = expensesByCategory.map { expense ->
                expense.copy(
                    percentage = if (totalExpenses > 0)
                        (expense.totalAmount / totalExpenses * 100).toFloat()
                    else 0f
                )
            }.sortedByDescending { it.totalAmount }

            emit(expensesWithPercentage)
        }
    }

    @SuppressLint("DefaultLocale")
    override suspend fun getMonthlyIncomesByCategory(year: Int, month: Int): Flow<List<CategoryIncome>> {
        return flow {
            val startDate = String.format("%04d-%02d-01", year, month)
            val endDate = String.format("%04d-%02d-31", year, month)

            val operations = operationDao.getOperationsByDateRangeAndType(startDate, endDate, 2)
            val categories = categoryDao.getAll().first()

            val incomesByCategory = operations.groupBy { it.category }
                .map { (categoryId, ops) ->
                    val category = categories.find { it.id == categoryId }
                    val totalAmount = ops.sumOf { it.amount }
                    CategoryIncome(
                        categoryId = categoryId,
                        categoryName = category?.description ?: "Sin categoría",
                        totalAmount = totalAmount,
                        percentage = 0f,
                        operationCount = ops.size
                    )
                }

            val totalIncome = incomesByCategory.sumOf { it.totalAmount }
            val incomesWithPercentage = incomesByCategory.map { income ->
                income.copy(
                    percentage = if (totalIncome > 0)
                        (income.totalAmount / totalIncome * 100).toFloat()
                    else 0f
                )
            }.sortedByDescending { it.totalAmount }

            emit(incomesWithPercentage)
        }
    }

    @SuppressLint("DefaultLocale")
    override suspend fun getMonthlyTotals(year: Int): Flow<List<MonthlyTotal>> {
        return flow {
            val monthlyTotals = mutableListOf<MonthlyTotal>()

            for (month in 1..12) {
                val startDate = String.format("%04d-%02d-01", year, month)
                val endDate = String.format("%04d-%02d-31", year, month)

                val expenses = operationDao.getOperationsByDateRangeAndType(startDate, endDate, 1)
                val incomes = operationDao.getOperationsByDateRangeAndType(startDate, endDate, 2)

                val totalExpenses = expenses.sumOf { it.amount }
                val totalIncome = incomes.sumOf { it.amount }
                val balance = totalIncome - totalExpenses

                monthlyTotals.add(
                    MonthlyTotal(
                        month = month,
                        year = year,
                        totalIncome = totalIncome,
                        totalExpenses = totalExpenses,
                        balance = balance
                    )
                )
            }

            emit(monthlyTotals)
        }
    }

    @SuppressLint("DefaultLocale")
    override suspend fun getYearlyComparison(): Flow<List<YearlyComparison>> {
        return flow {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val years = listOf(currentYear - 2, currentYear - 1, currentYear)

            val yearlyComparisons = years.map { year ->
                val startDate = String.format("%04d-01-01", year)
                val endDate = String.format("%04d-12-31", year)

                val expenses = operationDao.getOperationsByDateRangeAndType(startDate, endDate, 1)
                val incomes = operationDao.getOperationsByDateRangeAndType(startDate, endDate, 2)

                val totalExpenses = expenses.sumOf { it.amount }
                val totalIncome = incomes.sumOf { it.amount }
                val balance = totalIncome - totalExpenses

                YearlyComparison(
                    year = year,
                    totalIncome = totalIncome,
                    totalExpenses = totalExpenses,
                    balance = balance
                )
            }

            emit(yearlyComparisons)
        }
    }

    override suspend fun getExpensesByDateRange(startDate: String, endDate: String): Flow<List<Operation>> {
        return flow {
            val operations = operationDao.getOperationsByDateRangeAndType(startDate, endDate, 1)
            emit(operations.map { it.toOperation() })
        }
    }

    override suspend fun getIncomesByDateRange(startDate: String, endDate: String): Flow<List<Operation>> {
        return flow {
            val operations = operationDao.getOperationsByDateRangeAndType(startDate, endDate, 2)
            emit(operations.map { it.toOperation() })
        }
    }

    @SuppressLint("DefaultLocale")
    override suspend fun getCategoryTrends(categoryId: Int, months: Int): Flow<List<CategoryTrend>> {
        return flow {
            val trends = mutableListOf<CategoryTrend>()
            val calendar = Calendar.getInstance()

            for (i in months - 1 downTo 0) {
                calendar.add(Calendar.MONTH, -i)
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1

                val startDate = String.format("%04d-%02d-01", year, month)
                val endDate = String.format("%04d-%02d-31", year, month)

                val operations = operationDao.getOperationsByDateRangeAndCategory(startDate, endDate, categoryId)
                val totalAmount = operations.sumOf { it.amount }

                val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) ?: ""

                trends.add(
                    CategoryTrend(
                        month = monthName,
                        amount = totalAmount
                    )
                )

                calendar.add(Calendar.MONTH, i)
            }

            emit(trends)
        }
    }
}
