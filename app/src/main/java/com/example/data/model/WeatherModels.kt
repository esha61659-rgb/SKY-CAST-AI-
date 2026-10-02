package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Core domain models for SkyCast AI
 */

enum class SeverityLevel {
    LOW,
    MODERATE,
    HIGH,
    SEVERE
}

enum class RiskLevel(val label: String, val emoji: String) {
    LOW("LOW", "🟢"),
    MODERATE("MODERATE", "🟡"),
    HIGH("HIGH", "🟠"),
    SEVERE("SEVERE", "🔴")
}

data class CityLocation(
    val name: String,
    val country: String,
    val admin1: String = "",
    val latitude: Double,
    val longitude: Double,
    val isFavorite: Boolean = false
) {
    val displayName: String
        get() = if (admin1.isNotEmpty() && admin1 != name) "$name, $admin1, $country" else "$name, $country"
}

data class CurrentWeather(
    val temperature: Double,
    val feelsLike: Double,
    val weatherCode: Int,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double, // km/h
    val windDirection: Int, // degrees
    val pressure: Double, // hPa
    val visibilityKm: Double,
    val uvIndex: Double,
    val isDay: Boolean,
    val sunrise: String,
    val sunset: String,
    val timeFormatted: String
)

data class HourlyForecastItem(
    val timeIso: String,
    val timeDisplay: String,
    val temperature: Double,
    val weatherCode: Int,
    val condition: String,
    val rainProbability: Int, // %
    val windSpeed: Double, // km/h
    val humidity: Int
)

data class DailyForecastItem(
    val dateIso: String,
    val dayName: String,
    val dateDisplay: String,
    val tempMax: Double,
    val tempMin: Double,
    val weatherCode: Int,
    val condition: String,
    val rainProbability: Int, // %
    val windSpeedMax: Double, // km/h
    val humidityAvg: Int,
    val uvIndexMax: Double,
    val sunrise: String,
    val sunset: String
)

data class WeatherAlert(
    val id: String,
    val title: String,
    val description: String,
    val severity: SeverityLevel,
    val category: String, // e.g. "Extreme Heat", "Heavy Rain", "Storm", "High Wind", "UV Radiation", "Low Visibility"
    val advice: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class RiskFactor(
    val name: String,
    val value: String,
    val riskContribution: Int, // 0 - 100
    val status: String
)

data class WeatherRiskAssessment(
    val overallScore: Int, // 0 - 100
    val riskLevel: RiskLevel,
    val primaryThreat: String,
    val summary: String,
    val factors: List<RiskFactor>
)

data class AiInsight(
    val headline: String,
    val naturalLanguageSummary: String,
    val generalAdvice: String,
    val farmingNote: String,
    val travelerNote: String,
    val studentCommuterNote: String,
    val confidenceScore: Int = 92,
    val source: String = "SkyCast Hybrid AI Engine"
)

data class FullWeatherData(
    val city: CityLocation,
    val current: CurrentWeather,
    val hourly: List<HourlyForecastItem>,
    val daily: List<DailyForecastItem>,
    val alerts: List<WeatherAlert>,
    val riskAssessment: WeatherRiskAssessment,
    val aiInsight: AiInsight,
    val fetchedAtMillis: Long = System.currentTimeMillis()
)

data class CityComparisonResult(
    val city1: CityLocation,
    val weather1: CurrentWeather,
    val city2: CityLocation,
    val weather2: CurrentWeather,
    val tempDifference: Double, // city1 - city2
    val humidityDifference: Int,
    val windDifference: Double,
    val warmerCity: String,
    val rainierCity: String,
    val summaryInsight: String
)

enum class MlModelType(val modelName: String, val shortDesc: String, val r2Score: Double, val mae: Double) {
    LINEAR_REGRESSION("Linear Regression", "Multi-feature linear baseline model", 0.81, 1.82),
    RANDOM_FOREST("Random Forest", "Ensemble of 100 decision trees", 0.91, 1.25),
    XGBOOST("XGBoost", "Gradient boosted decision trees", 0.94, 1.10),
    LSTM("LSTM Recurrent Network", "Deep sequence model with temporal memory", 0.95, 0.98)
}

data class MlPredictionResult(
    val modelType: MlModelType,
    val predictedTemp: Double,
    val predictedRainProb: Int,
    val confidencePercent: Int,
    val explanation: String,
    val featureImportances: List<FeatureImportance>,
    val baselineDelta: Double
)

data class FeatureImportance(
    val featureName: String,
    val weightPercentage: Int
)
