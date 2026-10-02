package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CityLocation
import com.example.data.model.CurrentWeather
import com.example.data.model.FullWeatherData
import com.example.data.model.HourlyForecastItem
import com.example.data.model.RiskLevel
import com.example.data.model.WeatherCodeHelper
import com.example.ui.components.AlertCard
import com.example.ui.components.GlassCard
import com.example.ui.components.RiskScoreBadge
import com.example.ui.components.RiskScoreGaugeCard
import com.example.ui.components.SearchBarWithSuggestions
import com.example.ui.components.WeatherIconView
import com.example.ui.theme.RainBlue
import com.example.ui.theme.SkyPrimaryCyan
import com.example.ui.theme.SkyTertiaryGold
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    weatherData: FullWeatherData?,
    isLoading: Boolean,
    errorMessage: String?,
    currentCity: CityLocation,
    searchResults: List<CityLocation>,
    onCitySelected: (CityLocation) -> Unit,
    onUseCurrentLocation: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onRefresh: () -> Unit,
    onNavigateToForecast: () -> Unit,
    onNavigateToHourly: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToAiForecast: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header with app brand & refresh
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SkyCast AI",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "SMART",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = weatherData?.current?.timeFormatted ?: "Updating forecast...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.testTag("refresh_weather_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 2. Search & Pakistani City Chips
        item {
            SearchBarWithSuggestions(
                currentCity = currentCity,
                onCitySelected = onCitySelected,
                onUseCurrentLocation = onUseCurrentLocation,
                searchResults = searchResults,
                onQueryChanged = onSearchQueryChanged
            )
        }

        // Loading or Error State
        if (isLoading && weatherData == null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Analyzing synoptic weather observations for ${currentCity.name}...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else if (errorMessage != null && weatherData == null) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = MaterialTheme.colorScheme.error
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Network Error",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FilledTonalButton(onClick = onRefresh) {
                            Text("Retry Connection")
                        }
                    }
                }
            }
        } else if (weatherData != null) {
            val current = weatherData.current
            val todayDaily = weatherData.daily.firstOrNull()

            // 3. Hero Weather Card
            item {
                HeroWeatherCard(
                    city = weatherData.city,
                    current = current,
                    todayMax = todayDaily?.tempMax ?: current.temperature,
                    todayMin = todayDaily?.tempMin ?: current.temperature,
                    riskLevel = weatherData.riskAssessment.riskLevel,
                    riskScore = weatherData.riskAssessment.overallScore,
                    onRiskClick = onNavigateToAlerts
                )
            }

            // 4. Smart Alerts Banner (if any)
            if (weatherData.alerts.isNotEmpty()) {
                item {
                    val topAlert = weatherData.alerts.first()
                    AlertCard(alert = topAlert)
                }
            }

            // 5. Weather Risk Score Card
            item {
                RiskScoreGaugeCard(
                    assessment = weatherData.riskAssessment,
                    onViewDetailsClick = onNavigateToAlerts
                )
            }

            // 6. AI Natural Language Weather Insights Card
            item {
                AiInsightCard(
                    insight = weatherData.aiInsight,
                    onOpenMlLab = onNavigateToAiForecast
                )
            }

            // 7. Atmospheric Metrics Grid
            item {
                AtmosphericMetricsGrid(current = current)
            }

            // 8. 24-Hour Hourly Forecast Section
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HOURLY FORECAST (24H)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = onNavigateToHourly)
                        ) {
                            Text(
                                text = "View All",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(weatherData.hourly) { hour ->
                            HourlyQuickItemCard(item = hour)
                        }
                    }
                }
            }

            // 9. 7-Day Forecast Preview
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "7-DAY OUTLOOK",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = onNavigateToForecast)
                        ) {
                            Text(
                                text = "Full Forecast",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        weatherData.daily.take(4).forEach { day ->
                            DailyQuickRow(day = day)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeroWeatherCard(
    city: CityLocation,
    current: CurrentWeather,
    todayMax: Double,
    todayMin: Double,
    riskLevel: RiskLevel,
    riskScore: Int,
    onRiskClick: () -> Unit
) {
    val tempInt = current.temperature.roundToInt()
    val feelsInt = current.feelsLike.roundToInt()

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_weather_card"),
        contentPadding = 20.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = city.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (city.admin1.isNotEmpty()) "${city.admin1}, ${city.country}" else city.country,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                RiskScoreBadge(
                    riskLevel = riskLevel,
                    score = riskScore,
                    modifier = Modifier.clickable(onClick = onRiskClick)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "$tempInt",
                            fontSize = 68.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 70.sp
                        )
                        Text(
                            text = "°C",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Text(
                        text = current.condition,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = WeatherCodeHelper.getConditionColor(current.weatherCode)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Feels like $feelsInt°C • H: ${todayMax.roundToInt()}° L: ${todayMin.roundToInt()}°",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                WeatherIconView(
                    weatherCode = current.weatherCode,
                    size = 96.dp
                )
            }
        }
    }
}

@Composable
fun AiInsightCard(
    insight: com.example.data.model.AiInsight,
    onOpenMlLab: () -> Unit
) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = listOf("General", "Agriculture", "Travelers", "Students")

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = SkyPrimaryCyan.copy(alpha = 0.5f),
        backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = SkyPrimaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI WEATHER INSIGHTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = SkyPrimaryCyan,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "${insight.confidenceScore}% Conf.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = insight.headline,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = insight.naturalLanguageSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEachIndexed { index, title ->
                    val isSelected = selectedCategoryIndex == index
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryIndex = index },
                        label = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SkyPrimaryCyan,
                            selectedLabelColor = Color(0xFF003544),
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val categoryText = when (selectedCategoryIndex) {
                0 -> insight.generalAdvice
                1 -> "🌾 Farmer Guidance: ${insight.farmingNote}"
                2 -> "🚗 Traveler Notice: ${insight.travelerNote}"
                3 -> "🎒 Student & Commuter: ${insight.studentCommuterNote}"
                else -> insight.generalAdvice
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(12.dp)
            ) {
                Text(
                    text = categoryText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenMlLab),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Source: ${insight.source} • Open ML Lab",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = SkyPrimaryCyan
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = SkyPrimaryCyan,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun AtmosphericMetricsGrid(current: CurrentWeather) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricTile(
                title = "HUMIDITY",
                value = "${current.humidity}%",
                subtitle = if (current.humidity > 70) "Humid / Muggy" else "Comfortable",
                icon = Icons.Default.WaterDrop,
                iconColor = RainBlue,
                modifier = Modifier.weight(1f)
            )
            MetricTile(
                title = "WIND SPEED",
                value = "${current.windSpeed.roundToInt()} km/h",
                subtitle = "Dir: ${current.windDirection}°",
                icon = Icons.Default.Air,
                iconColor = SkyPrimaryCyan,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricTile(
                title = "PRESSURE",
                value = "${current.pressure.roundToInt()} hPa",
                subtitle = if (current.pressure < 1010) "Low (Stormy)" else "Stable Barometer",
                icon = Icons.Default.Compress,
                iconColor = Color(0xFFA78BFA),
                modifier = Modifier.weight(1f)
            )
            MetricTile(
                title = "VISIBILITY",
                value = "${current.visibilityKm} km",
                subtitle = if (current.visibilityKm >= 10.0) "Clear Sightlines" else "Fog / Mist",
                icon = Icons.Default.RemoveRedEye,
                iconColor = Color(0xFF34D399),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricTile(
                title = "UV INDEX",
                value = String.format("%.1f", current.uvIndex),
                subtitle = if (current.uvIndex >= 8) "Very High (Protect)" else if (current.uvIndex >= 6) "High" else "Moderate",
                icon = Icons.Default.WbSunny,
                iconColor = SkyTertiaryGold,
                modifier = Modifier.weight(1f)
            )
            MetricTile(
                title = "SUN CYCLE",
                value = current.sunrise,
                subtitle = "Sunset: ${current.sunset}",
                icon = Icons.Default.WbTwilight,
                iconColor = Color(0xFFF97316),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricTile(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        contentPadding = 12.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun HourlyQuickItemCard(item: HourlyForecastItem) {
    GlassCard(
        contentPadding = 12.dp,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.width(82.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = item.timeDisplay,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            WeatherIconView(
                weatherCode = item.weatherCode,
                size = 36.dp,
                animate = false
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${item.temperature.roundToInt()}°",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (item.rainProbability > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = RainBlue,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = "${item.rainProbability}%",
                        fontSize = 10.sp,
                        color = RainBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DailyQuickRow(day: com.example.data.model.DailyForecastItem) {
    GlassCard(
        contentPadding = 12.dp,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(110.dp)
            ) {
                WeatherIconView(weatherCode = day.weatherCode, size = 32.dp, animate = false)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = day.dayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = day.dateDisplay,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (day.rainProbability > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = RainBlue,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${day.rainProbability}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = RainBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(40.dp))
            }

            // Min-Max Temperature bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "${day.tempMin.roundToInt()}°",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    SkyTertiaryGold
                                )
                            )
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${day.tempMax.roundToInt()}°",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
