package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CityLocation
import com.example.data.model.MlModelType
import com.example.ui.components.GlassCard
import com.example.ui.theme.RainBlue
import com.example.ui.theme.SkyPrimaryCyan
import com.example.ui.theme.SkyTertiaryGold
import com.example.ui.viewmodel.MlSandboxState
import kotlin.math.roundToInt

@Composable
fun AiForecastScreen(
    currentCity: CityLocation,
    mlState: MlSandboxState,
    onModelSelected: (MlModelType) -> Unit,
    onTempChanged: (Double) -> Unit,
    onHumidityChanged: (Int) -> Unit,
    onPressureChanged: (Double) -> Unit,
    onWindChanged: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val prediction = mlState.predictionResult

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_forecast_screen_content"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = SkyPrimaryCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI FORECAST & ML LAB",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = SkyPrimaryCyan,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Predictive Modeling & Statistical Forecasting",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Using multi-variable meteorological datasets to predict temperature trajectory and rainfall probability.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Mandatory Accuracy & Source Distinction Notice
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = Color(0xFFFBBF24),
                backgroundColor = Color(0xFFFBBF24).copy(alpha = 0.08f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DATA SOURCE DISTINCTION (TRANSPARENCY)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFBBF24),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "• Real-Time Data: Real physical sensors from Open-Meteo.\n" +
                                    "• Official Forecast: Numerical Weather Prediction (NWP) synoptic models.\n" +
                                    "• Machine Learning: University research predictive simulation exploring Linear Regression, Random Forest, XGBoost, and LSTM temporal memory.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Model Selector
        item {
            Column {
                Text(
                    text = "SELECT PREDICTIVE ARCHITECTURE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MlModelType.entries.forEach { model ->
                        val isSelected = mlState.selectedModel == model
                        GlassCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onModelSelected(model) },
                            borderColor = if (isSelected) SkyPrimaryCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                            backgroundColor = if (isSelected) SkyPrimaryCyan.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            contentPadding = 8.dp
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = model.modelName.split(" ").first(),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    color = if (isSelected) SkyPrimaryCyan else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "R² ${(model.r2Score * 100).toInt()}%",
                                    fontSize = 10.sp,
                                    color = if (isSelected) SkyPrimaryCyan else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live ML Prediction Output Card
        if (prediction != null) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = SkyPrimaryCyan.copy(alpha = 0.6f),
                    backgroundColor = SkyPrimaryCyan.copy(alpha = 0.1f),
                    contentPadding = 18.dp
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = SkyPrimaryCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${prediction.modelType.modelName.uppercase()} INFERENCE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = SkyPrimaryCyan,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SkyPrimaryCyan.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${prediction.confidencePercent}% Confidence",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SkyPrimaryCyan
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "PREDICTED TEMP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${prediction.predictedTemp}°C",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                val deltaStr = if (prediction.baselineDelta >= 0) "+${prediction.baselineDelta}°C" else "${prediction.baselineDelta}°C"
                                Text(
                                    text = "($deltaStr 24h shift)",
                                    fontSize = 10.sp,
                                    color = if (prediction.baselineDelta >= 0) SkyTertiaryGold else SkyPrimaryCyan
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(55.dp)
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "PREDICTED RAIN",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${prediction.predictedRainProb}%",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = RainBlue
                                )
                                Text(
                                    text = if (prediction.predictedRainProb > 50) "High likelihood" else "Low likelihood",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = prediction.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Interactive Sandbox Sliders
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INTERACTIVE INPUT PARAMETERS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Live Tweak",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Temperature Slider
                    SliderRow(
                        label = "Current Temp: ${mlState.inputTemp.roundToInt()}°C",
                        value = mlState.inputTemp.toFloat(),
                        valueRange = 0f..50f,
                        onValueChange = { onTempChanged(it.toDouble()) }
                    )

                    // Humidity Slider
                    SliderRow(
                        label = "Relative Humidity: ${mlState.inputHumidity}%",
                        value = mlState.inputHumidity.toFloat(),
                        valueRange = 10f..100f,
                        onValueChange = { onHumidityChanged(it.toInt()) }
                    )

                    // Barometric Pressure Slider
                    SliderRow(
                        label = "Atmospheric Pressure: ${mlState.inputPressure.roundToInt()} hPa",
                        value = mlState.inputPressure.toFloat(),
                        valueRange = 980f..1040f,
                        onValueChange = { onPressureChanged(it.toDouble()) }
                    )

                    // Wind Speed Slider
                    SliderRow(
                        label = "Wind Speed: ${mlState.inputWindSpeed.roundToInt()} km/h",
                        value = mlState.inputWindSpeed.toFloat(),
                        valueRange = 0f..60f,
                        onValueChange = { onWindChanged(it.toDouble()) }
                    )
                }
            }
        }

        // Feature Importance Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "FEATURE IMPORTANCE (SHAP / GINI WEIGHTS)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    mlState.predictionResult?.featureImportances?.forEach { feat ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = feat.featureName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${feat.weightPercentage}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SkyPrimaryCyan
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(feat.weightPercentage / 100f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(SkyPrimaryCyan)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Model Benchmark Metrics
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "ACADEMIC BENCHMARK VALIDATION",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cross-validated against 10-fold split on multi-year meteorological stations. XGBoost & LSTM achieve minimal Mean Absolute Error (MAE < 1.15°C).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MlMetricPill("XGBoost R²", "0.94", SkyPrimaryCyan)
                        MlMetricPill("LSTM MAE", "0.98°C", Color(0xFF34D399))
                        MlMetricPill("RF Trees", "100", SkyTertiaryGold)
                    }
                }
            }
        }
    }
}

@Composable
fun SliderRow(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = SkyPrimaryCyan,
                activeTrackColor = SkyPrimaryCyan,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
fun MlMetricPill(
    label: String,
    value: String,
    accentColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = accentColor)
    }
}
