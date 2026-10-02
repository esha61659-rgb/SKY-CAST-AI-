package com.example.data.model

import java.util.UUID

object WeatherAlertEngine {

    fun generateAlerts(
        cityName: String,
        current: CurrentWeather,
        daily: List<DailyForecastItem>
    ): List<WeatherAlert> {
        val alerts = mutableListOf<WeatherAlert>()
        val rainProb = daily.firstOrNull()?.rainProbability ?: 0
        val maxWind = daily.firstOrNull()?.windSpeedMax ?: current.windSpeed
        val uv = current.uvIndex

        // 1. Extreme Heat Alert
        if (current.temperature >= 40.0) {
            alerts.add(
                WeatherAlert(
                    id = UUID.randomUUID().toString(),
                    title = "Extreme Heatwave Warning",
                    description = "Dangerous ambient temperatures of ${current.temperature}°C detected in $cityName. Heat index feels like ${current.feelsLike}°C.",
                    severity = SeverityLevel.SEVERE,
                    category = "Extreme Heat",
                    advice = "Avoid direct sunlight between 11 AM - 4 PM. Drink extra fluids and monitor elderly individuals and pets."
                )
            )
        } else if (current.temperature >= 35.0) {
            alerts.add(
                WeatherAlert(
                    id = UUID.randomUUID().toString(),
                    title = "High Heat Advisory",
                    description = "Elevated daytime temperatures reaching ${current.temperature}°C in $cityName.",
                    severity = SeverityLevel.MODERATE,
                    category = "Extreme Heat",
                    advice = "Stay well hydrated, seek shaded areas, and use sunscreen if outdoors."
                )
            )
        }

        // 2. Heavy Rain Alert
        if (current.weatherCode in listOf(65, 81, 82) || rainProb >= 80) {
            alerts.add(
                WeatherAlert(
                    id = UUID.randomUUID().toString(),
                    title = "Heavy Rain & Downpour Alert",
                    description = "Intense precipitation ($rainProb% probability) with potential for localized urban runoff and street flooding in $cityName.",
                    severity = SeverityLevel.HIGH,
                    category = "Heavy Rain",
                    advice = "Carry high-grade rain gear, drive with caution, and avoid parking near underpasses or waterlogged roadways."
                )
            )
        } else if (rainProb >= 50 || current.weatherCode in listOf(61, 63, 80)) {
            alerts.add(
                WeatherAlert(
                    id = UUID.randomUUID().toString(),
                    title = "Rain Shower Notice",
                    description = "Scattered rain showers expected today with $rainProb% precipitation probability in $cityName.",
                    severity = SeverityLevel.LOW,
                    category = "Heavy Rain",
                    advice = "Carry an umbrella and wear water-resistant footwear."
                )
            )
        }

        // 3. Storm Alert
        if (current.weatherCode in listOf(95, 96, 99)) {
            alerts.add(
                WeatherAlert(
                    id = UUID.randomUUID().toString(),
                    title = "Severe Convective Thunderstorm Alert",
                    description = "Active convective storm cells with lightning strikes and potential hail in $cityName vicinity.",
                    severity = SeverityLevel.SEVERE,
                    category = "Storm",
                    advice = "Seek enclosed indoor shelter immediately. Unplug sensitive electrical appliances and stay clear of tall trees."
                )
            )
        }

        // 4. Strong Wind Alert
        if (maxWind >= 50.0 || current.windSpeed >= 40.0) {
            alerts.add(
                WeatherAlert(
                    id = UUID.randomUUID().toString(),
                    title = "High Gale Wind Warning",
                    description = "Severe horizontal wind gusts measuring ${maxWind.toInt()} km/h detected across $cityName.",
                    severity = SeverityLevel.HIGH,
                    category = "Strong Wind",
                    advice = "Secure loose outdoor furniture, tarpaulins, and lightweight structures. Motorcyclists should exercise extreme caution."
                )
            )
        } else if (current.windSpeed >= 28.0) {
            alerts.add(
                WeatherAlert(
                    id = UUID.randomUUID().toString(),
                    title = "Brisk Wind Advisory",
                    description = "Moderate to fresh breezes up to ${current.windSpeed.toInt()} km/h in $cityName.",
                    severity = SeverityLevel.MODERATE,
                    category = "Strong Wind",
                    advice = "Watch out for dusty gusts and reduced visibility on highways."
                )
            )
        }

        // 5. High UV Alert
        if (uv >= 8.0) {
            alerts.add(
                WeatherAlert(
                    id = UUID.randomUUID().toString(),
                    title = "Very High UV Radiation Alert",
                    description = "Solar ultraviolet index has reached very high levels (${String.format("%.1f", uv)}) in $cityName.",
                    severity = SeverityLevel.HIGH,
                    category = "High UV",
                    advice = "Unprotected skin and eyes can burn rapidly. Wear UV400 sunglasses, a wide-brim hat, and apply SPF 50+ sunscreen."
                )
            )
        } else if (uv >= 6.0) {
            alerts.add(
                WeatherAlert(
                    id = UUID.randomUUID().toString(),
                    title = "Moderate to High UV Index",
                    description = "UV Index is measured at ${String.format("%.1f", uv)} during peak midday hours.",
                    severity = SeverityLevel.MODERATE,
                    category = "High UV",
                    advice = "Seek shade around solar noon and wear protective clothing."
                )
            )
        }

        // 6. Low Visibility Alert
        if (current.visibilityKm <= 2.5 || current.weatherCode in listOf(45, 48)) {
            alerts.add(
                WeatherAlert(
                    id = UUID.randomUUID().toString(),
                    title = "Dense Fog & Low Visibility Alert",
                    description = "Dense atmospheric fog or smog has reduced horizontal visibility to ${current.visibilityKm} km in $cityName.",
                    severity = SeverityLevel.HIGH,
                    category = "Low Visibility",
                    advice = "Turn on low-beam fog lights, double your braking distance on highways, and avoid overtaking in low sightlines."
                )
            )
        }

        return alerts
    }

    fun calculateRiskScore(
        current: CurrentWeather,
        daily: List<DailyForecastItem>
    ): WeatherRiskAssessment {
        var score = 5
        val factors = mutableListOf<RiskFactor>()
        val rainProb = daily.firstOrNull()?.rainProbability ?: 0

        // Factor 1: Temperature Extremes
        var tempScore = 0
        var tempStatus = "Optimal"
        when {
            current.temperature >= 42.0 -> {
                tempScore = 35
                tempStatus = "Extreme Heat (>42°C)"
            }
            current.temperature >= 38.0 -> {
                tempScore = 25
                tempStatus = "Very Hot (>38°C)"
            }
            current.temperature >= 34.0 -> {
                tempScore = 15
                tempStatus = "Hot (>34°C)"
            }
            current.temperature <= 0.0 -> {
                tempScore = 30
                tempStatus = "Freezing Frost (<0°C)"
            }
            current.temperature <= 5.0 -> {
                tempScore = 15
                tempStatus = "Near Freezing"
            }
        }
        score += tempScore
        factors.add(RiskFactor("Thermal Exposure", "${current.temperature.toInt()}°C", tempScore, tempStatus))

        // Factor 2: Precipitation Probability & Intensity
        var rainScore = 0
        var rainStatus = "Minimal"
        when {
            current.weatherCode in listOf(95, 96, 99) -> {
                rainScore = 40
                rainStatus = "Thunderstorm Active"
            }
            rainProb >= 80 || current.weatherCode == 65 -> {
                rainScore = 30
                rainStatus = "Heavy Rain Prob ($rainProb%)"
            }
            rainProb >= 50 -> {
                rainScore = 18
                rainStatus = "Moderate Rain Prob ($rainProb%)"
            }
            rainProb >= 25 -> {
                rainScore = 8
                rainStatus = "Low Rain Prob ($rainProb%)"
            }
        }
        score += rainScore
        factors.add(RiskFactor("Precipitation Risk", "$rainProb%", rainScore, rainStatus))

        // Factor 3: Wind Velocity
        var windScore = 0
        var windStatus = "Calm"
        when {
            current.windSpeed >= 50.0 -> {
                windScore = 25
                windStatus = "Gale Force (>50 km/h)"
            }
            current.windSpeed >= 35.0 -> {
                windScore = 15
                windStatus = "Strong Breeze (>35 km/h)"
            }
            current.windSpeed >= 20.0 -> {
                windScore = 8
                windStatus = "Moderate Breeze"
            }
        }
        score += windScore
        factors.add(RiskFactor("Wind Velocity", "${current.windSpeed.toInt()} km/h", windScore, windStatus))

        // Factor 4: UV Index Exposure
        var uvScore = 0
        var uvStatus = "Low"
        when {
            current.uvIndex >= 10.0 -> {
                uvScore = 18
                uvStatus = "Extreme UV (10+)"
            }
            current.uvIndex >= 7.5 -> {
                uvScore = 12
                uvStatus = "Very High UV"
            }
            current.uvIndex >= 5.0 -> {
                uvScore = 6
                uvStatus = "Moderate UV"
            }
        }
        score += uvScore
        factors.add(RiskFactor("Solar UV Index", String.format("%.1f", current.uvIndex), uvScore, uvStatus))

        // Factor 5: Atmospheric Visibility
        var visScore = 0
        var visStatus = "Clear"
        when {
            current.visibilityKm <= 2.0 -> {
                visScore = 20
                visStatus = "Fog / Smog Impairment"
            }
            current.visibilityKm <= 5.0 -> {
                visScore = 10
                visStatus = "Hazy Mist"
            }
        }
        score += visScore
        factors.add(RiskFactor("Visibility Range", "${current.visibilityKm} km", visScore, visStatus))

        val finalScore = score.coerceIn(0, 100)
        val level = when {
            finalScore >= 75 -> RiskLevel.SEVERE
            finalScore >= 50 -> RiskLevel.HIGH
            finalScore >= 25 -> RiskLevel.MODERATE
            else -> RiskLevel.LOW
        }

        val primaryThreat = factors.maxByOrNull { it.riskContribution }?.name ?: "None"
        val summary = when (level) {
            RiskLevel.LOW -> "Weather conditions are calm and favorable with negligible disruption risks."
            RiskLevel.MODERATE -> "Moderate meteorological variation observed. Standard precautions recommended."
            RiskLevel.HIGH -> "Elevated weather risks detected primarily driven by $primaryThreat. Exercise active awareness."
            RiskLevel.SEVERE -> "Hazardous weather conditions in effect. Urgent advisory for outdoor protection and travel delays."
        }

        return WeatherRiskAssessment(
            overallScore = finalScore,
            riskLevel = level,
            primaryThreat = primaryThreat,
            summary = summary,
            factors = factors
        )
    }
}
