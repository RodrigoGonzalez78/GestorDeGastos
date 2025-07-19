package com.example.gestordegastos.presenter.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestordegastos.domain.model.CategoryExpense
import com.example.gestordegastos.domain.model.CategoryIncome
import com.example.gestordegastos.domain.model.MonthlyTotal
import com.example.gestordegastos.domain.model.YearlyComparison
import com.example.gestordegastos.domain.usecase.GetMonthlyExpensesByCategoryUseCase
import com.example.gestordegastos.domain.usecase.GetMonthlyIncomesByCategoryUseCase
import com.example.gestordegastos.domain.usecase.GetMonthlyTotalsUseCase
import com.example.gestordegastos.domain.usecase.GetYearlyComparisonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getMonthlyExpensesByCategoryUseCase: GetMonthlyExpensesByCategoryUseCase,
    private val getMonthlyIncomesByCategoryUseCase: GetMonthlyIncomesByCategoryUseCase,
    private val getMonthlyTotalsUseCase: GetMonthlyTotalsUseCase,
    private val getYearlyComparisonUseCase: GetYearlyComparisonUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private val currentDate = Calendar.getInstance()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        val currentYear = currentDate.get(Calendar.YEAR)
        val currentMonth = currentDate.get(Calendar.MONTH) + 1

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val expensesDeferred = async {
                    getMonthlyExpensesByCategoryUseCase(currentYear, currentMonth).first()
                }
                val incomesDeferred = async {
                    getMonthlyIncomesByCategoryUseCase(currentYear, currentMonth).first()
                }
                val monthlyTotalsDeferred = async {
                    getMonthlyTotalsUseCase(currentYear).first()
                }
                val yearlyComparisonDeferred = async {
                    getYearlyComparisonUseCase().first()
                }

                val expenses = expensesDeferred.await()
                val incomes = incomesDeferred.await()
                val monthlyTotals = monthlyTotalsDeferred.await()
                val yearlyComparison = yearlyComparisonDeferred.await()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    monthlyExpenses = expenses,
                    monthlyIncomes = incomes,
                    monthlyTotals = monthlyTotals,
                    yearlyComparison = yearlyComparison,
                    selectedYear = currentYear,
                    selectedMonth = currentMonth
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun selectMonth(month: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(selectedMonth = month)

            val expenses = getMonthlyExpensesByCategoryUseCase(_uiState.value.selectedYear, month).first()
            val incomes = getMonthlyIncomesByCategoryUseCase(_uiState.value.selectedYear, month).first()

            _uiState.value = _uiState.value.copy(
                monthlyExpenses = expenses,
                monthlyIncomes = incomes
            )
        }
    }

    fun selectYear(year: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(selectedYear = year)

            val monthlyTotals = getMonthlyTotalsUseCase(year).first()
            val expenses = getMonthlyExpensesByCategoryUseCase(year, _uiState.value.selectedMonth).first()
            val incomes = getMonthlyIncomesByCategoryUseCase(year, _uiState.value.selectedMonth).first()

            _uiState.value = _uiState.value.copy(
                monthlyTotals = monthlyTotals,
                monthlyExpenses = expenses,
                monthlyIncomes = incomes
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class StatisticsUiState(
    val isLoading: Boolean = false,
    val monthlyExpenses: List<CategoryExpense> = emptyList(),
    val monthlyIncomes: List<CategoryIncome> = emptyList(),
    val monthlyTotals: List<MonthlyTotal> = emptyList(),
    val yearlyComparison: List<YearlyComparison> = emptyList(),
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    val error: String? = null
)
