package com.example.gestordegastos.data.repository

import com.example.gestordegastos.data.local.dao.CategoryDao
import com.example.gestordegastos.data.local.dao.OperationDao
import com.example.gestordegastos.domain.model.Operation
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


//@Singleton
//class StatisticsRepositoryImpl @Inject constructor(
//    private val operationDao: OperationDao,
//    private val categoryDao: CategoryDao
//) {
//    suspend fun getOperationsPerPeriod(periodo: String): List<Operation> {
//        val initialDate =  calculateStartDate(periodo)
//        return if (initialDate != null) {
//            initialDate.getOperacionesPorPeriodo(initialDate)
//        } else {
//            operacionDao.getAllOperaciones()
//        }
//    }
//
//    suspend fun getCategoriaById(id: Int): Categoria? {
//        return categoriaDao.getCategoriaById(id)
//    }
//
//    private fun calculateStartDate(periodo: String): String? {
//        val hour = Date()
//        val calendar = Calendar.getInstance()
//        calendar.time = hour
//
//        return when (periodo) {
//            "Semana" -> {
//                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
//                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
//            }
//            "Mes" -> {
//                calendar.set(Calendar.DAY_OF_MONTH, 1)
//                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
//            }
//            "Año" -> {
//                calendar.set(Calendar.DAY_OF_YEAR, 1)
//                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
//            }
//            else -> null
//        }
//    }
//}
