package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object WeatherCodeHelper {

    fun getCondition(code: Int): String {
        return when (code) {
            0 -> "Clear Sky"
            1 -> "Mainly Clear"
            2 -> "Partly Cloudy"
            3 -> "Overcast"
            45, 48 -> "Foggy & Mist"
            51, 53, 55 -> "Drizzle"
            56, 57 -> "Freezing Drizzle"
            61 -> "Slight Rain"
            63 -> "Moderate Rain"
            65 -> "Heavy Rain"
            66, 67 -> "Freezing Rain"
            71 -> "Slight Snow"
            73 -> "Moderate Snow"
            75 -> "Heavy Snow"
            77 -> "Snow Grains"
            80, 81, 82 -> "Rain Showers"
            85, 86 -> "Snow Showers"
            95 -> "Thunderstorm"
            96, 99 -> "Severe Thunderstorm with Hail"
            else -> "Partly Cloudy"
        }
    }

    fun getIconEmoji(code: Int): String {
        return when (code) {
            0 -> "☀️"
            1, 2 -> "🌤️"
            3 -> "☁️"
            45, 48 -> "🌫️"
            51, 53, 55 -> "🌦️"
            61, 63 -> "🌧️"
            65 -> "⛈️"
            66, 67 -> "🌧️❄️"
            71, 73, 75, 77 -> "❄️"
            80, 81, 82 -> "🌧️"
            85, 86 -> "🌨️"
            95, 96, 99 -> "⛈️"
            else -> "🌤️"
        }
    }

    fun getVectorIcon(code: Int): ImageVector {
        return when (code) {
            0, 1 -> Icons.Default.WbSunny
            2 -> Icons.Default.CloudQueue
            3 -> Icons.Default.Cloud
            45, 48 -> Icons.Default.Air
            51, 53, 55, 61, 63 -> Icons.Default.Grain
            65, 80, 81, 82 -> Icons.Default.Grain
            95, 96, 99 -> Icons.Default.Thunderstorm
            else -> Icons.Default.WbSunny
        }
    }

    fun getConditionColor(code: Int): Color {
        return when (code) {
            0, 1 -> Color(0xFFFFB703) // Solar Amber
            2 -> Color(0xFF64B5F6) // Sky blue
            3 -> Color(0xFF90A4AE) // Slate gray
            45, 48 -> Color(0xFFB0BEC5) // Fog
            51, 53, 55, 61, 63, 65, 80, 81, 82 -> Color(0xFF29B6F6) // Rain cyan
            95, 96, 99 -> Color(0xFFEF5350) // Thunderstorm red
            else -> Color(0xFF4FC3F7)
        }
    }

    fun isRainy(code: Int): Boolean {
        return code in listOf(51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82, 95, 96, 99)
    }

    fun isStormy(code: Int): Boolean {
        return code in listOf(95, 96, 99)
    }
}
