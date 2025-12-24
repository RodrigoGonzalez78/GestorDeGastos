package com.example.gestordegastos.presenter.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestordegastos.domain.model.CategoryExpense
import com.example.gestordegastos.domain.model.CategoryIncome
import com.example.gestordegastos.domain.model.DailyTotal
import com.example.gestordegastos.domain.model.YearlyComparison
import com.example.gestordegastos.domain.repository.StatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        StatisticsUiState(
            startDate = getCurrentMonthDateRange().first,
            endDate = getCurrentMonthDateRange().second
        )
    )
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    private fun getCurrentMonthDateRange(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = dateFormat.format(calendar.time)

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endDate = dateFormat.format(calendar.time)

        return Pair(startDate, endDate)
    }

    private fun loadStatistics() {
        val dateRange = getCurrentMonthDateRange()
        loadStatisticsForDateRange(dateRange.first, dateRange.second)
    }

    private fun loadStatisticsForDateRange(startDate: String, endDate: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val expensesDeferred = async {
                    statisticsRepository.getExpensesByCategoryInRange(startDate, endDate).first()
                }
                val incomesDeferred = async {
                    statisticsRepository.getIncomesByCategoryInRange(startDate, endDate).first()
                }
                val dailyTotalsDeferred = async {
                    statisticsRepository.getDailyTotalsInRange(startDate, endDate).first()
                }
                val totalsDeferred = async {
                    statisticsRepository.getTotalsInRange(startDate, endDate).first()
                }
                val yearlyComparisonDeferred = async {
                    statisticsRepository.getYearlyComparison().first()
                }

                val expenses = expensesDeferred.await()
                val incomes = incomesDeferred.await()
                val dailyTotals = dailyTotalsDeferred.await()
                val totals = totalsDeferred.await()
                val yearlyComparison = yearlyComparisonDeferred.await()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    expensesByCategory = expenses,
                    incomesByCategory = incomes,
                    dailyTotals = dailyTotals,
                    totalIncome = totals.first,
                    totalExpenses = totals.second,
                    yearlyComparison = yearlyComparison,
                    startDate = startDate,
                    endDate = endDate
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun onDatesChange(startDate: String, endDate: String) {
        _uiState.value = _uiState.value.copy(startDate = startDate, endDate = endDate)
        loadStatisticsForDateRange(startDate, endDate)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class StatisticsUiState(
    val isLoading: Boolean = false,
    val expensesByCategory: List<CategoryExpense> = emptyList(),
    val incomesByCategory: List<CategoryIncome> = emptyList(),
    val dailyTotals: List<DailyTotal> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val yearlyComparison: List<YearlyComparison> = emptyList(),
    val startDate: String = "",
    val endDate: String = "",
    val error: String? = null
)
