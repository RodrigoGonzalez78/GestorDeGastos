package com.example.gestordegastos.domain.usecase

import com.example.gestordegastos.domain.model.CategoryExpense
import com.example.gestordegastos.domain.model.CategoryIncome
import com.example.gestordegastos.domain.model.MonthlyTotal
import com.example.gestordegastos.domain.repository.StatisticsRepository
import com.example.gestordegastos.domain.model.YearlyComparison
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetMonthlyExpensesByCategoryUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) {
    suspend operator fun invoke(year: Int, month: Int): Flow<List<CategoryExpense>> {
        return statisticsRepository.getMonthlyExpensesByCategory(year, month)
    }
}


class GetMonthlyIncomesByCategoryUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) {
    suspend operator fun invoke(year: Int, month: Int): Flow<List<CategoryIncome>> {
        return statisticsRepository.getMonthlyIncomesByCategory(year, month)
    }
}


class GetMonthlyTotalsUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) {
    suspend operator fun invoke(year: Int): Flow<List<MonthlyTotal>> {
        return statisticsRepository.getMonthlyTotals(year)
    }
}

class GetYearlyComparisonUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) {
    suspend operator fun invoke(): Flow<List<YearlyComparison>> {
        return statisticsRepository.getYearlyComparison()
    }
}
