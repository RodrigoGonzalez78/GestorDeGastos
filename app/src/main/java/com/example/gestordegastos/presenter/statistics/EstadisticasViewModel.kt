package com.example.gestordegastos.presenter.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//@HiltViewModel
//class EstadisticasViewModel @Inject constructor(
//    private val repository: EstadisticasRepository
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(EstadisticasUiState())
//    val uiState: StateFlow<EstadisticasUiState> = _uiState.asStateFlow()
//
//    init {
//        cargarEstadisticas()
//    }
//
//    fun cambiarPeriodo(nuevoPeriodo: String) {
//        _uiState.value = _uiState.value.copy(periodoSeleccionado = nuevoPeriodo)
//        cargarEstadisticas()
//    }
//
//    private fun cargarEstadisticas() {
//        viewModelScope.launch {
//            try {
//                _uiState.value = _uiState.value.copy(isLoading = true)
//
//                val operaciones = repository.getOperacionesPorPeriodo(_uiState.value.periodoSeleccionado)
//                val estadisticas = calcularEstadisticas(operaciones)
//
//                _uiState.value = _uiState.value.copy(
//                    isLoading = false,
//                    estadisticas = estadisticas.first,
//                    gastosPorCategoria = estadisticas.second,
//                    ingresosPorCategoria = estadisticas.third,
//                    operacionesPorDia = estadisticas.fourth
//                )
//            } catch (e: Exception) {
//                _uiState.value = _uiState.value.copy(
//                    isLoading = false,
//                    error = "Error al cargar estadísticas: ${e.message}"
//                )
//            }
//        }
//    }
//
//    private suspend fun calcularEstadisticas(operaciones: List<Operacion>):
//            Quadruple<EstadisticasData, List<OperacionPorCategoria>, List<OperacionPorCategoria>, List<OperacionPorDia>> {
//
//        var totalIngresos = 0.0
//        var totalGastos = 0.0
//
//        // Calcular totales
//        operaciones.forEach { op ->
//            when (op.tipoOperacion) {
//                1 -> totalGastos += op.monto
//                2 -> totalIngresos += op.monto
//            }
//        }
//
//        // Calcular por categoría
//        val gastosPorCategoriaMap = mutableMapOf<Int, Double>()
//        val ingresosPorCategoriaMap = mutableMapOf<Int, Double>()
//
//        operaciones.forEach { op ->
//            when (op.tipoOperacion) {
//                1 -> gastosPorCategoriaMap[op.categoria] =
//                    gastosPorCategoriaMap.getOrDefault(op.categoria, 0.0) + op.monto
//                2 -> ingresosPorCategoriaMap[op.categoria] =
//                    ingresosPorCategoriaMap.getOrDefault(op.categoria, 0.0) + op.monto
//            }
//        }
//
//        // Convertir a lista con nombres de categorías
//        val gastosPorCategoria = gastosPorCategoriaMap.map { (categoriaId, monto) ->
//            val categoria = repository.getCategoriaById(categoriaId)
//            OperacionPorCategoria(
//                categoria = categoria?.descripcion ?: "Sin categoría",
//                monto = monto,
//                porcentaje = if (totalGastos > 0) (monto / totalGastos) * 100 else 0.0
//            )
//        }.sortedByDescending { it.monto }
//
//        val ingresosPorCategoria = ingresosPorCategoriaMap.map { (categoriaId, monto) ->
//            val categoria = repository.getCategoriaById(categoriaId)
//            OperacionPorCategoria(
//                categoria = categoria?.descripcion ?: "Sin categoría",
//                monto = monto,
//                porcentaje = if (totalIngresos > 0) (monto / totalIngresos) * 100 else 0.0
//            )
//        }.sortedByDescending { it.monto }
//
//        // Calcular por día
//        val operacionesPorDiaMap = mutableMapOf<String, Pair<Double, Double>>()
//
//        operaciones.forEach { op ->
//            val fecha = op.fecha
//            val actual = operacionesPorDiaMap.getOrDefault(fecha, Pair(0.0, 0.0))
//
//            when (op.tipoOperacion) {
//                1 -> operacionesPorDiaMap[fecha] = Pair(actual.first + op.monto, actual.second)
//                2 -> operacionesPorDiaMap[fecha] = Pair(actual.first, actual.second + op.monto)
//            }
//        }
//
//        val operacionesPorDia = operacionesPorDiaMap.map { (fecha, montos) ->
//            OperacionPorDia(
//                fecha = fecha,
//                gastos = montos.first,
//                ingresos = montos.second
//            )
//        }.sortedBy { it.fecha }
//
//        // Calcular estadísticas adicionales
//        val promedioDiarioGastos = if (operacionesPorDia.isNotEmpty())
//            totalGastos / operacionesPorDia.size else 0.0
//        val promedioDiarioIngresos = if (operacionesPorDia.isNotEmpty())
//            totalIngresos / operacionesPorDia.size else 0.0
//
//        val diaMayorGasto = operacionesPorDia.maxByOrNull { it.gastos }
//
//        val estadisticas = EstadisticasData(
//            totalIngresos = totalIngresos,
//            totalGastos = totalGastos,
//            balance = totalIngresos - totalGastos,
//            promedioDiarioGastos = promedioDiarioGastos,
//            promedioDiarioIngresos = promedioDiarioIngresos,
//            diaMayorGasto = diaMayorGasto,
//            cantidadOperaciones = operaciones.size
//        )
//
//        return Quadruple(estadisticas, gastosPorCategoria, ingresosPorCategoria, operacionesPorDia)
//    }
//}
//
//// Clase auxiliar para retornar 4 valores
//data class Quadruple<A, B, C, D>(
//    val first: A,
//    val second: B,
//    val third: C,
//    val fourth: D
//)
//
//// EstadisticasUiState.kt
//data class EstadisticasUiState(
//    val isLoading: Boolean = false,
//    val periodoSeleccionado: String = "Mes",
//    val estadisticas: EstadisticasData = EstadisticasData(),
//    val gastosPorCategoria: List<OperacionPorCategoria> = emptyList(),
//    val ingresosPorCategoria: List<OperacionPorCategoria> = emptyList(),
//    val operacionesPorDia: List<OperacionPorDia> = emptyList(),
//    val error: String? = null
//)