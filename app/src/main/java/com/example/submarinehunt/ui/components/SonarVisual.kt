package com.example.submarinehunt.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.submarinehunt.ui.theme.OceanCyan

/**
 * Lightweight, subtle decorative sonar rings for headers and backgrounds.
 * Decorative only; does NOT reveal or hint at any game coordinates.
 */
@Composable
fun DecorativeHeaderSonar(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    color: Color = OceanCyan,
    animated: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sonar_pulse")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sonar_progress"
    )

    Canvas(modifier = modifier.size(size)) {
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val maxRadius = this.size.width / 2f * 0.95f

        // Static concentric guide circles
        drawCircle(
            color = color.copy(alpha = 0.15f),
            radius = maxRadius * 0.33f,
            center = center,
            style = Stroke(width = 1.2f)
        )
        drawCircle(
            color = color.copy(alpha = 0.15f),
            radius = maxRadius * 0.66f,
            center = center,
            style = Stroke(width = 1.2f)
        )
        drawCircle(
            color = color.copy(alpha = 0.20f),
            radius = maxRadius,
            center = center,
            style = Stroke(width = 1.5f)
        )

        // Center hub
        drawCircle(
            color = color.copy(alpha = 0.8f),
            radius = 3.5f,
            center = center
        )

        if (animated) {
            // Pulse wave 1
            val radius1 = maxRadius * pulseProgress
            val alpha1 = ((1f - pulseProgress) * 0.5f).coerceIn(0f, 1f)
            drawCircle(
                color = color.copy(alpha = alpha1),
                radius = radius1,
                center = center,
                style = Stroke(width = 2f)
            )

            // Pulse wave 2 (offset)
            val progress2 = (pulseProgress + 0.5f) % 1f
            val radius2 = maxRadius * progress2
            val alpha2 = ((1f - progress2) * 0.4f).coerceIn(0f, 1f)
            drawCircle(
                color = color.copy(alpha = alpha2),
                radius = radius2,
                center = center,
                style = Stroke(width = 1.5f)
            )
        }
    }
}
