package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CityLocation
import com.example.data.model.FullWeatherData
import com.example.ui.components.GlassCard
import com.example.ui.components.RainProbabilityChart
import com.example.ui.components.TemperatureTrendChart
import com.example.ui.theme.RainBlue
import com.example.ui.theme.SkyPrimaryCyan
import com.example.ui.theme.SkyTertiaryGold
import kotlin.math.roundToInt

@Composable
fun TrendsScreen(
    weatherData: FullWeatherData?,
    currentCity: CityLocation,
    modifier: Modifier = Modifier
) {
    if (weatherData == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No trend data available",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val hourly = weatherData.hourly

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("trends_screen_content"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "WEATHER TRENDS & ANALYTICS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Visual Atmospheric Trajectory for ${currentCity.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Tap on chart curves to inspect granular values along the 24-hour cycle.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Summary Statistics Card
        item {
            val maxT = hourly.maxOfOrNull { it.temperature } ?: 0.0
            val minT = hourly.minOfOrNull { it.temperature } ?: 0.0
            val maxRain = hourly.maxOfOrNull { it.rainProbability } ?: 0
            val maxWind = hourly.maxOfOrNull { it.windSpeed } ?: 0.0

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TrendSummaryStat(
                        label = "Max Temp",
                        value = "${maxT.roundToInt()}°C",
                        color = SkyTertiaryGold
                    )
                    TrendSummaryStat(
                        label = "Min Temp",
                        value = "${minT.roundToInt()}°C",
                        color = SkyPrimaryCyan
                    )
                    TrendSummaryStat(
                        label = "Peak Rain",
                        value = "$maxRain%",
                        color = RainBlue
                    )
                    TrendSummaryStat(
                        label = "Peak Wind",
                        value = "${maxWind.roundToInt()} km/h",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Chart 1: Temperature Trend Spline
        item {
            TemperatureTrendChart(
                hourlyItems = hourly,
                chartHeight = 190
            )
        }

        // Chart 2: Rain Probability Bar Chart
        item {
            RainProbabilityChart(
                hourlyItems = hourly,
                chartHeight = 170
            )
        }

        // Chart 3: Wind Speed Trajectory Breakdown
        item {
            WindTrendCard(hourly = hourly)
        }
    }
}

@Composable
fun TrendSummaryStat(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun WindTrendCard(hourly: List<com.example.data.model.HourlyForecastItem>) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(
                text = "WIND SPEED PROGRESSION (KM/H)",
                style = MaterialTheme.typography.labelSmall,
                color = SkyPrimaryCyan,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                hourly.take(6).forEach { item ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = item.timeDisplay,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${item.windSpeed.roundToInt()}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "km/h",
                            fontSize = 8.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
