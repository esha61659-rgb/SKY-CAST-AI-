package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HourlyForecastItem
import com.example.ui.theme.RainBlue
import com.example.ui.theme.SkyPrimaryCyan
import com.example.ui.theme.SkyTertiaryGold
import kotlin.math.roundToInt

@Composable
fun TemperatureTrendChart(
    hourlyItems: List<HourlyForecastItem>,
    modifier: Modifier = Modifier,
    chartHeight: Int = 180
) {
    if (hourlyItems.isEmpty()) return

    val displayItems = remember(hourlyItems) { hourlyItems.take(12) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val minTemp = displayItems.minOfOrNull { it.temperature } ?: 0.0
    val maxTemp = displayItems.maxOfOrNull { it.temperature } ?: 40.0
    val tempRange = (maxTemp - minTemp).coerceAtLeast(4.0)

    val primaryColor = SkyPrimaryCyan
    val secondaryColor = SkyTertiaryGold

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TEMPERATURE TREND",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "12-Hour Hourly Trajectory",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                selectedIndex?.let { idx ->
                    val item = displayItems.getOrNull(idx)
                    if (item != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${item.timeDisplay}: ${item.temperature.roundToInt()}°C (${item.condition})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight.dp)
                    .pointerInput(displayItems) {
                        detectTapGestures { offset ->
                            val stepX = size.width / (displayItems.size - 1).coerceAtLeast(1)
                            val tappedIdx = ((offset.x + stepX / 2f) / stepX)
                                .toInt()
                                .coerceIn(0, displayItems.size - 1)
                            selectedIndex = tappedIdx
                        }
                    }
            ) {
                val width = size.width
                val height = size.height
                val bottomPadding = 32.dp.toPx()
                val topPadding = 24.dp.toPx()
                val usableHeight = height - bottomPadding - topPadding

                val stepX = width / (displayItems.size - 1).coerceAtLeast(1)

                val points = displayItems.mapIndexed { index, item ->
                    val normY = ((item.temperature - minTemp) / tempRange).toFloat()
                    val x = index * stepX
                    val y = height - bottomPadding - (normY * usableHeight)
                    Offset(x, y)
                }

                // Fill gradient path
                val fillPath = Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points.first().x, height - bottomPadding)
                        lineTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) {
                            val prev = points[i - 1]
                            val curr = points[i]
                            val midX = (prev.x + curr.x) / 2f
                            cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                        }
                        lineTo(points.last().x, height - bottomPadding)
                        close()
                    }
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.35f),
                            primaryColor.copy(alpha = 0.0f)
                        ),
                        startY = topPadding,
                        endY = height - bottomPadding
                    )
                )

                // Stroke line
                val strokePath = Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) {
                            val prev = points[i - 1]
                            val curr = points[i]
                            val midX = (prev.x + curr.x) / 2f
                            cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                        }
                    }
                }

                drawPath(
                    path = strokePath,
                    brush = Brush.horizontalGradient(listOf(primaryColor, secondaryColor)),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw point markers
                points.forEachIndexed { index, pt ->
                    val isSelected = selectedIndex == index
                    val radius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx()
                    val pointColor = if (isSelected) Color.White else primaryColor

                    drawCircle(
                        color = Color(0xFF0F172A),
                        radius = radius + 2.dp.toPx(),
                        center = pt
                    )
                    drawCircle(
                        color = pointColor,
                        radius = radius,
                        center = pt
                    )

                    // Draw vertical guide line if selected
                    if (isSelected) {
                        drawLine(
                            color = primaryColor.copy(alpha = 0.5f),
                            start = Offset(pt.x, topPadding),
                            end = Offset(pt.x, height - bottomPadding),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }
                }
            }

            // Time labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                displayItems.filterIndexed { idx, _ -> idx % 2 == 0 || idx == displayItems.lastIndex }
                    .forEach { item ->
                        Text(
                            text = item.timeDisplay,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
            }
        }
    }
}

@Composable
fun RainProbabilityChart(
    hourlyItems: List<HourlyForecastItem>,
    modifier: Modifier = Modifier,
    chartHeight: Int = 160
) {
    if (hourlyItems.isEmpty()) return
    val displayItems = remember(hourlyItems) { hourlyItems.take(12) }

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RAIN PROBABILITY TREND",
                        style = MaterialTheme.typography.labelSmall,
                        color = RainBlue,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Hourly Precipitation Likelihood (%)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val maxProb = displayItems.maxOfOrNull { it.rainProbability } ?: 0
                Text(
                    text = "Peak: $maxProb%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = RainBlue
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight.dp)
            ) {
                val width = size.width
                val height = size.height
                val barCount = displayItems.size
                val barSpacing = 8.dp.toPx()
                val totalSpacing = barSpacing * (barCount - 1)
                val barWidth = ((width - totalSpacing) / barCount).coerceAtLeast(6.dp.toPx())

                displayItems.forEachIndexed { index, item ->
                    val prob = item.rainProbability.coerceIn(0, 100)
                    val barHeight = ((prob / 100f) * (height - 20.dp.toPx())).coerceAtLeast(4.dp.toPx())
                    val x = index * (barWidth + barSpacing)
                    val y = height - barHeight

                    // Bar background slot
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.05f),
                        topLeft = Offset(x, 0f),
                        size = Size(barWidth, height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                    )

                    // Active bar
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            listOf(
                                RainBlue,
                                RainBlue.copy(alpha = 0.4f)
                            )
                        ),
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Time labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                displayItems.filterIndexed { idx, _ -> idx % 2 == 0 || idx == displayItems.lastIndex }
                    .forEach { item ->
                        Text(
                            text = item.timeDisplay,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
            }
        }
    }
}
