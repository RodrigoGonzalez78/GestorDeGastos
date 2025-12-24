package com.example.gestordegastos.presenter.new_operation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestordegastos.domain.model.Category
import com.example.gestordegastos.domain.model.Installment
import com.example.gestordegastos.domain.model.Operation
import com.example.gestordegastos.domain.model.TypeOperation
import com.example.gestordegastos.domain.usecase.GetAllCategoriesUseCase
import com.example.gestordegastos.domain.usecase.GetAllOperationTypesUseCase
import com.example.gestordegastos.domain.usecase.GetInstallmentsByOperationIdUseCase
import com.example.gestordegastos.domain.usecase.GetOperationByIdUseCase
import com.example.gestordegastos.domain.usecase.InsertCategoryUseCase
import com.example.gestordegastos.domain.usecase.InsertInstallmentsUseCase
import com.example.gestordegastos.domain.usecase.InsertOperationUseCase
import com.example.gestordegastos.domain.usecase.UpdateOperationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NewOperationViewModel @Inject constructor(
    private val insertOperationUseCase: InsertOperationUseCase,
    private val updateOperationUseCase: UpdateOperationUseCase,
    private val getOperationByIdUseCase: GetOperationByIdUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val getAllOperationTypesUseCase: GetAllOperationTypesUseCase,
    private val insertCategoryUseCase: InsertCategoryUseCase,
    private val insertInstallmentsUseCase: InsertInstallmentsUseCase,
    private val getInstallmentsByOperationIdUseCase: GetInstallmentsByOperationIdUseCase
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

    fun loadOperation(operationId: Int) {
        if (operationId <= 0) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val operation = getOperationByIdUseCase(operationId)
                if (operation != null) {
                    // Load installments if it's an installment operation
                    val installments = if (operation.isInstallment) {
                        getInstallmentsByOperationIdUseCase(operationId).firstOrNull() ?: emptyList()
                    } else {
                        emptyList()
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isEditMode = true,
                        editingOperationId = operationId,
                        amount = operation.amount.toString(),
                        date = operation.date,
                        selectedCategoryId = operation.categoryId,
                        selectedOperationTypeId = operation.typeOperationId,
                        isInstallment = operation.isInstallment,
                        numberOfInstallments = if (installments.isNotEmpty()) installments.size.toString() else "",
                        generatedInstallments = installments
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Operación no encontrada"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar la operación: ${e.message}"
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
        // Regenerate installments if needed
        if (_uiState.value.isInstallment) {
            generateInstallmentsPreview()
        }
    }

    fun updateDate(date: String) {
        _uiState.value = _uiState.value.copy(date = date)
        // Regenerate installments if needed
        if (_uiState.value.isInstallment) {
            generateInstallmentsPreview()
        }
    }

    fun toggleInstallment(isInstallment: Boolean) {
        _uiState.value = _uiState.value.copy(
            isInstallment = isInstallment,
            numberOfInstallments = if (!isInstallment) "" else _uiState.value.numberOfInstallments,
            generatedInstallments = if (!isInstallment) emptyList() else _uiState.value.generatedInstallments
        )
    }

    fun updateNumberOfInstallments(number: String) {
        _uiState.value = _uiState.value.copy(numberOfInstallments = number)
        generateInstallmentsPreview()
    }

    private fun generateInstallmentsPreview() {
        val currentState = _uiState.value
        val numInstallments = currentState.numberOfInstallments.toIntOrNull() ?: 0
        val totalAmount = currentState.amount.replace(",", ".").toDoubleOrNull() ?: 0.0

        if (numInstallments <= 0 || totalAmount <= 0) {
            _uiState.value = currentState.copy(generatedInstallments = emptyList())
            return
        }

        val installmentAmount = totalAmount / numInstallments
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        try {
            calendar.time = dateFormat.parse(currentState.date) ?: Date()
        } catch (e: Exception) {
            calendar.time = Date()
        }

        val installments = (1..numInstallments).map { number ->
            val dueDate = dateFormat.format(calendar.time)
            calendar.add(Calendar.MONTH, 1)
            Installment(
                id = 0,
                operationId = 0,
                amount = installmentAmount,
                dueDate = dueDate,
                isPaid = false,
                installmentNumber = number
            )
        }

        _uiState.value = currentState.copy(generatedInstallments = installments)
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

    fun showInstallmentDatePicker(installmentIndex: Int) {
        _uiState.value = _uiState.value.copy(
            showInstallmentDatePicker = true,
            editingInstallmentIndex = installmentIndex
        )
    }

    fun hideInstallmentDatePicker() {
        _uiState.value = _uiState.value.copy(
            showInstallmentDatePicker = false,
            editingInstallmentIndex = -1
        )
    }

    fun updateInstallmentDate(newDate: String) {
        val currentState = _uiState.value
        val index = currentState.editingInstallmentIndex
        if (index < 0 || index >= currentState.generatedInstallments.size) return

        val updatedInstallments = currentState.generatedInstallments.toMutableList()
        updatedInstallments[index] = updatedInstallments[index].copy(dueDate = newDate)
        
        _uiState.value = currentState.copy(
            generatedInstallments = updatedInstallments,
            showInstallmentDatePicker = false,
            editingInstallmentIndex = -1
        )
    }

    fun addNewCategory(description: String, icon: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val newCategory = Category(
                    description = description,
                    icon = icon
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

        // Additional validation for installments
        if (currentState.isInstallment) {
            val numInstallments = currentState.numberOfInstallments.toIntOrNull()
            if (numInstallments == null || numInstallments <= 0) {
                _uiState.value = currentState.copy(error = "Ingrese un número de cuotas válido")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true)

            try {
                val operation = Operation(
                    id = if (currentState.isEditMode) currentState.editingOperationId else 0,
                    amount = amount,
                    date = currentState.date,
                    categoryId = currentState.selectedCategoryId,
                    typeOperationId = currentState.selectedOperationTypeId,
                    isInstallment = currentState.isInstallment
                )

                val operationId: Long
                if (currentState.isEditMode) {
                    updateOperationUseCase(operation)
                    operationId = currentState.editingOperationId.toLong()
                } else {
                    operationId = insertOperationUseCase(operation)
                }

                // Save installments if applicable
                if (currentState.isInstallment && currentState.generatedInstallments.isNotEmpty()) {
                    val installmentsWithOperationId = currentState.generatedInstallments.map {
                        it.copy(operationId = operationId.toInt())
                    }
                    insertInstallmentsUseCase(installmentsWithOperationId)
                }
                
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
    val showDatePicker: Boolean = false,
    val isEditMode: Boolean = false,
    val editingOperationId: Int = 0,
    // Installment related state
    val isInstallment: Boolean = false,
    val numberOfInstallments: String = "",
    val generatedInstallments: List<Installment> = emptyList(),
    // Installment date picker state
    val showInstallmentDatePicker: Boolean = false,
    val editingInstallmentIndex: Int = -1
)