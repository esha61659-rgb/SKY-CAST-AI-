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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
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
import com.example.data.model.HourlyForecastItem
import com.example.ui.components.GlassCard
import com.example.ui.components.TemperatureTrendChart
import com.example.ui.components.WeatherIconView
import com.example.ui.theme.RainBlue
import com.example.ui.theme.SkyPrimaryCyan
import kotlin.math.roundToInt

@Composable
fun HourlyScreen(
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
                text = "No hourly forecast available",
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
            .testTag("hourly_screen_content"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "HOURLY WEATHER FORECAST",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Next 24 Hours in ${currentCity.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Diurnal trajectory of temperature, rain probability, wind, and humidity.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            TemperatureTrendChart(hourlyItems = hourly)
        }

        itemsIndexed(hourly) { index, hour ->
            HourlyDetailCard(item = hour, isFirst = index == 0)
        }
    }
}

@Composable
fun HourlyDetailCard(
    item: HourlyForecastItem,
    isFirst: Boolean
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = 14.dp,
        shape = RoundedCornerShape(16.dp),
        borderColor = if (isFirst) SkyPrimaryCyan.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.width(62.dp)) {
                    Text(
                        text = item.timeDisplay,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (isFirst) FontWeight.Black else FontWeight.Bold,
                        color = if (isFirst) SkyPrimaryCyan else MaterialTheme.colorScheme.onSurface
                    )
                    if (isFirst) {
                        Text(
                            text = "Current",
                            fontSize = 10.sp,
                            color = SkyPrimaryCyan
                        )
                    }
                }

                WeatherIconView(
                    weatherCode = item.weatherCode,
                    size = 38.dp,
                    animate = false
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "${item.temperature.roundToInt()}°C",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.condition,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Rain probability
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = RainBlue,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${item.rainProbability}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RainBlue
                        )
                    }
                    Text(
                        text = "Rain prob",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Wind
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Air,
                            contentDescription = null,
                            tint = SkyPrimaryCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${item.windSpeed.roundToInt()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "km/h wind",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
