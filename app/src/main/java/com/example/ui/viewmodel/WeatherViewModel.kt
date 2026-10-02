package com.example.ui.viewmodel

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CityComparisonResult
import com.example.data.model.CityLocation
import com.example.data.model.FullWeatherData
import com.example.data.model.MlModelType
import com.example.data.model.MlPredictionResult
import com.example.data.remote.GeminiAiService
import com.example.data.remote.WeatherApiService
import com.example.data.repository.WeatherRepository
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(val data: FullWeatherData) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

data class MlSandboxState(
    val selectedModel: MlModelType = MlModelType.XGBOOST,
    val inputTemp: Double = 32.0,
    val inputHumidity: Int = 65,
    val inputPressure: Double = 1012.0,
    val inputWindSpeed: Double = 18.0,
    val predictionResult: MlPredictionResult? = null
)

data class ComparisonUiState(
    val city1: CityLocation = WeatherApiService.PAKISTAN_CITIES[0], // Multan
    val city2: CityLocation = WeatherApiService.PAKISTAN_CITIES[1], // Lahore
    val isLoading: Boolean = false,
    val result: CityComparisonResult? = null,
    val error: String? = null
)

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WeatherRepository

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _currentCity = MutableStateFlow(WeatherApiService.PAKISTAN_CITIES[0]) // Default: Multan
    val currentCity: StateFlow<CityLocation> = _currentCity.asStateFlow()

    private val _searchResults = MutableStateFlow<List<CityLocation>>(emptyList())
    val searchResults: StateFlow<List<CityLocation>> = _searchResults.asStateFlow()

    private val _mlSandboxState = MutableStateFlow(MlSandboxState())
    val mlSandboxState: StateFlow<MlSandboxState> = _mlSandboxState.asStateFlow()

    private val _comparisonState = MutableStateFlow(ComparisonUiState())
    val comparisonState: StateFlow<ComparisonUiState> = _comparisonState.asStateFlow()

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    init {
        val db = AppDatabase.getDatabase(application)
        val api = WeatherApiService()
        val gemini = GeminiAiService()
        repository = WeatherRepository(api, gemini, db.cityDao())

        // Load initial city weather
        loadWeatherForCity(_currentCity.value)
        // Run initial ML sandbox inference
        runMlSandboxInference()
        // Load default city comparison (Multan vs Lahore)
        loadCityComparison(_comparisonState.value.city1, _comparisonState.value.city2)
    }

    fun loadWeatherForCity(city: CityLocation) {
        _currentCity.value = city
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val fullData = repository.getFullWeatherData(city)
                _uiState.value = WeatherUiState.Success(fullData)

                // Sync ML sandbox inputs with live current weather
                _mlSandboxState.update { current ->
                    current.copy(
                        inputTemp = fullData.current.temperature,
                        inputHumidity = fullData.current.humidity,
                        inputPressure = fullData.current.pressure,
                        inputWindSpeed = fullData.current.windSpeed
                    )
                }
                runMlSandboxInference()
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Failed to load weather: ${e.message}", e)
                _uiState.value = WeatherUiState.Error(
                    e.message ?: "Failed to connect to weather network. Please check internet connection."
                )
            }
        }
    }

    fun refreshCurrentWeather() {
        loadWeatherForCity(_currentCity.value)
    }

    fun onSearchQueryChanged(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                val results = repository.searchCities(query)
                _searchResults.value = results
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            }
        }
    }

    fun detectCurrentLocation(onPermissionNeeded: () -> Unit) {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val userCity = CityLocation(
                            name = "Current Location",
                            country = "GPS",
                            admin1 = "Nearby",
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                        loadWeatherForCity(userCity)
                    } else {
                        onPermissionNeeded()
                    }
                }
                .addOnFailureListener {
                    onPermissionNeeded()
                }
        } catch (e: SecurityException) {
            onPermissionNeeded()
        }
    }

    fun updateMlModel(model: MlModelType) {
        _mlSandboxState.update { it.copy(selectedModel = model) }
        runMlSandboxInference()
    }

    fun updateMlInputTemp(temp: Double) {
        _mlSandboxState.update { it.copy(inputTemp = temp) }
        runMlSandboxInference()
    }

    fun updateMlInputHumidity(humidity: Int) {
        _mlSandboxState.update { it.copy(inputHumidity = humidity) }
        runMlSandboxInference()
    }

    fun updateMlInputPressure(pressure: Double) {
        _mlSandboxState.update { it.copy(inputPressure = pressure) }
        runMlSandboxInference()
    }

    fun updateMlInputWindSpeed(windSpeed: Double) {
        _mlSandboxState.update { it.copy(inputWindSpeed = windSpeed) }
        runMlSandboxInference()
    }

    private fun runMlSandboxInference() {
        val state = _mlSandboxState.value
        val result = repository.runMlInference(
            modelType = state.selectedModel,
            temp = state.inputTemp,
            humidity = state.inputHumidity,
            pressure = state.inputPressure,
            windSpeed = state.inputWindSpeed
        )
        _mlSandboxState.update { it.copy(predictionResult = result) }
    }

    fun setComparisonCities(city1: CityLocation, city2: CityLocation) {
        _comparisonState.update { it.copy(city1 = city1, city2 = city2) }
        loadCityComparison(city1, city2)
    }

    fun loadCityComparison(city1: CityLocation, city2: CityLocation) {
        viewModelScope.launch {
            _comparisonState.update { it.copy(isLoading = true, error = null) }
            try {
                val result = repository.compareCities(city1, city2)
                _comparisonState.update { it.copy(isLoading = false, result = result) }
            } catch (e: Exception) {
                _comparisonState.update {
                    it.copy(isLoading = false, error = "Failed to compare cities: ${e.message}")
                }
            }
        }
    }
}
