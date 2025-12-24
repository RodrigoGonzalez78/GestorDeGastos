package com.example.gestordegastos.presenter.export

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestordegastos.domain.usecase.ExportItem
import com.example.gestordegastos.domain.usecase.ExportOperationsUseCase
import com.example.gestordegastos.domain.usecase.GetCategoriaByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val exportOperationsUseCase: ExportOperationsUseCase,
    private val getCategoryByIdUseCase: GetCategoriaByIdUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ExportUiState(
            startDate = getCurrentMonthDateRange().first,
            endDate = getCurrentMonthDateRange().second
        )
    )
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()

    private var lastGeneratedCsvContent: String? = null

    fun updateStartDate(date: String) {
        _uiState.update { it.copy(startDate = date) }
    }

    fun updateEndDate(date: String) {
        _uiState.update { it.copy(endDate = date) }
    }

    fun showStartDatePicker() {
        _uiState.update { it.copy(showStartDatePicker = true) }
    }

    fun hideStartDatePicker() {
        _uiState.update { it.copy(showStartDatePicker = false) }
    }

    fun showEndDatePicker() {
        _uiState.update { it.copy(showEndDatePicker = true) }
    }

    fun hideEndDatePicker() {
        _uiState.update { it.copy(showEndDatePicker = false) }
    }

    fun exportToCSV() {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, error = null) }
            
            try {
                val items = exportOperationsUseCase(_uiState.value.startDate, _uiState.value.endDate)
                
                if (items.isEmpty()) {
                    _uiState.update { 
                        it.copy(
                            isExporting = false, 
                            error = "No hay datos para exportar en el período seleccionado"
                        ) 
                    }
                    return@launch
                }
                
                // Generate CSV content
                val csvContent = generateCSVContent(items)
                lastGeneratedCsvContent = csvContent
                
                // Save to app's external files for sharing
                val fileName = "gastos_${_uiState.value.startDate}_${_uiState.value.endDate}.csv"
                val file = File(context.getExternalFilesDir(null), fileName)
                FileWriter(file).use { writer ->
                    writer.write(csvContent)
                }
                
                // Create share intent
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                
                _uiState.update { 
                    it.copy(
                        isExporting = false, 
                        exportSuccess = true,
                        exportedFileUri = uri,
                        exportedFileName = fileName
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isExporting = false, 
                        error = "Error al exportar: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun saveToDownloads() {
        viewModelScope.launch {
            try {
                val csvContent = lastGeneratedCsvContent
                if (csvContent == null) {
                    _uiState.update { it.copy(error = "No hay archivo para guardar") }
                    return@launch
                }

                val fileName = _uiState.value.exportedFileName ?: return@launch

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10+ use MediaStore
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                        put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                        put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }

                    val resolver = context.contentResolver
                    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    
                    uri?.let {
                        resolver.openOutputStream(it)?.use { outputStream ->
                            outputStream.write(csvContent.toByteArray())
                        }
                        _uiState.update { state ->
                            state.copy(
                                savedToDownloads = true,
                                savedPath = "Descargas/$fileName"
                            )
                        }
                    }
                } else {
                    // Legacy approach for older Android versions
                    @Suppress("DEPRECATION")
                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val file = File(downloadsDir, fileName)
                    FileWriter(file).use { writer ->
                        writer.write(csvContent)
                    }
                    _uiState.update { 
                        it.copy(
                            savedToDownloads = true,
                            savedPath = file.absolutePath
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Error al guardar: ${e.message}")
                }
            }
        }
    }

    private suspend fun generateCSVContent(items: List<ExportItem>): String {
        val sb = StringBuilder()
        
        // Header
        sb.appendLine("Fecha,Tipo,Categoría,Monto,Es Cuota,Número de Cuota,Total Cuotas")
        
        // Data
        for (item in items) {
            val category = getCategoryByIdUseCase(item.categoryId)
            val categoryName = category?.description ?: "Sin categoría"
            
            val installmentInfo = if (item.isInstallment) "Sí" else "No"
            val installmentNumber = item.installmentNumber?.toString() ?: ""
            val totalInstallments = item.totalInstallments?.toString() ?: ""
            
            sb.appendLine("${item.date},${item.type},\"$categoryName\",${item.amount},$installmentInfo,$installmentNumber,$totalInstallments")
        }
        
        return sb.toString()
    }

    fun shareFile() {
        val uri = _uiState.value.exportedFileUri ?: return
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Compartir archivo").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    fun resetExportSuccess() {
        _uiState.update { 
            it.copy(
                exportSuccess = false, 
                exportedFileUri = null, 
                exportedFileName = null,
                savedToDownloads = false,
                savedPath = null
            ) 
        }
        lastGeneratedCsvContent = null
    }

    private fun getCurrentMonthDateRange(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = dateFormat.format(calendar.time)

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endDate = dateFormat.format(calendar.time)

        return Pair(startDate, endDate)
    }
}

data class ExportUiState(
    val startDate: String,
    val endDate: String,
    val showStartDatePicker: Boolean = false,
    val showEndDatePicker: Boolean = false,
    val isExporting: Boolean = false,
    val exportSuccess: Boolean = false,
    val exportedFileUri: Uri? = null,
    val exportedFileName: String? = null,
    val savedToDownloads: Boolean = false,
    val savedPath: String? = null,
    val error: String? = null
)

