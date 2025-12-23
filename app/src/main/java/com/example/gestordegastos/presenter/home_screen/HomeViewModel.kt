package com.example.gestordegastos.presenter.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.gestordegastos.domain.model.Category
import com.example.gestordegastos.domain.model.Operation
import com.example.gestordegastos.domain.usecase.DeleteOperationUseCase
import com.example.gestordegastos.domain.usecase.GetCategoriaByIdUseCase
import com.example.gestordegastos.domain.usecase.GetOperationsUseCase
import com.example.gestordegastos.domain.usecase.GetPagedOperationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getOperationsUseCase: GetOperationsUseCase,
    private val getCategoryByIdUseCase: GetCategoriaByIdUseCase,
    private val deleteOperationUseCase: DeleteOperationUseCase,
    private val getPagedOperationsUseCase: GetPagedOperationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            startDate = getCurrentMonthDateRange().first,
            endDate = getCurrentMonthDateRange().second
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val income: Flow<PagingData<Operation>> = uiState.flatMapLatest { state ->
        getPagedOperationsUseCase(2, state.startDate, state.endDate)
    }.cachedIn(viewModelScope)

    val bills: Flow<PagingData<Operation>> = uiState.flatMapLatest { state ->
        getPagedOperationsUseCase(1, state.startDate, state.endDate)
    }.cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            uiState.map { it.startDate to it.endDate }
                .distinctUntilChanged()
                .collectLatest { (startDate, endDate) ->
                    updateTotals(startDate, endDate)
                }
        }
    }

    fun onDatesChange(startDate: String, endDate: String) {
        _uiState.update { it.copy(startDate = startDate, endDate = endDate) }
    }

    private fun updateTotals(startDate: String, endDate: String) {
        viewModelScope.launch {
            getOperationsUseCase().collectLatest { operations ->
                val filteredOperations = operations.filter { op ->
                    op.date >= startDate && op.date <= endDate
                }
                val income = filteredOperations.filter { it.typeOperationId == 2 }
                val bills = filteredOperations.filter { it.typeOperationId == 1 }
                val totalIncome = income.sumOf { it.amount }
                val totalBills = bills.sumOf { it.amount }
                val total = totalIncome - totalBills

                _uiState.update {
                    it.copy(
                        total = total,
                        totalIncome = totalIncome,
                        totalBills = totalBills
                    )
                }
            }
        }
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

    suspend fun getCategoriaById(id: Int): Category? {
        return getCategoryByIdUseCase(id)
    }

    fun deleteOperation(id: Int) {
        viewModelScope.launch {
            deleteOperationUseCase(id)
        }
    }
}

data class HomeUiState(
    val total: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalBills: Double = 0.0,
    val startDate: String,
    val endDate: String
)
