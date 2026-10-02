package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeatherCodeHelper

@Composable
fun WeatherIconView(
    weatherCode: Int,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    animate: Boolean = true
) {
    val emoji = WeatherCodeHelper.getIconEmoji(weatherCode)
    val glowColor = WeatherCodeHelper.getConditionColor(weatherCode)

    val infiniteTransition = rememberInfiniteTransition(label = "weather_pulse")
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val currentScale = if (animate) scaleAnim else 1.0f

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Subtle ambient atmospheric glow
        Box(
            modifier = Modifier
                .size(size * 0.85f)
                .scale(currentScale)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Text(
            text = emoji,
            fontSize = (size.value * 0.58f).sp,
            modifier = Modifier.scale(currentScale)
        )
    }
}
