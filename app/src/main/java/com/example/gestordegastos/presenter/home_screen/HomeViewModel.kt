package com.example.gestordegastos.presenter.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestordegastos.domain.model.Category
import com.example.gestordegastos.domain.model.Operation
import com.example.gestordegastos.domain.usecase.DeleteOperationUseCase
import com.example.gestordegastos.domain.usecase.GetCategoriaByIdUseCase
import com.example.gestordegastos.domain.usecase.GetOperationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getOperationsUseCase: GetOperationsUseCase,
    private val getCategoryByIdUseCase: GetCategoriaByIdUseCase,
    private val deleteOperationUseCase: DeleteOperationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getOperationsUseCase().collectLatest { operations ->
                val income = operations.filter { it.typeOperationId == 2 }
                val bills = operations.filter { it.typeOperationId == 1 }
                val totalIncome = income.sumOf { it.amount }
                val totalBills = bills.sumOf { it.amount }
                val total = totalIncome - totalBills
                _uiState.value = HomeUiState(
                    income = income,
                    bills = bills,
                    total = total,
                    totalIncome = totalIncome,
                    totalBills = totalBills
                )
            }
        }
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
    val income: List<Operation> = emptyList(),
    val bills: List<Operation> = emptyList(),
    val total: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalBills: Double = 0.0
)
