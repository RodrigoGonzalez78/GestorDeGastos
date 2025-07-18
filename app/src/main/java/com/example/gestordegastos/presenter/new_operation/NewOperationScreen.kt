package com.example.gestordegastos.presenter.new_operation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.gestordegastos.domain.model.Category
import com.example.gestordegastos.domain.model.TypeOperation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOperationScreen(
    viewModel: NewOperationViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current


    LaunchedEffect(uiState.success) {
        if (uiState.success) {

        }
    }


    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            // Show snackbar or toast
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        TopAppBar(
            title = { Text("Nueva Operación") },
            navigationIcon = {
                IconButton(onClick ={}) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.Black
            )
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    OperationTypeSection(
                        operationTypes = uiState.operationTypes,
                        selectedOperationTypeId = uiState.selectedOperationTypeId,
                        onOperationTypeSelected = viewModel::selectOperationType
                    )
                }

                item {
                    AmountSection(
                        amount = uiState.amount,
                        selectedOperationTypeId = uiState.selectedOperationTypeId,
                        onAmountChange = viewModel::updateAmount
                    )
                }

                item {
                    DateSection(
                        date = uiState.date,
                        onDateClick = viewModel::showDatePicker
                    )
                }

                item {
                    CategorySection(
                        categories = uiState.categories,
                        selectedCategoryId = uiState.selectedCategoryId,
                        onCategorySelected = viewModel::selectCategory,
                        onAddCategoryClick = viewModel::showNewCategoryDialog
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = viewModel::saveOperation,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "GUARDAR",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (uiState.showDatePicker) {
        DatePickerDialog(
            currentDate = uiState.date,
            onDateSelected = { selectedDate ->
                viewModel.updateDate(selectedDate)
                viewModel.hideDatePicker()
            },
            onDismiss = viewModel::hideDatePicker
        )
    }

    if (uiState.showNewCategoryDialog) {
        NewCategoryDialog(
            onCategoryAdded = viewModel::addNewCategory,
            onDismiss = viewModel::hideNewCategoryDialog
        )
    }
}

@Composable
fun OperationTypeSection(
    operationTypes: List<TypeOperation>,
    selectedOperationTypeId: Int?,
    onOperationTypeSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Text(
                text = "Tipo de Operación",
                modifier = Modifier.padding(16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF757575)
            )

            Divider(color = Color(0xFFE0E0E0))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                operationTypes.forEach { operationType ->
                    val isSelected = selectedOperationTypeId == operationType.id
                    val isExpense = operationType.id == 1 // Assuming id=1 is expense
                    val color = if (isExpense) Color(0xFFEF5350) else Color(0xFF66BB6A)
                    val icon = if (isExpense) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOperationTypeSelected(operationType.id) }
                            .background(
                                if (isSelected) color.copy(alpha = 0.1f) else Color.Transparent
                            )
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) color else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = operationType.description,
                                color = if (isSelected) color else Color(0xFF757575),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmountSection(
    amount: String,
    selectedOperationTypeId: Int?,
    onAmountChange: (String) -> Unit
) {
    val color = when (selectedOperationTypeId) {
        1 -> Color(0xFFD32F2F) // Red for expense
        2 -> Color(0xFF388E3C) // Green for income
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChange,
            label = { Text("Monto") },
            leadingIcon = {
                Icon(
                    Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = color
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSection(
    date: String,
    onDateClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        OutlinedTextField(
            value = date,
            onValueChange = { },
            label = { Text("Fecha") },
            leadingIcon = {
                Icon(Icons.Default.CalendarToday, contentDescription = null)
            },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onDateClick() },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySection(
    categories: List<Category>,
    selectedCategoryId: Int?,
    onCategorySelected: (Int) -> Unit,
    onAddCategoryClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Categoría",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF757575)
                )

                IconButton(onClick = onAddCategoryClick) {
                    Icon(
                        Icons.Default.AddCircleOutline,
                        contentDescription = "Agregar categoría",
                        tint = Color(0xFF1976D2)
                    )
                }
            }

            Divider(color = Color(0xFFE0E0E0))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = categories.firstOrNull() { it.id == selectedCategoryId }?.description ?: "",
                    onValueChange = { },
                    readOnly = true,
                    placeholder = { Text("Seleccionar categoría") },
                    trailingIcon = {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { categoryItem ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = getCategoryIcon(categoryItem.description),
                                        contentDescription = null,
                                        tint = Color(0xFF757575),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(categoryItem.description)
                                }
                            },
                            onClick = {
                                onCategorySelected(categoryItem.id)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DatePickerDialog(
    currentDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Parse current date
    try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(currentDate)
        date?.let { calendar.time = it }
    } catch (e: Exception) {
        // Use current date if parsing fails
    }

    LaunchedEffect(Unit) {
        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                onDateSelected(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.setOnDismissListener { onDismiss() }
        datePickerDialog.show()
    }
}

@Composable
fun NewCategoryDialog(
    onCategoryAdded: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var categoryName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Categoría") },
        text = {
            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Nombre de la categoría") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (categoryName.isNotBlank()) {
                        onCategoryAdded(categoryName.trim())
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "comida", "alimento" -> Icons.Default.Restaurant
        "transporte" -> Icons.Default.DirectionsCar
        "casa", "hogar" -> Icons.Default.Home
        "salud" -> Icons.Default.MedicalServices
        "entretenimiento" -> Icons.Default.Movie
        "educación" -> Icons.Default.School
        "salario", "sueldo" -> Icons.Default.Work
        "ahorro", "inversión" -> Icons.Default.Savings
        else -> Icons.Default.Category
    }
}
