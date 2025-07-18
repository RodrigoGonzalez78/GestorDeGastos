package com.example.gestordegastos.domain.model

data class EstadisticasData(
    val totalIngresos: Double = 0.0,
    val totalGastos: Double = 0.0,
    val balance: Double = 0.0,
    val promedioDiarioGastos: Double = 0.0,
    val promedioDiarioIngresos: Double = 0.0,
    val diaMayorGasto: OperacionPorDia? = null,
    val cantidadOperaciones: Int = 0
)

data class OperacionPorCategoria(
    val categoria: String,
    val monto: Double,
    val porcentaje: Double
)

data class OperacionPorDia(
    val fecha: String,
    val gastos: Double,
    val ingresos: Double
)
