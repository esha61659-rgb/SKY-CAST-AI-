package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RiskLevel
import com.example.data.model.WeatherRiskAssessment
import com.example.ui.theme.RiskHighOrange
import com.example.ui.theme.RiskLowGreen
import com.example.ui.theme.RiskModerateYellow
import com.example.ui.theme.RiskSevereRed

@Composable
fun RiskScoreBadge(
    riskLevel: RiskLevel,
    score: Int,
    modifier: Modifier = Modifier
) {
    val (badgeBg, textColor) = when (riskLevel) {
        RiskLevel.LOW -> Pair(RiskLowGreen.copy(alpha = 0.2f), RiskLowGreen)
        RiskLevel.MODERATE -> Pair(RiskModerateYellow.copy(alpha = 0.2f), RiskModerateYellow)
        RiskLevel.HIGH -> Pair(RiskHighOrange.copy(alpha = 0.2f), RiskHighOrange)
        RiskLevel.SEVERE -> Pair(RiskSevereRed.copy(alpha = 0.25f), RiskSevereRed)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(badgeBg)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = riskLevel.emoji, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "${riskLevel.label} RISK ($score/100)",
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RiskScoreGaugeCard(
    assessment: WeatherRiskAssessment,
    modifier: Modifier = Modifier,
    onViewDetailsClick: (() -> Unit)? = null
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (assessment.overallScore / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 900),
        label = "riskProgress"
    )

    val riskColor = when (assessment.riskLevel) {
        RiskLevel.LOW -> RiskLowGreen
        RiskLevel.MODERATE -> RiskModerateYellow
        RiskLevel.HIGH -> RiskHighOrange
        RiskLevel.SEVERE -> RiskSevereRed
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onViewDetailsClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "WEATHER RISK SCORE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = assessment.riskLevel.emoji,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${assessment.riskLevel.label} (${assessment.overallScore}/100)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = riskColor
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = assessment.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Circular Meter
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(70.dp)
            ) {
                Canvas(modifier = Modifier.size(64.dp)) {
                    val strokeWidth = 7.dp.toPx()
                    // Track
                    drawArc(
                        color = Color.White.copy(alpha = 0.1f),
                        startAngle = 140f,
                        sweepAngle = 260f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    // Indicator
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                RiskLowGreen,
                                RiskModerateYellow,
                                RiskHighOrange,
                                RiskSevereRed
                            )
                        ),
                        startAngle = 140f,
                        sweepAngle = 260f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${assessment.overallScore}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = riskColor
                    )
                    Text(
                        text = "INDEX",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
