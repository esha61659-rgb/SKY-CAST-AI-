package com.example.data.remote

import android.util.Log
import com.example.data.model.CityLocation
import com.example.data.model.CurrentWeather
import com.example.data.model.DailyForecastItem
import com.example.data.model.HourlyForecastItem
import com.example.data.model.WeatherCodeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class WeatherApiService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
) {
    companion object {
        private const val TAG = "WeatherApiService"
        private const val BASE_WEATHER_URL = "https://api.open-meteo.com/v1/forecast"
        private const val BASE_GEO_URL = "https://geocoding-api.open-meteo.com/v1/search"

        val PAKISTAN_CITIES = listOf(
            CityLocation("Multan", "Pakistan", "Punjab", 30.1575, 71.5249),
            CityLocation("Lahore", "Pakistan", "Punjab", 31.5204, 74.3587),
            CityLocation("Islamabad", "Pakistan", "Federal Capital", 33.6844, 73.0479),
            CityLocation("Karachi", "Pakistan", "Sindh", 24.8607, 67.0011),
            CityLocation("Faisalabad", "Pakistan", "Punjab", 31.4504, 73.1350),
            CityLocation("Rawalpindi", "Pakistan", "Punjab", 33.5651, 73.0169),
            CityLocation("Peshawar", "Pakistan", "Khyber Pakhtunkhwa", 34.0151, 71.5249),
            CityLocation("Quetta", "Pakistan", "Balochistan", 30.1798, 66.9750),
            CityLocation("Hyderabad", "Pakistan", "Sindh", 25.3960, 68.3578),
            CityLocation("Bahawalpur", "Pakistan", "Punjab", 29.3544, 71.6911)
        )

        val GLOBAL_PRESETS = listOf(
            CityLocation("Dubai", "United Arab Emirates", "Dubai", 25.2048, 55.2708),
            CityLocation("London", "United Kingdom", "England", 51.5074, -0.1278),
            CityLocation("New York", "United States", "New York", 40.7128, -74.0060),
            CityLocation("Tokyo", "Japan", "Tokyo", 35.6762, 139.6503),
            CityLocation("Istanbul", "Turkey", "Istanbul", 41.0082, 28.9784)
        )
    }

    suspend fun fetchWeather(lat: Double, lon: Double): Pair<CurrentWeather, Pair<List<HourlyForecastItem>, List<DailyForecastItem>>> =
        withContext(Dispatchers.IO) {
            val url = "$BASE_WEATHER_URL?" +
                    "latitude=$lat&longitude=$lon" +
                    "&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,surface_pressure,wind_speed_10m,wind_direction_10m" +
                    "&hourly=temperature_2m,relative_humidity_2m,precipitation_probability,weather_code,surface_pressure,wind_speed_10m,uv_index" +
                    "&daily=weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,uv_index_max,precipitation_sum,precipitation_probability_max,wind_speed_10m_max" +
                    "&timezone=auto"

            val request = Request.Builder().url(url).build()
            val body = client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("Weather API HTTP Error: ${response.code}")
                }
                response.body?.string() ?: throw Exception("Empty weather API response")
            }
            parseWeatherJson(body)
        }

    private fun parseWeatherJson(jsonStr: String): Pair<CurrentWeather, Pair<List<HourlyForecastItem>, List<DailyForecastItem>>> {
        val root = JSONObject(jsonStr)

        // Current weather
        val currentJson = root.getJSONObject("current")
        val currentTemp = currentJson.optDouble("temperature_2m", 25.0)
        val currentFeels = currentJson.optDouble("apparent_temperature", currentTemp)
        val currentHumidity = currentJson.optInt("relative_humidity_2m", 50)
        val currentWeatherCode = currentJson.optInt("weather_code", 0)
        val currentWindSpeed = currentJson.optDouble("wind_speed_10m", 10.0)
        val currentWindDir = currentJson.optInt("wind_direction_10m", 0)
        val currentPressure = currentJson.optDouble("surface_pressure", 1013.2)
        val isDay = currentJson.optInt("is_day", 1) == 1

        // Daily
        val dailyJson = root.optJSONObject("daily")
        val dailyTimes = dailyJson?.optJSONArray("time")
        val dailyCodes = dailyJson?.optJSONArray("weather_code")
        val dailyMaxs = dailyJson?.optJSONArray("temperature_2m_max")
        val dailyMins = dailyJson?.optJSONArray("temperature_2m_min")
        val dailySunrises = dailyJson?.optJSONArray("sunrise")
        val dailySunsets = dailyJson?.optJSONArray("sunset")
        val dailyRainProbs = dailyJson?.optJSONArray("precipitation_probability_max")
        val dailyWindMaxs = dailyJson?.optJSONArray("wind_speed_10m_max")
        val dailyUvMaxs = dailyJson?.optJSONArray("uv_index_max")

        var firstSunrise = "06:00"
        var firstSunset = "18:30"
        val dailyItems = mutableListOf<DailyForecastItem>()

        if (dailyTimes != null) {
            val count = minOf(dailyTimes.length(), 7)
            val inSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dayNameSdf = SimpleDateFormat("EEE", Locale.US)
            val dateDispSdf = SimpleDateFormat("MMM d", Locale.US)
            val timeSdf = SimpleDateFormat("HH:mm", Locale.US)
            val isoTimeSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)

            for (i in 0 until count) {
                val dateIso = dailyTimes.optString(i, "")
                var dayName = "Day ${i + 1}"
                var dateDisplay = dateIso
                try {
                    val parsedDate = inSdf.parse(dateIso)
                    if (parsedDate != null) {
                        dayName = if (i == 0) "Today" else if (i == 1) "Tomorrow" else dayNameSdf.format(parsedDate)
                        dateDisplay = dateDispSdf.format(parsedDate)
                    }
                } catch (e: Exception) {
                    // ignore
                }

                val code = dailyCodes?.optInt(i, 0) ?: 0
                val tMax = dailyMaxs?.optDouble(i, 30.0) ?: 30.0
                val tMin = dailyMins?.optDouble(i, 20.0) ?: 20.0
                val rainP = dailyRainProbs?.optInt(i, 0) ?: 0
                val wMax = dailyWindMaxs?.optDouble(i, 12.0) ?: 12.0
                val uvM = dailyUvMaxs?.optDouble(i, 5.0) ?: 5.0

                var sr = "06:00"
                var ss = "18:30"
                try {
                    val srRaw = dailySunrises?.optString(i, "") ?: ""
                    val ssRaw = dailySunsets?.optString(i, "") ?: ""
                    if (srRaw.contains("T")) {
                        sr = timeSdf.format(isoTimeSdf.parse(srRaw) ?: Date())
                    }
                    if (ssRaw.contains("T")) {
                        ss = timeSdf.format(isoTimeSdf.parse(ssRaw) ?: Date())
                    }
                } catch (e: Exception) {
                    // fallback
                }

                if (i == 0) {
                    firstSunrise = sr
                    firstSunset = ss
                }

                dailyItems.add(
                    DailyForecastItem(
                        dateIso = dateIso,
                        dayName = dayName,
                        dateDisplay = dateDisplay,
                        tempMax = tMax,
                        tempMin = tMin,
                        weatherCode = code,
                        condition = WeatherCodeHelper.getCondition(code),
                        rainProbability = rainP,
                        windSpeedMax = wMax,
                        humidityAvg = currentHumidity,
                        uvIndexMax = uvM,
                        sunrise = sr,
                        sunset = ss
                    )
                )
            }
        }

        // Hourly
        val hourlyJson = root.optJSONObject("hourly")
        val hourlyTimes = hourlyJson?.optJSONArray("time")
        val hourlyTemps = hourlyJson?.optJSONArray("temperature_2m")
        val hourlyHumids = hourlyJson?.optJSONArray("relative_humidity_2m")
        val hourlyRainProbs = hourlyJson?.optJSONArray("precipitation_probability")
        val hourlyCodes = hourlyJson?.optJSONArray("weather_code")
        val hourlyWinds = hourlyJson?.optJSONArray("wind_speed_10m")
        val hourlyUvs = hourlyJson?.optJSONArray("uv_index")

        val hourlyItems = mutableListOf<HourlyForecastItem>()
        var currentUvIndex = dailyItems.firstOrNull()?.uvIndexMax ?: 4.5

        if (hourlyTimes != null) {
            val totalHours = hourlyTimes.length()
            val isoTimeSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
            val displayHourSdf = SimpleDateFormat("h a", Locale.US)
            val nowMillis = System.currentTimeMillis()

            // Find index closest to now
            var startIndex = 0
            for (i in 0 until totalHours) {
                try {
                    val parsed = isoTimeSdf.parse(hourlyTimes.getString(i))
                    if (parsed != null && parsed.time >= nowMillis - (60 * 60 * 1000)) {
                        startIndex = i
                        break
                    }
                } catch (e: Exception) {
                    // ignore
                }
            }

            val count = minOf(24, totalHours - startIndex)
            for (i in 0 until count) {
                val idx = startIndex + i
                val timeIso = hourlyTimes.optString(idx, "")
                var timeDisp = "Now"
                if (i > 0) {
                    try {
                        val parsed = isoTimeSdf.parse(timeIso)
                        if (parsed != null) {
                            timeDisp = displayHourSdf.format(parsed)
                        }
                    } catch (e: Exception) {
                        timeDisp = "$i:00"
                    }
                }

                val t = hourlyTemps?.optDouble(idx, currentTemp) ?: currentTemp
                val h = hourlyHumids?.optInt(idx, currentHumidity) ?: currentHumidity
                val rp = hourlyRainProbs?.optInt(idx, 0) ?: 0
                val code = hourlyCodes?.optInt(idx, currentWeatherCode) ?: currentWeatherCode
                val ws = hourlyWinds?.optDouble(idx, currentWindSpeed) ?: currentWindSpeed
                val uv = hourlyUvs?.optDouble(idx, 0.0) ?: 0.0

                if (i == 0 && uv > 0.0) {
                    currentUvIndex = uv
                }

                hourlyItems.add(
                    HourlyForecastItem(
                        timeIso = timeIso,
                        timeDisplay = timeDisp,
                        temperature = t,
                        weatherCode = code,
                        condition = WeatherCodeHelper.getCondition(code),
                        rainProbability = rp,
                        windSpeed = ws,
                        humidity = h
                    )
                )
            }
        }

        val currentTimeFormatted = SimpleDateFormat("EEEE, d MMMM • h:mm a", Locale.US).format(Date())

        // Compute estimated visibility (derived from humidity & rain code, or approx 10.0km default)
        val estimatedVisibility = when {
            currentWeatherCode in listOf(45, 48) -> 1.5
            currentWeatherCode in listOf(65, 95, 96, 99) -> 3.5
            currentHumidity > 85 -> 6.0
            else -> 10.0
        }

        val currentWeather = CurrentWeather(
            temperature = currentTemp,
            feelsLike = currentFeels,
            weatherCode = currentWeatherCode,
            condition = WeatherCodeHelper.getCondition(currentWeatherCode),
            humidity = currentHumidity,
            windSpeed = currentWindSpeed,
            windDirection = currentWindDir,
            pressure = currentPressure,
            visibilityKm = estimatedVisibility,
            uvIndex = currentUvIndex,
            isDay = isDay,
            sunrise = firstSunrise,
            sunset = firstSunset,
            timeFormatted = currentTimeFormatted
        )

        return Pair(currentWeather, Pair(hourlyItems, dailyItems))
    }

    suspend fun searchCities(query: String): List<CityLocation> = withContext(Dispatchers.IO) {
        if (query.trim().isEmpty()) {
            return@withContext PAKISTAN_CITIES
        }

        val trimmed = query.trim().lowercase(Locale.ROOT)
        // First check in Pakistan cities and global presets
        val localMatches = (PAKISTAN_CITIES + GLOBAL_PRESETS).filter {
            it.name.lowercase(Locale.ROOT).contains(trimmed) ||
                    it.country.lowercase(Locale.ROOT).contains(trimmed) ||
                    it.admin1.lowercase(Locale.ROOT).contains(trimmed)
        }

        try {
            val encodedQuery = java.net.URLEncoder.encode(query.trim(), "UTF-8")
            val url = "$BASE_GEO_URL?name=$encodedQuery&count=10&language=en&format=json"
            val request = Request.Builder().url(url).build()
            val body = client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.string() ?: ""
                } else {
                    ""
                }
            }
            if (body.isNotEmpty()) {
                val root = JSONObject(body)
                val results = root.optJSONArray("results")
                if (results != null) {
                    val apiCities = mutableListOf<CityLocation>()
                    for (i in 0 until results.length()) {
                        val item = results.getJSONObject(i)
                        val name = item.optString("name")
                        val country = item.optString("country", "")
                        val admin1 = item.optString("admin1", "")
                        val lat = item.optDouble("latitude")
                        val lon = item.optDouble("longitude")
                        apiCities.add(CityLocation(name, country, admin1, lat, lon))
                    }
                    // Combine results, prioritizing exact match
                    val combined = (localMatches + apiCities).distinctBy { "${it.name}_${it.country}_${it.admin1}" }
                    return@withContext combined
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Geocoding API failed, falling back to local list: ${e.message}")
        }

        localMatches.ifEmpty {
            PAKISTAN_CITIES.filter { it.name.contains(trimmed, ignoreCase = true) }
        }
    }
}
