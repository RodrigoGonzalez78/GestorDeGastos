package com.example.gestordegastos.data.repository

import android.annotation.SuppressLint
import com.example.gestordegastos.data.local.dao.CategoryDao
import com.example.gestordegastos.data.local.dao.InstallmentDao
import com.example.gestordegastos.data.local.dao.OperationDao
import com.example.gestordegastos.data.mappers.toOperation
import com.example.gestordegastos.domain.model.Operation
import com.example.gestordegastos.domain.model.CategoryExpense
import com.example.gestordegastos.domain.model.CategoryIncome
import com.example.gestordegastos.domain.model.CategoryTrend
import com.example.gestordegastos.domain.model.DailyTotal
import com.example.gestordegastos.domain.model.MonthlyTotal
import com.example.gestordegastos.domain.repository.StatisticsRepository
import com.example.gestordegastos.domain.model.YearlyComparison
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class StatisticsRepositoryImpl @Inject constructor(
    private val operationDao: OperationDao,
    private val categoryDao: CategoryDao,
    private val installmentDao: InstallmentDao
) : StatisticsRepository {

    // Data class to represent a unified item (operation or installment)
    private data class StatItem(
        val categoryId: Int,
        val amount: Double,
        val date: String,
        val typeOperationId: Int
    )

    // Helper function to get combined operations and installments
    private suspend fun getCombinedItems(startDate: String, endDate: String, typeOperationId: Int): List<StatItem> {
        // Get regular operations (non-installment)
        val regularOperations = operationDao.getOperationsByDateRangeAndType(startDate, endDate, typeOperationId)
            .filter { !it.isInstallment }
            .map { StatItem(it.category, it.amount, it.date, typeOperationId) }
        
        // Get installments within date range
        val installments = installmentDao.getByTypeAndDateRange(typeOperationId, startDate, endDate)
            .first()
        
        // Map installments to StatItems, getting categoryId from parent operation
        val installmentItems = installments.mapNotNull { inst ->
            val parentOp = operationDao.getById(inst.operationId)
            if (parentOp != null) {
                StatItem(parentOp.category, inst.amount, inst.dueDate, typeOperationId)
            } else null
        }
        
        return regularOperations + installmentItems
    }

    @SuppressLint("DefaultLocale")
    override suspend fun getMonthlyExpensesByCategory(year: Int, month: Int): Flow<List<CategoryExpense>> {
        return flow {
            val startDate = String.format("%04d-%02d-01", year, month)
            val endDate = String.format("%04d-%02d-31", year, month)

            val items = getCombinedItems(startDate, endDate, 1)
            val categories = categoryDao.getAll().first()

            val expensesByCategory = items.groupBy { it.categoryId }
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

            val items = getCombinedItems(startDate, endDate, 2)
            val categories = categoryDao.getAll().first()

            val incomesByCategory = items.groupBy { it.categoryId }
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

                val expenseItems = getCombinedItems(startDate, endDate, 1)
                val incomeItems = getCombinedItems(startDate, endDate, 2)

                val totalExpenses = expenseItems.sumOf { it.amount }
                val totalIncome = incomeItems.sumOf { it.amount }
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

                val expenseItems = getCombinedItems(startDate, endDate, 1)
                val incomeItems = getCombinedItems(startDate, endDate, 2)

                val totalExpenses = expenseItems.sumOf { it.amount }
                val totalIncome = incomeItems.sumOf { it.amount }
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

    // New date range methods - now including installments
    override suspend fun getExpensesByCategoryInRange(startDate: String, endDate: String): Flow<List<CategoryExpense>> {
        return flow {
            val items = getCombinedItems(startDate, endDate, 1)
            val categories = categoryDao.getAll().first()

            val expensesByCategory = items.groupBy { it.categoryId }
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

    override suspend fun getIncomesByCategoryInRange(startDate: String, endDate: String): Flow<List<CategoryIncome>> {
        return flow {
            val items = getCombinedItems(startDate, endDate, 2)
            val categories = categoryDao.getAll().first()

            val incomesByCategory = items.groupBy { it.categoryId }
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

    override suspend fun getDailyTotalsInRange(startDate: String, endDate: String): Flow<List<DailyTotal>> {
        return flow {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val displayFormat = SimpleDateFormat("dd/MM", Locale.US)
            
            val start = dateFormat.parse(startDate) ?: return@flow
            val end = dateFormat.parse(endDate) ?: return@flow
            
            val calendar = Calendar.getInstance()
            calendar.time = start
            
            val dailyTotals = mutableListOf<DailyTotal>()
            
            while (!calendar.time.after(end)) {
                val currentDate = dateFormat.format(calendar.time)
                val dayLabel = displayFormat.format(calendar.time)
                
                // Get combined items for this specific day
                val expenseItems = getCombinedItems(currentDate, currentDate, 1)
                val incomeItems = getCombinedItems(currentDate, currentDate, 2)
                
                val totalExpenses = expenseItems.sumOf { it.amount }
                val totalIncome = incomeItems.sumOf { it.amount }
                
                dailyTotals.add(
                    DailyTotal(
                        date = currentDate,
                        dayLabel = dayLabel,
                        totalIncome = totalIncome,
                        totalExpenses = totalExpenses,
                        balance = totalIncome - totalExpenses
                    )
                )
                
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            
            emit(dailyTotals)
        }
    }

    override suspend fun getTotalsInRange(startDate: String, endDate: String): Flow<Pair<Double, Double>> {
        return flow {
            val expenseItems = getCombinedItems(startDate, endDate, 1)
            val incomeItems = getCombinedItems(startDate, endDate, 2)
            
            val totalExpenses = expenseItems.sumOf { it.amount }
            val totalIncome = incomeItems.sumOf { it.amount }
            
            emit(Pair(totalIncome, totalExpenses))
        }
    }
}
