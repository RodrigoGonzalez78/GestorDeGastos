package com.example.gestordegastos.presenter.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.gestordegastos.domain.model.Category
import com.example.gestordegastos.domain.model.Operation
import com.example.gestordegastos.domain.model.OperationDisplayItem
import com.example.gestordegastos.domain.usecase.DeleteOperationUseCase
import com.example.gestordegastos.domain.usecase.GetCategoriaByIdUseCase
import com.example.gestordegastos.domain.usecase.GetDisplayOperationsUseCase
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
    private val getPagedOperationsUseCase: GetPagedOperationsUseCase,
    private val getDisplayOperationsUseCase: GetDisplayOperationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            startDate = getCurrentMonthDateRange().first,
            endDate = getCurrentMonthDateRange().second
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Display items (operations + installments combined)
    val expenses: StateFlow<List<OperationDisplayItem>> = uiState.flatMapLatest { state ->
        getDisplayOperationsUseCase(1, state.startDate, state.endDate)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val income: StateFlow<List<OperationDisplayItem>> = uiState.flatMapLatest { state ->
        getDisplayOperationsUseCase(2, state.startDate, state.endDate)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
            // Combine regular operations and installments for totals calculation
            combine(
                getDisplayOperationsUseCase(1, startDate, endDate),
                getDisplayOperationsUseCase(2, startDate, endDate)
            ) { expenseItems, incomeItems ->
                val totalBills = expenseItems.sumOf { it.amount }
                val totalIncome = incomeItems.sumOf { it.amount }
                val total = totalIncome - totalBills
                Triple(total, totalIncome, totalBills)
            }.collectLatest { (total, totalIncome, totalBills) ->
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

