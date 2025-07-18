package com.example.gestordegastos.presenter.home_screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.gestordegastos.domain.model.Operation
import com.example.gestordegastos.presenter.app_navigation.NewOperationRoute
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Gestor de Gastos") },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = "Estadísticas")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(NewOperationRoute) }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Operación")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            BalanceSection(
                total = uiState.total,
                ingresos = uiState.totalIncome,
                gastos = uiState.totalBills
            )
            Spacer(Modifier.height(8.dp))
            TabbedOperations(
                income = uiState.income,
                expenses = uiState.bills,
                getCategoria = viewModel::getCategoriaById,
                onDelete = viewModel::deleteOperation
            )
        }
    }
}

@Composable
fun BalanceSection(total: Double, ingresos: Double, gastos: Double) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Balance Total", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(4.dp))
        Text(
            text = formatCurrency(total),
            style = MaterialTheme.typography.headlineMedium,
            color = if (total >= 0) Color.Black else Color.Red
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            BalanceChip("Ingresos", ingresos, Color(0xFF4CAF50), Icons.Default.ArrowUpward)
            Spacer(Modifier.width(16.dp))
            BalanceChip("Gastos", gastos, Color(0xFFF44336), Icons.Default.ArrowDownward)
        }
    }
}

@Composable
fun BalanceChip(label: String, amount: Double, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = label, tint = color)
        Spacer(Modifier.width(4.dp))
        Text(text = formatCurrency(amount), color = color)
    }
}

@Composable
fun TabbedOperations(
    income: List<Operation>,
    expenses: List<Operation>,
    getCategoria: suspend (Int) -> String,
    onDelete: (Int) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Gastos", "Ingresos")

    TabRow(selectedTabIndex = selectedTab) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { selectedTab = index },
                text = { Text(title) }
            )
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val list = if (selectedTab == 0) expenses else income
        items(list) { operation ->
            OperationCard(operation = operation , getCategory = getCategoria, onDelete = onDelete)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun OperationCard(
    operation: Operation,
    getCategory: suspend (Int) -> String,
    onDelete: (Int) -> Unit
) {
    val categoria by produceState<String?>(initialValue = null, operation) {
        value = getCategory(operation.categoryId)
    }
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        ListItem(
            headlineContent = {
                Text(categoria ?: "Sin categoría", fontWeight = FontWeight.Bold)
            },
            supportingContent = {
                Text(operation.date)
            },
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatCurrency(operation.amount),
                        color = if (operation.typeOperationId == 1) Color.Red else Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { onDelete(operation.id) }) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = "Eliminar")
                    }
                }
            }
        )
    }
}

fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("es", "AR")).format(amount)
}