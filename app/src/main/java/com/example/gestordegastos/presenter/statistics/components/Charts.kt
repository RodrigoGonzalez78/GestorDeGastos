package com.example.gestordegastos.presenter.statistics.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

data class PieChartData(
    val label: String,
    val value: Double,
    val color: Color
)

data class BarChartData(
    val label: String,
    val value: Double,
    val color: Color
)

data class LineChartData(
    val label: String,
    val value: Double
)

// Colores para gráficos
val chartColors = listOf(
    Color(0xFF3B82F6), // Blue
    Color(0xFF10B981), // Green
    Color(0xFFF59E0B), // Amber
    Color(0xFFEF4444), // Red
    Color(0xFF8B5CF6), // Purple
    Color(0xFFEC4899), // Pink
    Color(0xFF06B6D4), // Cyan
    Color(0xFFF97316), // Orange
    Color(0xFF84CC16), // Lime
    Color(0xFF6366F1)  // Indigo
)

@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier,
    showLegend: Boolean = true
) {
    if (data.isEmpty()) {
        EmptyChartMessage("No hay datos para mostrar")
        return
    }

    val total = data.sumOf { it.value }
    if (total == 0.0) {
        EmptyChartMessage("No hay datos para mostrar")
        return
    }

    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "pie_animation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(180.dp)) {
                var startAngle = -90f

                data.forEach { item ->
                    val sweepAngle = ((item.value / total) * 360f * animatedProgress).toFloat()
                    drawArc(
                        color = item.color,
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

            // Total en el centro
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = formatCurrency(total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }
        }

        if (showLegend) {
            Spacer(modifier = Modifier.height(16.dp))
            PieChartLegend(data = data, total = total)
        }
    }
}

@Composable
fun PieChartLegend(
    data: List<PieChartData>,
    total: Double
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        data.take(5).forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(item.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = formatCurrency(item.value),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "${((item.value / total) * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
fun BarChart(
    incomeValue: Double,
    expenseValue: Double,
    modifier: Modifier = Modifier
) {
    val maxValue = maxOf(incomeValue, expenseValue)
    if (maxValue == 0.0) {
        EmptyChartMessage("No hay datos para mostrar")
        return
    }

    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "bar_animation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Barra de Ingresos
        BarItem(
            label = "Ingresos",
            value = incomeValue,
            maxValue = maxValue,
            color = Color(0xFF10B981),
            progress = animatedProgress
        )

        // Barra de Gastos
        BarItem(
            label = "Gastos",
            value = expenseValue,
            maxValue = maxValue,
            color = Color(0xFFEF4444),
            progress = animatedProgress
        )

        // Balance
        val balance = incomeValue - expenseValue
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (balance >= 0) Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Balance",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (balance >= 0) Color(0xFF059669) else Color(0xFFDC2626)
            )
            Text(
                text = formatCurrency(balance),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (balance >= 0) Color(0xFF059669) else Color(0xFFDC2626)
            )
        }
    }
}

@Composable
fun BarItem(
    label: String,
    value: Double,
    maxValue: Double,
    color: Color,
    progress: Float
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )
            Text(
                text = formatCurrency(value),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFF1F5F9))
        ) {
            val barWidth = ((value / maxValue) * progress).toFloat()
            Box(
                modifier = Modifier
                    .fillMaxWidth(barWidth)
                    .height(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun LineChart(
    data: List<LineChartData>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF3B82F6)
) {
    if (data.isEmpty() || data.size < 2) {
        EmptyChartMessage("Datos insuficientes para el gráfico")
        return
    }

    val maxValue = data.maxOfOrNull { it.value } ?: 0.0
    val minValue = data.minOfOrNull { it.value } ?: 0.0
    val range = if (maxValue == minValue) 1.0 else maxValue - minValue

    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "line_animation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            val width = size.width
            val height = size.height
            val padding = 8.dp.toPx()
            val chartWidth = width - padding * 2
            val chartHeight = height - padding * 2

            // Líneas de fondo
            for (i in 0..4) {
                val y = padding + (chartHeight / 4) * i
                drawLine(
                    color = Color(0xFFE2E8F0),
                    start = Offset(padding, y),
                    end = Offset(width - padding, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Dibujar la línea del gráfico
            val path = Path()
            val points = mutableListOf<Offset>()

            data.forEachIndexed { index, item ->
                val x = padding + (chartWidth / (data.size - 1)) * index
                val normalizedValue = if (range > 0) (item.value - minValue) / range else 0.5
                val y = padding + chartHeight * (1 - normalizedValue.toFloat()) * animatedProgress

                points.add(Offset(x, y))

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            // Línea principal
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            // Puntos en cada valor
            points.forEach { point ->
                drawCircle(
                    color = Color.White,
                    radius = 6.dp.toPx(),
                    center = point
                )
                drawCircle(
                    color = lineColor,
                    radius = 4.dp.toPx(),
                    center = point
                )
            }
        }

        // Etiquetas del eje X
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.take(6).forEach { item ->
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DualLineChart(
    incomeData: List<LineChartData>,
    expenseData: List<LineChartData>,
    modifier: Modifier = Modifier
) {
    if (incomeData.isEmpty() && expenseData.isEmpty()) {
        EmptyChartMessage("No hay datos para mostrar")
        return
    }

    val allValues = incomeData.map { it.value } + expenseData.map { it.value }
    val maxValue = allValues.maxOrNull() ?: 0.0
    val minValue = 0.0
    val range = if (maxValue == minValue) 1.0 else maxValue - minValue

    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "dual_line_animation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Leyenda
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981))
            )
            Text(
                text = " Ingresos",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444))
            )
            Text(
                text = " Gastos",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF64748B)
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            val width = size.width
            val height = size.height
            val padding = 8.dp.toPx()
            val chartWidth = width - padding * 2
            val chartHeight = height - padding * 2

            // Líneas de fondo
            for (i in 0..4) {
                val y = padding + (chartHeight / 4) * i
                drawLine(
                    color = Color(0xFFE2E8F0),
                    start = Offset(padding, y),
                    end = Offset(width - padding, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Función para dibujar línea
            fun drawDataLine(data: List<LineChartData>, color: Color) {
                if (data.size < 2) return
                
                val path = Path()
                val points = mutableListOf<Offset>()

                data.forEachIndexed { index, item ->
                    val x = padding + (chartWidth / (data.size - 1)) * index
                    val normalizedValue = if (range > 0) (item.value - minValue) / range else 0.5
                    val y = padding + chartHeight * (1 - normalizedValue.toFloat()) * animatedProgress

                    points.add(Offset(x, y))

                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }

                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(
                        width = 2.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                points.forEach { point ->
                    drawCircle(
                        color = Color.White,
                        radius = 5.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = color,
                        radius = 3.5.dp.toPx(),
                        center = point
                    )
                }
            }

            // Dibujar ambas líneas
            drawDataLine(incomeData, Color(0xFF10B981))
            drawDataLine(expenseData, Color(0xFFEF4444))
        }

        // Etiquetas del eje X
        val labels = (if (incomeData.isNotEmpty()) incomeData else expenseData).take(6)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { item ->
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun EmptyChartMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center
        )
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
    return format.format(amount)
}
