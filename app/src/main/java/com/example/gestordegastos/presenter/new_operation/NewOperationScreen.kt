package com.example.gestordegastos.presenter.new_operation

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.gestordegastos.domain.model.Category
import com.example.gestordegastos.domain.model.TypeOperation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import com.example.gestordegastos.utils.CategoryIcon
import com.example.gestordegastos.utils.getIconForName
import com.example.gestordegastos.utils.iconsCategory
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOperationScreen(
    viewModel: NewOperationViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            snackbarHostState.showSnackbar(
                message = "Operación guardada exitosamente",
                actionLabel = "OK"
            )
            navController.popBackStack()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                actionLabel = "Cerrar"
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Nueva Operación",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0xFFF8FAFC),
                                CircleShape
                            )
                            .border(
                                1.dp,
                                Color(0xFFE2E8F0),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFFAFAFA)
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF0F172A),
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        "Guardando operación...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(20.dp),
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
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0F172A),
                            disabledContainerColor = Color(0xFFE2E8F0)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = uiState.selectedOperationTypeId != null &&
                                uiState.amount.isNotBlank() &&
                                uiState.selectedCategoryId != null
                    ) {
                        Text(
                            text = "Guardar Operación",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (uiState.selectedOperationTypeId != null &&
                                uiState.amount.isNotBlank() &&
                                uiState.selectedCategoryId != null
                            )
                                Color.White else Color(0xFF94A3B8)
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
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Tipo de Operación",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0F172A)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            operationTypes.forEach { operationType ->
                val isSelected = selectedOperationTypeId == operationType.id
                val isExpense = operationType.id == 1
                val color = if (isExpense) Color(0xFFDC2626) else Color(0xFF059669)
                val icon =
                    if (isExpense) Icons.AutoMirrored.Filled.TrendingDown else Icons.Default.TrendingUp

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOperationTypeSelected(operationType.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) color.copy(alpha = 0.05f) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(0.dp),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) color else Color(0xFFE2E8F0)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) color else Color(0xFF94A3B8),
                            modifier = Modifier.size(24.dp)
                        )

                        Text(
                            text = operationType.description,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (isSelected) color else Color(0xFF64748B),
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun AmountSection(
    amount: String,
    selectedOperationTypeId: Int?,
    onAmountChange: (String) -> Unit
) {
    val color = when (selectedOperationTypeId) {
        1 -> Color(0xFFDC2626) // Red for expense
        2 -> Color(0xFF059669) // Green for income
        else -> Color(0xFF64748B)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Monto",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0F172A)
        )

        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChange,
            placeholder = {
                Text(
                    "0.00",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF94A3B8)
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = color,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = color
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}


@Composable
fun DateSection(
    date: String,
    onDateClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Fecha",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0F172A)
        )

        val interactionSource = remember { MutableInteractionSource() }

        OutlinedTextField(
            value = date,
            onValueChange = { },
            placeholder = { Text("Seleccionar fecha", color = Color(0xFF94A3B8)) },
            leadingIcon = {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(18.dp)
                )
            },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onDateClick() },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0F172A),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledBorderColor = Color(0xFFE2E8F0),
                disabledContainerColor = Color.White,
                disabledTextColor = Color(0xFF0F172A)
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = false,
            interactionSource = interactionSource
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
    val selectedCategory = categories.firstOrNull { it.id == selectedCategoryId }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categoría",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )

            IconButton(
                onClick = onAddCategoryClick,
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        Color(0xFFF8FAFC),
                        CircleShape
                    )
                    .border(
                        1.dp,
                        Color(0xFFE2E8F0),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar categoría",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategory?.description ?: "",
                onValueChange = { },
                readOnly = true,
                placeholder = { Text("Seleccionar categoría", color = Color(0xFF94A3B8)) },
                leadingIcon = {
                    selectedCategory?.let { getIconForName (it.icon) }?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                trailingIcon = {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(if (expanded) 180f else 0f)
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0F172A),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                categories.forEach { categoryItem ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = getIconForName(categoryItem.icon),
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    categoryItem.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        },
                        onClick = {
                            onCategorySelected(categoryItem.id)
                            expanded = false
                        },
                        modifier = Modifier.background(
                            if (selectedCategoryId == categoryItem.id)
                                Color(0xFFF8FAFC)
                            else
                                Color.Transparent
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    currentDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.parse(currentDate)?.time
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val selectedDate = Date(millis)
                        onDateSelected(dateFormat.format(selectedDate))
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F172A)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Confirmar",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Cancelar",
                    color = Color(0xFF64748B)
                )
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                titleContentColor = Color(0xFF0F172A),
                headlineContentColor = Color(0xFF0F172A),
                weekdayContentColor = Color(0xFF64748B),
                subheadContentColor = Color(0xFF64748B),
                yearContentColor = Color(0xFF0F172A),
                currentYearContentColor = Color(0xFF0F172A),
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = Color(0xFF0F172A),
                dayContentColor = Color(0xFF0F172A),
                disabledDayContentColor = Color(0xFF94A3B8),
                selectedDayContentColor = Color.White,
                disabledSelectedDayContentColor = Color.White,
                selectedDayContainerColor = Color(0xFF0F172A),
                disabledSelectedDayContainerColor = Color(0xFF94A3B8),
                todayContentColor = Color(0xFF0F172A),
                todayDateBorderColor = Color(0xFF0F172A)
            )
        )
    }
}


@Composable
fun NewCategoryDialog(
    onCategoryAdded: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf<CategoryIcon>(iconsCategory[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Nueva Categoría",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Nombre de la categoría:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B)
                )

                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    placeholder = { Text("Category name", color = Color(0xFF94A3B8)) },
                    leadingIcon = {
                        Icon(
                            selectedIcon.icon,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0F172A),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Text(
                    "Selecciona un ícono:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier
                        .height(160.dp)
                        .padding(top = 8.dp)
                ) {
                    items(iconsCategory) { item ->
                        IconButton(onClick = { selectedIcon = item }) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.name,
                                tint = if (selectedIcon == item) Color(0xFF0F172A) else Color.Gray,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedIcon?.let {
                        onCategoryAdded(categoryName.trim(), it.name)
                        onDismiss()
                    }
                },
                enabled = categoryName.isNotBlank() && selectedIcon != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F172A),
                    disabledContainerColor = Color(0xFFE2E8F0)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Guardar",
                    color = if (categoryName.isNotBlank() && selectedIcon != null) Color.White else Color(0xFF94A3B8)
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancelar", color = Color(0xFF64748B))
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(12.dp)
    )
}

