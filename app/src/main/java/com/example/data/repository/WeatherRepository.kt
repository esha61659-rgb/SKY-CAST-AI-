package com.example.data.repository

import com.example.data.local.CityDao
import com.example.data.local.SavedCityEntity
import com.example.data.ml.WeatherMlEngine
import com.example.data.model.CityComparisonResult
import com.example.data.model.CityLocation
import com.example.data.model.CurrentWeather
import com.example.data.model.FullWeatherData
import com.example.data.model.MlModelType
import com.example.data.model.MlPredictionResult
import com.example.data.model.WeatherAlertEngine
import com.example.data.remote.GeminiAiService
import com.example.data.remote.WeatherApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.abs
import kotlin.math.roundToInt

class WeatherRepository(
    private val apiService: WeatherApiService,
    private val geminiService: GeminiAiService,
    private val cityDao: CityDao
) {
    val savedCities: Flow<List<CityLocation>> = cityDao.getAllSavedCities().map { entities ->
        entities.map { entity ->
            CityLocation(
                name = entity.cityName,
                country = entity.country,
                admin1 = entity.admin1,
                latitude = entity.latitude,
                longitude = entity.longitude,
                isFavorite = entity.isFavorite
            )
        }
    }

    suspend fun getFullWeatherData(city: CityLocation): FullWeatherData {
        val (current, forecastPair) = apiService.fetchWeather(city.latitude, city.longitude)
        val hourly = forecastPair.first
        val daily = forecastPair.second

        val alerts = WeatherAlertEngine.generateAlerts(city.name, current, daily)
        val riskScore = WeatherAlertEngine.calculateRiskScore(current, daily)
        val aiInsight = geminiService.generateWeatherInsight(city.name, current, daily)

        // Save accessed city into Room
        cityDao.insertCity(
            SavedCityEntity(
                cityName = city.name,
                country = city.country,
                admin1 = city.admin1,
                latitude = city.latitude,
                longitude = city.longitude,
                isFavorite = city.isFavorite,
                lastAccessed = System.currentTimeMillis()
            )
        )

        return FullWeatherData(
            city = city,
            current = current,
            hourly = hourly,
            daily = daily,
            alerts = alerts,
            riskAssessment = riskScore,
            aiInsight = aiInsight
        )
    }

    suspend fun compareCities(city1: CityLocation, city2: CityLocation): CityComparisonResult {
        val weather1Pair = apiService.fetchWeather(city1.latitude, city1.longitude)
        val weather2Pair = apiService.fetchWeather(city2.latitude, city2.longitude)

        val w1 = weather1Pair.first
        val w2 = weather2Pair.first

        val tempDiff = (w1.temperature - w2.temperature * 10.0).roundToInt() / 10.0
        val humidityDiff = w1.humidity - w2.humidity
        val windDiff = (w1.windSpeed - w2.windSpeed * 10.0).roundToInt() / 10.0

        val warmerCity = if (w1.temperature > w2.temperature) city1.name else city2.name
        val rainierProb1 = weather1Pair.second.second.firstOrNull()?.rainProbability ?: 0
        val rainierProb2 = weather2Pair.second.second.firstOrNull()?.rainProbability ?: 0
        val rainierCity = if (rainierProb1 > rainierProb2) city1.name else if (rainierProb2 > rainierProb1) city2.name else "Equal ($rainierProb1%)"

        val summary = when {
            abs(tempDiff) <= 1.5 -> "${city1.name} and ${city2.name} share almost identical thermal profiles today (~${w1.temperature.toInt()}°C)."
            tempDiff > 0 -> "${city1.name} is ${abs(tempDiff)}°C warmer than ${city2.name}."
            else -> "${city2.name} is ${abs(tempDiff)}°C warmer than ${city1.name}."
        }

        return CityComparisonResult(
            city1 = city1,
            weather1 = w1,
            city2 = city2,
            weather2 = w2,
            tempDifference = tempDiff,
            humidityDifference = humidityDiff,
            windDifference = windDiff,
            warmerCity = warmerCity,
            rainierCity = rainierCity,
            summaryInsight = summary
        )
    }

    suspend fun searchCities(query: String): List<CityLocation> {
        return apiService.searchCities(query)
    }

    suspend fun toggleFavorite(city: CityLocation) {
        val newFav = !city.isFavorite
        cityDao.updateFavorite(city.name, newFav)
    }

    fun runMlInference(
        modelType: MlModelType,
        temp: Double,
        humidity: Int,
        pressure: Double,
        windSpeed: Double
    ): MlPredictionResult {
        return WeatherMlEngine.predict(
            modelType = modelType,
            currentTemp = temp,
            humidity = humidity,
            pressure = pressure,
            windSpeed = windSpeed
        )
    }
}
