package com.example.gestordegastos.presenter.home_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.gestordegastos.domain.model.Category
import com.example.gestordegastos.domain.model.Operation
import com.example.gestordegastos.utils.getIconForName
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNewOperation: (Int) -> Unit,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val income = viewModel.income.collectAsLazyPagingItems()
    val expenses = viewModel.bills.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Hola, Usuario",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Gestiona tus finanzas",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { /* TODO: Navigate to statistics */ },
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                Color(0xFFF1F5F9),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Analytics,
                            contentDescription = "Ver estadísticas",
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNewOperation(-1) },
                containerColor = Color(0xFF0F172A),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(6.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Agregar",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                BalanceSection(
                    total = uiState.total,
                    ingresos = uiState.totalIncome,
                    gastos = uiState.totalBills
                )
            }

            item {
                DateRangeSelector(
                    startDate = uiState.startDate,
                    endDate = uiState.endDate,
                    onDateSelected = { viewModel.onDatesChange(it.first, it.second) }
                )
            }

            item {
                TabbedOperations(
                    income = income,
                    expenses = expenses,
                    getCategoria = { viewModel.getCategoriaById(it) },
                    onDelete = { viewModel.deleteOperation(it) }
                )
            }
        }
    }
}

@Composable
fun BalanceSection(total: Double, ingresos: Double, gastos: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A)
        ),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.1f),
                    radius = 100.dp.toPx(),
                    center = Offset(size.width * 0.8f, -50.dp.toPx())
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = 150.dp.toPx(),
                    center = Offset(size.width * 1.2f, size.height * 0.3f)
                )
            }

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            "Balance Total",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = formatCurrency(total),
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Icon(
                        if (total >= 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        tint = if (total >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BalanceChip(
                        label = "Ingresos",
                        amount = ingresos,
                        color = Color(0xFF10B981),
                        backgroundColor = Color(0xFF10B981).copy(alpha = 0.15f),
                        icon = Icons.Default.ArrowUpward,
                        modifier = Modifier.weight(1f)
                    )

                    BalanceChip(
                        label = "Gastos",
                        amount = gastos,
                        color = Color(0xFFEF4444),
                        backgroundColor = Color(0xFFEF4444).copy(alpha = 0.15f),
                        icon = Icons.Default.ArrowDownward,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun BalanceChip(
    label: String,
    amount: Double,
    color: Color,
    backgroundColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
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
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = formatCurrency(amount),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
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
    var showDatePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
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
                        contentDescription = "Seleccionar fecha",
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        "Rango de fechas",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        "$startDate - $endDate",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )
                }
            }
            Icon(
                Icons.Default.Edit,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }
    }

    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = {
                Text(
                    "Selecciona un rango de fechas",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                DateRangePicker(state = dateRangePickerState)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDatePicker = false
                        val start = dateRangePickerState.selectedStartDateMillis?.let { toDateString(it) }
                        val end = dateRangePickerState.selectedEndDateMillis?.let { toDateString(it) }
                        if (start != null && end != null) {
                            onDateSelected(Pair(start, end))
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
                    onClick = { showDatePicker = false },
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

fun toDateString(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val date = Date(millis)
    return sdf.format(date)
}



@Composable
fun TabbedOperations(
    income: LazyPagingItems<Operation>,
    expenses: LazyPagingItems<Operation>,
    getCategoria: suspend (Int) -> Category?,
    onDelete: (Int) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Gastos Recientes", "Ingresos Recientes")

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Transacciones Recientes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0F172A)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(
                            Color(0xFFF1F5F9),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    tabs.forEachIndexed { index, title ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = index }
                                .background(
                                    if (selectedTab == index) Color.White else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                title,
                                style = MaterialTheme.typography.titleSmall,
                                color = if (selectedTab == index) Color(0xFF0F172A) else Color(0xFF64748B),
                                fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Medium
                            )
                        }
                    }
                }

                val list = if (selectedTab == 0) expenses else income

                if (list.loadState.refresh is LoadState.NotLoading && list.itemCount == 0) {
                    EmptyStateMessage(
                        message = if (selectedTab == 0) "No hay gastos registrados" else "No hay ingresos registrados",
                        icon = if (selectedTab == 0) Icons.Default.ShoppingCart else Icons.Default.AccountBalance
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(list.itemCount) { index ->
                            val operation = list[index]
                            if (operation != null) {
                                OperationCard(
                                    operation = operation,
                                    getCategory = getCategoria,
                                    onDelete = onDelete
                                )
                            }
                        }

                        list.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.padding(16.dp),
                                                color = Color(0xFF0F172A)
                                            )
                                        }
                                    }
                                }
                                loadState.append is LoadState.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.padding(16.dp),
                                                color = Color(0xFF0F172A)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateMessage(message: String, icon: ImageVector) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    Color(0xFFF1F5F9),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color(0xFF94A3B8)
            )
        }

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun OperationCard(
    operation: Operation,
    getCategory: suspend (Int) -> Category?,
    onDelete: (Int) -> Unit
) {
    val categoria by produceState<Category?>(initialValue = null) {
        value = getCategory(operation.categoryId)
    }

    val categoryIcon = getIconForName(categoria?.icon ?: "")
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (operation.typeOperationId == 1)
                                Color(0xFFEF4444).copy(alpha = 0.1f)
                            else
                                Color(0xFF10B981).copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        categoryIcon,
                        contentDescription = null,
                        tint = if (operation.typeOperationId == 1) Color(0xFFEF4444) else Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        categoria?.description ?: "Sin categoría",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        operation.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${if (operation.typeOperationId == 1) "-" else "+"}${formatCurrency(operation.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (operation.typeOperationId == 1) Color(0xFFEF4444) else Color(0xFF10B981),
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Opciones",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Eliminar transacción",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que quieres eliminar esta transacción? Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(operation.id)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Eliminar", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("es", "AR")).format(amount)
}