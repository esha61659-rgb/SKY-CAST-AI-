package com.example.ui.navigation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.FullWeatherData
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AiForecastScreen
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.ComparisonScreen
import com.example.ui.screens.ForecastScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HourlyScreen
import com.example.ui.screens.TrendsScreen
import com.example.ui.theme.RiskHighOrange
import com.example.ui.theme.SkyPrimaryCyan
import com.example.ui.viewmodel.WeatherUiState
import com.example.ui.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: WeatherViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    val uiState by viewModel.uiState.collectAsState()
    val currentCity by viewModel.currentCity.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val mlState by viewModel.mlSandboxState.collectAsState()
    val comparisonState by viewModel.comparisonState.collectAsState()

    // Handle back button on sub-screens to return to Home
    BackHandler(enabled = currentScreen != Screen.HOME) {
        currentScreen = Screen.HOME
    }

    // Permission launcher for Location detection
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.detectCurrentLocation {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Unable to fetch GPS fix. Please ensure location services are enabled.")
                }
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Location permission denied. You can still search for any city.")
            }
        }
    }

    val requestLocation = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.detectCurrentLocation {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Unable to fetch GPS fix. Please ensure location services are enabled.")
                }
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val weatherData: FullWeatherData? = when (uiState) {
        is WeatherUiState.Success -> (uiState as WeatherUiState.Success).data
        else -> null
    }

    val isLoading = uiState is WeatherUiState.Loading
    val errorMessage = (uiState as? WeatherUiState.Error)?.message

    val alertCount = weatherData?.alerts?.size ?: 0

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // Secondary page quick navigation tabs at the top for complete 8-page access
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Screen.entries.forEach { screen ->
                        val isSelected = currentScreen == screen
                        FilterChip(
                            selected = isSelected,
                            onClick = { currentScreen = screen },
                            leadingIcon = {
                                if (screen == Screen.ALERTS && alertCount > 0) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = RiskHighOrange,
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = if (screen == Screen.ALERTS && alertCount > 0) "${screen.title} ($alertCount)" else screen.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SkyPrimaryCyan,
                                selectedLabelColor = Color(0xFF003544),
                                selectedLeadingIconColor = Color(0xFF003544),
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            border = null,
                            modifier = Modifier.testTag("top_chip_${screen.name.lowercase()}")
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Primary Bottom Navigation Bar with active indicator pills
            val primaryScreens = listOf(
                Screen.HOME,
                Screen.FORECAST,
                Screen.HOURLY,
                Screen.AI_FORECAST,
                Screen.TRENDS
            )

            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 8.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("main_bottom_nav")
            ) {
                primaryScreens.forEach { screen ->
                    val isSelected = currentScreen == screen

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF003544),
                            selectedTextColor = SkyPrimaryCyan,
                            indicatorColor = SkyPrimaryCyan,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag(screen.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    Screen.HOME -> HomeScreen(
                        weatherData = weatherData,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        currentCity = currentCity,
                        searchResults = searchResults,
                        onCitySelected = { viewModel.loadWeatherForCity(it) },
                        onUseCurrentLocation = requestLocation,
                        onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                        onRefresh = { viewModel.refreshCurrentWeather() },
                        onNavigateToForecast = { currentScreen = Screen.FORECAST },
                        onNavigateToHourly = { currentScreen = Screen.HOURLY },
                        onNavigateToAlerts = { currentScreen = Screen.ALERTS },
                        onNavigateToAiForecast = { currentScreen = Screen.AI_FORECAST }
                    )

                    Screen.FORECAST -> ForecastScreen(
                        weatherData = weatherData,
                        currentCity = currentCity
                    )

                    Screen.HOURLY -> HourlyScreen(
                        weatherData = weatherData,
                        currentCity = currentCity
                    )

                    Screen.TRENDS -> TrendsScreen(
                        weatherData = weatherData,
                        currentCity = currentCity
                    )

                    Screen.AI_FORECAST -> AiForecastScreen(
                        currentCity = currentCity,
                        mlState = mlState,
                        onModelSelected = { viewModel.updateMlModel(it) },
                        onTempChanged = { viewModel.updateMlInputTemp(it) },
                        onHumidityChanged = { viewModel.updateMlInputHumidity(it) },
                        onPressureChanged = { viewModel.updateMlInputPressure(it) },
                        onWindChanged = { viewModel.updateMlInputWindSpeed(it) }
                    )

                    Screen.ALERTS -> AlertsScreen(
                        weatherData = weatherData,
                        currentCity = currentCity
                    )

                    Screen.COMPARISON -> ComparisonScreen(
                        comparisonState = comparisonState,
                        onSwapCities = {
                            viewModel.setComparisonCities(
                                comparisonState.city2,
                                comparisonState.city1
                            )
                        },
                        onSelectCity1 = {
                            viewModel.setComparisonCities(it, comparisonState.city2)
                        },
                        onSelectCity2 = {
                            viewModel.setComparisonCities(comparisonState.city1, it)
                        }
                    )

                    Screen.ABOUT -> AboutScreen()
                }
            }
        }
    }
}
