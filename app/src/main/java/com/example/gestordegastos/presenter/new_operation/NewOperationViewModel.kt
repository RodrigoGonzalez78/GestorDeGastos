package com.example.gestordegastos.presenter.new_operation

import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestordegastos.domain.model.Category
import com.example.gestordegastos.domain.model.Operation
import com.example.gestordegastos.domain.model.TypeOperation
import com.example.gestordegastos.domain.usecase.GetAllCategoriesUseCase
import com.example.gestordegastos.domain.usecase.GetAllOperationTypesUseCase
import com.example.gestordegastos.domain.usecase.InsertCategoryUseCase
import com.example.gestordegastos.domain.usecase.InsertOperationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NewOperationViewModel @Inject constructor(
    private val insertOperationUseCase: InsertOperationUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val getAllOperationTypesUseCase: GetAllOperationTypesUseCase,
    private val insertCategoryUseCase: InsertCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewOperationUiState())
    val uiState: StateFlow<NewOperationUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val categories = getAllCategoriesUseCase().firstOrNull() ?: emptyList()
                val operationTypes = getAllOperationTypesUseCase().firstOrNull() ?: emptyList()
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    categories = categories,
                    operationTypes = operationTypes,
                    date = currentDate
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar datos: ${e.message}"
                )
            }
        }
    }

    fun selectCategory(categoryId: Int) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
    }

    fun selectOperationType(operationTypeId: Int) {
        _uiState.value = _uiState.value.copy(selectedOperationTypeId = operationTypeId)
    }

    fun updateAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun updateDate(date: String) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun showNewCategoryDialog() {
        _uiState.value = _uiState.value.copy(showNewCategoryDialog = true)
    }

    fun hideNewCategoryDialog() {
        _uiState.value = _uiState.value.copy(showNewCategoryDialog = false)
    }

    fun showDatePicker() {
        _uiState.value = _uiState.value.copy(showDatePicker = true)
    }

    fun hideDatePicker() {
        _uiState.value = _uiState.value.copy(showDatePicker = false)
    }

    fun addNewCategory(description: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val newCategory = Category(
                    description = description,
                    color = Color.RED.toString()
                )
                insertCategoryUseCase(newCategory)

                // Reload categories
                val categories = getAllCategoriesUseCase().firstOrNull()?: emptyList()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    categories = categories,
                    showNewCategoryDialog = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al guardar categoría: ${e.message}"
                )
            }
        }
    }

    fun saveOperation() {
        val currentState = _uiState.value

        // Validations
        if (currentState.amount.isEmpty() ||
            currentState.selectedCategoryId == null ||
            currentState.selectedOperationTypeId == null) {
            _uiState.value = currentState.copy(error = "Por favor complete todos los campos")
            return
        }

        val amount = currentState.amount.replace(",", ".").toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.value = currentState.copy(error = "Ingrese un monto válido")
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true)

            try {
                val operation = Operation(
                    amount = amount,
                    date = currentState.date,
                    categoryId = currentState.selectedCategoryId,
                    typeOperationId = currentState.selectedOperationTypeId
                )

                insertOperationUseCase(operation)
                _uiState.value = currentState.copy(
                    isLoading = false,
                    success = true
                )
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    error = "Error al guardar la operación: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}


data class NewOperationUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val operationTypes: List<TypeOperation> = emptyList(),
    val selectedCategoryId: Int? = null,
    val selectedOperationTypeId: Int? = null,
    val amount: String = "",
    val date: String = "",
    val error: String? = null,
    val success: Boolean = false,
    val showNewCategoryDialog: Boolean = false,
    val showDatePicker: Boolean = false
)