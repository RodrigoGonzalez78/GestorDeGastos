package com.example.gestordegastos.domain.usecase

import com.example.gestordegastos.domain.repository.CategoryExpense
import com.example.gestordegastos.domain.repository.CategoryIncome
import com.example.gestordegastos.domain.repository.MonthlyTotal
import com.example.gestordegastos.domain.repository.StatisticsRepository
import com.example.gestordegastos.domain.repository.YearlyComparison
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


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
