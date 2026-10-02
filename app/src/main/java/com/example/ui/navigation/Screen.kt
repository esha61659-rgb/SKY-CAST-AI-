package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Default.Home, "nav_tab_home"),
    FORECAST("7-Day", Icons.Default.CalendarMonth, "nav_tab_forecast"),
    HOURLY("Hourly", Icons.Default.Schedule, "nav_tab_hourly"),
    TRENDS("Trends", Icons.AutoMirrored.Filled.ShowChart, "nav_tab_trends"),
    AI_FORECAST("AI Lab", Icons.Default.Psychology, "nav_tab_ai_lab"),
    ALERTS("Alerts", Icons.Default.NotificationsActive, "nav_tab_alerts"),
    COMPARISON("Compare", Icons.AutoMirrored.Filled.CompareArrows, "nav_tab_compare"),
    ABOUT("About", Icons.Default.Info, "nav_tab_about")
}
