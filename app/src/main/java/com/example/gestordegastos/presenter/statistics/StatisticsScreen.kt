package com.example.gestordegastos.presenter.statistics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.gestordegastos.domain.model.CategoryExpense
import com.example.gestordegastos.domain.model.CategoryIncome
import com.example.gestordegastos.domain.model.DailyTotal
import com.example.gestordegastos.domain.model.MonthlyTotal
import com.example.gestordegastos.domain.model.YearlyComparison
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

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
                        "Estadísticas",
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
                        "Cargando estadísticas...",
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
                    DateRangeSelector(
                        startDate = uiState.startDate,
                        endDate = uiState.endDate,
                        onDateSelected = { viewModel.onDatesChange(it.first, it.second) }
                    )
                }

                // Gráfico de torta - Gastos por categoría
                item {
                    ExpensesPieChartCard(
                        expenses = uiState.expensesByCategory
                    )
                }


                item {
                    IncomeVsExpenseChartCard(
                        totalIncome = uiState.totalIncome,
                        totalExpenses = uiState.totalExpenses
                    )
                }


                item {
                    TrendsLineChartCard(
                        dailyTotals = uiState.dailyTotals
                    )
                }

                item {
                    ExpensesByCategoryCard(
                        expenses = uiState.expensesByCategory
                    )
                }

                item {
                    IncomesByCategoryCard(
                        incomes = uiState.incomesByCategory
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelector(
    startDate: String,
    endDate: String,
    onDateSelected: (Pair<String, String>) -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var currentStartDate by remember { mutableStateOf(startDate) }
    var currentEndDate by remember { mutableStateOf(endDate) }

    LaunchedEffect(startDate, endDate) {
        currentStartDate = startDate
        currentEndDate = endDate
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color(0xFFF1F5F9),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Rango de fechas",
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    "Período de análisis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showStartDatePicker = true },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    elevation = CardDefaults.cardElevation(0.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "Desde",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                formatDisplayDate(currentStartDate),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showEndDatePicker = true },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    elevation = CardDefaults.cardElevation(0.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "Hasta",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Event,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                formatDisplayDate(currentEndDate),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showStartDatePicker) {
        val endDateMillis = parseDateToMillis(currentEndDate)
        val startDatePickerState = rememberDatePickerState(
            initialSelectedDateMillis = parseDateToMillis(currentStartDate),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis <= endDateMillis
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        showStartDatePicker = false
                        startDatePickerState.selectedDateMillis?.let { millis ->
                            val newStartDate = toDateString(millis)
                            currentStartDate = newStartDate
                            onDateSelected(Pair(newStartDate, currentEndDate))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F172A)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Aceptar", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showStartDatePicker = false },
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar", color = Color(0xFF64748B))
                }
            },
            colors = DatePickerDefaults.colors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            DatePicker(
                state = startDatePickerState,
                title = {
                    Text(
                        "Seleccionar fecha de inicio",
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    }

    if (showEndDatePicker) {
        val startDateMillis = parseDateToMillis(currentStartDate)
        val endDatePickerState = rememberDatePickerState(
            initialSelectedDateMillis = parseDateToMillis(currentEndDate),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= startDateMillis
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        showEndDatePicker = false
                        endDatePickerState.selectedDateMillis?.let { millis ->
                            val newEndDate = toDateString(millis)
                            currentEndDate = newEndDate
                            onDateSelected(Pair(currentStartDate, newEndDate))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F172A)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Aceptar", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showEndDatePicker = false },
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar", color = Color(0xFF64748B))
                }
            },
            colors = DatePickerDefaults.colors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            DatePicker(
                state = endDatePickerState,
                title = {
                    Text(
                        "Seleccionar fecha de fin",
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    }
}

fun formatDisplayDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("es", "AR"))
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

fun parseDateToMillis(dateString: String): Long {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        format.parse(dateString)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}

fun toDateString(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val date = Date(millis)
    return sdf.format(date)
}




@Composable
fun StatisticItem(
    label: String,
    amount: Double,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = formatCurrency(amount),
                style = MaterialTheme.typography.titleLarge,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ExpensesByCategoryCard(
    expenses: List<CategoryExpense>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Gastos por Categoría",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )

            if (expenses.isNotEmpty()) {
                expenses.take(5).forEach { expense ->
                    CategoryExpenseItem(expense = expense)
                }
            } else {
                Text(
                    "No hay gastos en este período",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CategoryExpenseItem(expense: CategoryExpense) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                expense.categoryName,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Medium
            )

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    formatCurrency(expense.totalAmount),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFDC2626),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${expense.percentage.toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )
            }
        }

        LinearProgressIndicator(
            progress = expense.percentage / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = Color(0xFFDC2626),
            trackColor = Color(0xFFE2E8F0)
        )
    }
}

@Composable
fun IncomesByCategoryCard(
    incomes: List<CategoryIncome>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Ingresos por Categoría",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )

            if (incomes.isNotEmpty()) {
                incomes.take(5).forEach { income ->
                    CategoryIncomeItem(income = income)
                }
            } else {
                Text(
                    "No hay ingresos en este período",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CategoryIncomeItem(income: CategoryIncome) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                income.categoryName,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Medium
            )

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    formatCurrency(income.totalAmount),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF059669),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${income.percentage.toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )
            }
        }

        LinearProgressIndicator(
            progress = income.percentage / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = Color(0xFF059669),
            trackColor = Color(0xFFE2E8F0)
        )
    }
}

@Composable
fun MonthlyTrendsCard(
    monthlyTotals: List<MonthlyTotal>
) {
    val months = listOf(
        "Ene", "Feb", "Mar", "Abr", "May", "Jun",
        "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Tendencia Mensual",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )

            if (monthlyTotals.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(monthlyTotals) { monthData ->
                        MonthlyTrendItem(
                            month = months[monthData.month - 1],
                            balance = monthData.balance,
                            income = monthData.totalIncome,
                            expenses = monthData.totalExpenses
                        )
                    }
                }
            } else {
                Text(
                    "No hay datos disponibles",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun MonthlyTrendItem(
    month: String,
    balance: Double,
    income: Double,
    expenses: Double
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8FAFC)
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                month,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Medium
            )

            Text(
                formatCurrency(balance),
                style = MaterialTheme.typography.titleSmall,
                color = if (balance >= 0) Color(0xFF059669) else Color(0xFFDC2626),
                fontWeight = FontWeight.SemiBold
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    formatCurrency(income),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF059669)
                )
                Text(
                    formatCurrency(expenses),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFDC2626)
                )
            }
        }
    }
}

@Composable
fun YearlyComparisonCard(
    yearlyComparison: List<YearlyComparison>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Comparación Anual",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )

            if (yearlyComparison.isNotEmpty()) {
                yearlyComparison.forEach { yearData ->
                    YearlyComparisonItem(yearData = yearData)
                }
            } else {
                Text(
                    "No hay datos disponibles",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun YearlyComparisonItem(yearData: YearlyComparison) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8FAFC)
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                yearData.year.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.SemiBold
            )

            // Ingresos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Ingresos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
                Text(
                    formatCurrency(yearData.totalIncome),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF059669),
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Gastos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Gastos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
                Text(
                    formatCurrency(yearData.totalExpenses),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFDC2626),
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Balance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (yearData.balance >= 0) Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Balance",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (yearData.balance >= 0) Color(0xFF059669) else Color(0xFFDC2626),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    formatCurrency(yearData.balance),
                    style = MaterialTheme.typography.titleSmall,
                    color = if (yearData.balance >= 0) Color(0xFF059669) else Color(0xFFDC2626),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("es", "AR")).format(amount)
}

@Composable
fun ExpensesPieChartCard(
    expenses: List<CategoryExpense>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Distribución de Gastos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )

            if (expenses.isNotEmpty()) {
                val chartColors = listOf(
                    Color(0xFF3B82F6),
                    Color(0xFF10B981),
                    Color(0xFFF59E0B),
                    Color(0xFFEF4444),
                    Color(0xFF8B5CF6),
                    Color(0xFFEC4899),
                    Color(0xFF06B6D4),
                    Color(0xFFF97316)
                )

                val total = expenses.sumOf { it.totalAmount }
                
                // Gráfico de dona simple
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(180.dp)) {
                        var startAngle = -90f
                        expenses.forEachIndexed { index, expense ->
                            val sweepAngle = ((expense.totalAmount / total) * 360f).toFloat()
                            drawArc(
                                color = chartColors[index % chartColors.size],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                size = Size(size.width, size.height)
                            )
                            startAngle += sweepAngle
                        }
                        // Centro blanco para efecto donut
                        drawCircle(
                            color = Color.White,
                            radius = size.width * 0.35f,
                            center = Offset(size.width / 2, size.height / 2)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Total", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                        Text(
                            formatCurrency(total),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }

                // Leyenda
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    expenses.take(5).forEachIndexed { index, expense ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(chartColors[index % chartColors.size])
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    expense.categoryName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF475569)
                                )
                            }
                            Text(
                                "${expense.percentage.toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay gastos en este período", color = Color(0xFF94A3B8))
                }
            }
        }
    }
}

@Composable
fun IncomeVsExpenseChartCard(
    totalIncome: Double,
    totalExpenses: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Ingresos vs Gastos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )

            val maxValue = maxOf(totalIncome, totalExpenses)
            
            if (maxValue > 0) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Barra de ingresos
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Ingresos", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                            Text(formatCurrency(totalIncome), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Color(0xFF10B981))
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF1F5F9))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth((totalIncome / maxValue).toFloat())
                                    .height(20.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF10B981))
                            )
                        }
                    }

                    // Barra de gastos
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Gastos", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                            Text(formatCurrency(totalExpenses), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Color(0xFFEF4444))
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF1F5F9))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth((totalExpenses / maxValue).toFloat())
                                    .height(20.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFEF4444))
                            )
                        }
                    }

                    // Balance
                    val balance = totalIncome - totalExpenses
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (balance >= 0) Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Balance del período",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (balance >= 0) Color(0xFF059669) else Color(0xFFDC2626)
                            )
                            Text(
                                formatCurrency(balance),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (balance >= 0) Color(0xFF059669) else Color(0xFFDC2626)
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay datos para mostrar", color = Color(0xFF94A3B8))
                }
            }
        }
    }
}

@Composable
fun TrendsLineChartCard(
    dailyTotals: List<DailyTotal>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Evolución del Período",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )

            if (dailyTotals.isNotEmpty() && dailyTotals.size >= 2) {
                // Leyenda
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF10B981)))
                    Text(" Ingresos", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                    Text(" Gastos", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                }

                val maxValue = dailyTotals.maxOfOrNull { maxOf(it.totalIncome, it.totalExpenses) } ?: 1.0

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    val width = size.width
                    val height = size.height
                    val padding = 16f
                    val chartWidth = width - padding * 2
                    val chartHeight = height - padding * 2

                    // Líneas de fondo
                    for (i in 0..4) {
                        val y = padding + (chartHeight / 4) * i
                        drawLine(Color(0xFFE2E8F0), Offset(padding, y), Offset(width - padding, y), 1f)
                    }

                    // Dibujar línea de ingresos
                    if (dailyTotals.size >= 2) {
                        val incomePath = Path()
                        dailyTotals.forEachIndexed { index, data ->
                            val x = padding + (chartWidth / (dailyTotals.size - 1)) * index
                            val y = padding + chartHeight * (1 - (data.totalIncome / maxValue).toFloat())
                            if (index == 0) incomePath.moveTo(x, y) else incomePath.lineTo(x, y)
                        }
                        drawPath(incomePath, Color(0xFF10B981), style = Stroke(width = 3f, cap = StrokeCap.Round))

                        // Puntos
                        dailyTotals.forEachIndexed { index, data ->
                            val x = padding + (chartWidth / (dailyTotals.size - 1)) * index
                            val y = padding + chartHeight * (1 - (data.totalIncome / maxValue).toFloat())
                            drawCircle(Color.White, 6f, Offset(x, y))
                            drawCircle(Color(0xFF10B981), 4f, Offset(x, y))
                        }
                    }

                    // Dibujar línea de gastos
                    if (dailyTotals.size >= 2) {
                        val expensePath = Path()
                        dailyTotals.forEachIndexed { index, data ->
                            val x = padding + (chartWidth / (dailyTotals.size - 1)) * index
                            val y = padding + chartHeight * (1 - (data.totalExpenses / maxValue).toFloat())
                            if (index == 0) expensePath.moveTo(x, y) else expensePath.lineTo(x, y)
                        }
                        drawPath(expensePath, Color(0xFFEF4444), style = Stroke(width = 3f, cap = StrokeCap.Round))

                        // Puntos
                        dailyTotals.forEachIndexed { index, data ->
                            val x = padding + (chartWidth / (dailyTotals.size - 1)) * index
                            val y = padding + chartHeight * (1 - (data.totalExpenses / maxValue).toFloat())
                            drawCircle(Color.White, 6f, Offset(x, y))
                            drawCircle(Color(0xFFEF4444), 4f, Offset(x, y))
                        }
                    }
                }

                // Etiquetas de días (mostrar algunos)
                val labelsToShow = if (dailyTotals.size <= 7) dailyTotals else {
                    val step = (dailyTotals.size - 1) / 5
                    dailyTotals.filterIndexed { index, _ -> index % step == 0 || index == dailyTotals.size - 1 }.take(6)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    labelsToShow.forEach { data ->
                        Text(
                            data.dayLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Datos insuficientes para el gráfico", color = Color(0xFF94A3B8))
                }
            }
        }
    }
}
