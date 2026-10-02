package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CityLocation
import com.example.data.remote.WeatherApiService
import com.example.ui.components.GlassCard
import com.example.ui.components.WeatherIconView
import com.example.ui.theme.RainBlue
import com.example.ui.theme.SkyPrimaryCyan
import com.example.ui.theme.SkyTertiaryGold
import com.example.ui.viewmodel.ComparisonUiState
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun ComparisonScreen(
    comparisonState: ComparisonUiState,
    onSwapCities: () -> Unit,
    onSelectCity1: (CityLocation) -> Unit,
    onSelectCity2: (CityLocation) -> Unit,
    modifier: Modifier = Modifier
) {
    val result = comparisonState.result

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("comparison_screen_content"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CITY WEATHER COMPARISON",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${comparisonState.city1.name} vs ${comparisonState.city2.name}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Compare temperature divergence, rain likelihood, humidity, and microclimates.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // City Selector Card with Swap Button
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CITY 1",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = SkyPrimaryCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = comparisonState.city1.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = onSwapCities,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Swap Cities",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "CITY 2",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = SkyTertiaryGold,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = comparisonState.city2.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Change City 1:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        WeatherApiService.PAKISTAN_CITIES.forEach { c ->
                            FilterChip(
                                selected = comparisonState.city1.name == c.name,
                                onClick = { onSelectCity1(c) },
                                label = { Text(c.name, fontSize = 11.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Change City 2:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        WeatherApiService.PAKISTAN_CITIES.forEach { c ->
                            FilterChip(
                                selected = comparisonState.city2.name == c.name,
                                onClick = { onSelectCity2(c) },
                                label = { Text(c.name, fontSize = 11.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Comparison Result Body
        if (comparisonState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        } else if (result != null) {
            // Summary Insight Pill
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = SkyPrimaryCyan.copy(alpha = 0.5f),
                    backgroundColor = SkyPrimaryCyan.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "💡 ${result.summaryInsight}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Side-by-Side Comparison
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // City 1 Head
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                WeatherIconView(
                                    weatherCode = result.weather1.weatherCode,
                                    size = 54.dp,
                                    animate = false
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = result.city1.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${result.weather1.temperature.roundToInt()}°C",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = SkyPrimaryCyan
                                )
                                Text(
                                    text = result.weather1.condition,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(100.dp)
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            )

                            // City 2 Head
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                WeatherIconView(
                                    weatherCode = result.weather2.weatherCode,
                                    size = 54.dp,
                                    animate = false
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = result.city2.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${result.weather2.temperature.roundToInt()}°C",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = SkyTertiaryGold
                                )
                                Text(
                                    text = result.weather2.condition,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Metric Rows
                        ComparisonMetricRow(
                            label = "Feels Like",
                            val1 = "${result.weather1.feelsLike.roundToInt()}°C",
                            val2 = "${result.weather2.feelsLike.roundToInt()}°C"
                        )

                        ComparisonMetricRow(
                            label = "Humidity",
                            val1 = "${result.weather1.humidity}%",
                            val2 = "${result.weather2.humidity}%"
                        )

                        ComparisonMetricRow(
                            label = "Wind Speed",
                            val1 = "${result.weather1.windSpeed.roundToInt()} km/h",
                            val2 = "${result.weather2.windSpeed.roundToInt()} km/h"
                        )

                        ComparisonMetricRow(
                            label = "Pressure",
                            val1 = "${result.weather1.pressure.roundToInt()} hPa",
                            val2 = "${result.weather2.pressure.roundToInt()} hPa"
                        )

                        ComparisonMetricRow(
                            label = "UV Index",
                            val1 = String.format("%.1f", result.weather1.uvIndex),
                            val2 = String.format("%.1f", result.weather2.uvIndex)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComparisonMetricRow(
    label: String,
    val1: String,
    val2: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = val1,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = SkyPrimaryCyan,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Text(
            text = val2,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = SkyTertiaryGold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}
