package com.example.gestordegastos.presenter.statistics

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.gestordegastos.domain.model.EstadisticasData
import java.text.NumberFormat

//@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
//@Composable
//fun StatisticsScreen(
//    viewModel: EstadisticasViewModel = hiltViewModel(),
//    navController: NavHostController
//) {
//    val uiState by viewModel.uiState.collectAsState()
//
//    var selectedTabIndex by remember { mutableIntStateOf(0) }
//    val tabs = listOf("Resumen", "Gastos", "Ingresos")
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF5F5F5))
//    ) {
//        // TopAppBar
//        TopAppBar(
//            title = { Text("Estadísticas") },
//            colors = TopAppBarDefaults.topAppBarColors(
//                containerColor = Color.White,
//                titleContentColor = Color.Black
//            )
//        )
//
//        // TabRow
//        TabRow(
//            selectedTabIndex = selectedTabIndex,
//            containerColor = Color.White,
//            contentColor = Color(0xFF1976D2)
//        ) {
//            tabs.forEachIndexed { index, title ->
//                Tab(
//                    text = { Text(title) },
//                    selected = selectedTabIndex == index,
//                    onClick = { selectedTabIndex = index }
//                )
//            }
//        }
//
//        // Period Selector
//        PeriodSelector(
//            selectedPeriod = uiState.periodoSeleccionado,
//            onPeriodSelected = { viewModel.cambiarPeriodo(it) }
//        )
//
//        // Content
//        if (uiState.isLoading) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        } else {
//            when (selectedTabIndex) {
//                0 -> ResumenTab(uiState)
//                1 -> GastosTab(uiState)
//                2 -> IngresosTab(uiState)
//            }
//        }
//
//        // Error handling
//        uiState.error?.let { error ->
//            LaunchedEffect(error) {
//                // Mostrar Snackbar o Toast
//            }
//        }
//    }
//}
//
//@Composable
//fun PeriodSelector(
//    selectedPeriod: String,
//    onPeriodSelected: (String) -> Unit
//) {
//    val periodos = listOf("Semana", "Mes", "Año", "Todo")
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = "Período:",
//                fontWeight = FontWeight.Bold,
//                color = Color(0xFF666666)
//            )
//
//            Spacer(modifier = Modifier.width(16.dp))
//
//            LazyRow {
//                items(periodos) { periodo ->
//                    FilterChip(
//                        onClick = { onPeriodSelected(periodo) },
//                        label = { Text(periodo) },
//                        selected = selectedPeriod == periodo,
//                        modifier = Modifier.padding(end = 8.dp),
//                        colors = FilterChipDefaults.filterChipColors(
//                            selectedContainerColor = Color(0xFFE3F2FD),
//                            selectedLabelColor = Color(0xFF1976D2)
//                        )
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ResumenTab(uiState: EstadisticasUiState) {
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        item {
//            ResumenCard(uiState.estadisticas, uiState.periodoSeleccionado)
//        }
//
//        item {
//            TendenciaChart(uiState.operacionesPorDia)
//        }
//
//        item {
//            DistribucionGastosChart(uiState.gastosPorCategoria)
//        }
//
//        item {
//            EstadisticasAdicionalesCard(uiState.estadisticas)
//        }
//    }
//}
//
//@Composable
//fun ResumenCard(estadisticas: EstadisticasData, periodo: String) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(containerColor = Color.White)
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Text(
//                text = "Resumen del $periodo",
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Balance
//            ResumenItem(
//                titulo = "Balance",
//                valor = formatCurrency(estadisticas.balance),
//                color = if (estadisticas.balance >= 0) Color(0xFF4CAF50) else Color(0xFFE57373),
//                icono = if (estadisticas.balance >= 0) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown
//            )
//
//            Divider(modifier = Modifier.padding(vertical = 16.dp))
//
//            Row {
//                ResumenItem(
//                    titulo = "Ingresos",
//                    valor = formatCurrency(estadisticas.totalIngresos),
//                    color = Color(0xFF4CAF50),
//                    icono = Icons.Default.KeyboardArrowUp,
//                    modifier = Modifier.weight(1f)
//                )
//
//                ResumenItem(
//                    titulo = "Gastos",
//                    valor = formatCurrency(estadisticas.totalGastos),
//                    color = Color(0xFFE57373),
//                    icono = Icons.Default.KeyboardArrowDown,
//                    modifier = Modifier.weight(1f)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun ResumenItem(
//    titulo: String,
//    valor: String,
//    color: Color,
//    icono: ImageVector,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        modifier = modifier,
//        crossAxisAlignment = CrossAxisAlignment.Start
//    ) {
//        Text(
//            text = titulo,
//            fontSize = 14.sp,
//            color = Color(0xFF666666)
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Row(
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                imageVector = icono,
//                contentDescription = null,
//                tint = color,
//                modifier = Modifier.size(16.dp)
//            )
//
//            Spacer(modifier = Modifier.width(4.dp))
//
//            Text(
//                text = valor,
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                color = color
//            )
//        }
//    }
//}
//
//@Composable
//fun GastosTab(uiState: EstadisticasUiState) {
//    if (uiState.gastosPorCategoria.isEmpty()) {
//        EmptyState("No hay gastos para mostrar en este período")
//        return
//    }
//
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        item {
//            GastosTotalesCard(uiState.estadisticas)
//        }
//
//        item {
//            CategoriasList(
//                titulo = "Gastos por Categoría",
//                categorias = uiState.gastosPorCategoria,
//                color = Color(0xFFE57373)
//            )
//        }
//    }
//}
//
//@Composable
//fun IngresosTab(uiState: EstadisticasUiState) {
//    if (uiState.ingresosPorCategoria.isEmpty()) {
//        EmptyState("No hay ingresos para mostrar en este período")
//        return
//    }
//
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        item {
//            IngresosTotalesCard(uiState.estadisticas)
//        }
//
//        item {
//            CategoriasList(
//                titulo = "Ingresos por Categoría",
//                categorias = uiState.ingresosPorCategoria,
//                color = Color(0xFF4CAF50)
//            )
//        }
//    }
//}
//
//@Composable
//fun EmptyState(mensaje: String) {
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Icon(
//                imageVector = Icons.Default.BarChart,
//                contentDescription = null,
//                modifier = Modifier.size(64.dp),
//                tint = Color(0xFFBDBDBD)
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                text = mensaje,
//                fontSize = 16.sp,
//                color = Color(0xFF666666),
//                textAlign = TextAlign.Center
//            )
//        }
//    }
//}
//
//// Utility functions
//fun formatCurrency(amount: Double): String {
//    return NumberFormat.getCurrencyInstance().format(amount)
//}
//
//fun getCategoryIcon(categoria: String): ImageVector {
//    return when (categoria.lowercase()) {
//        "comida", "alimento" -> Icons.Default.Restaurant
//        "transporte" -> Icons.Default.DirectionsCar
//        "casa", "hogar" -> Icons.Default.Home
//        "salud" -> Icons.Default.MedicalServices
//        "entretenimiento" -> Icons.Default.Movie
//        "educación" -> Icons.Default.School
//        "salario", "sueldo" -> Icons.Default.Work
//        "ahorro", "inversión" -> Icons.Default.Savings
//        "otros" -> Icons.Default.MoreHoriz
//        else -> Icons.Default.Category
//    }
//}
