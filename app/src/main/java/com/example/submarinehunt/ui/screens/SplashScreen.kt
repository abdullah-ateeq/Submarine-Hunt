package com.example.submarinehunt.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submarinehunt.ui.components.DecorativeHeaderSonar
import com.example.submarinehunt.ui.components.SubmarineIcon
import com.example.submarinehunt.ui.theme.OceanBlack
import com.example.submarinehunt.ui.theme.OceanCyan
import com.example.submarinehunt.ui.theme.OceanCyanLight
import com.example.submarinehunt.ui.theme.OceanFoundEmerald
import com.example.submarinehunt.ui.theme.OceanMutedText
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val titleAlpha = remember { Animatable(0f) }
    val gridAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        gridAlpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        titleAlpha.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        delay(600)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OceanBlack),
        contentAlignment = Alignment.Center
    ) {
        // Decorative abstract 7x7 grid in the background
        Canvas(
            modifier = Modifier
                .size(240.dp)
                .alpha(gridAlpha.value * 0.25f)
        ) {
            val step = size.width / 7f
            for (i in 0..7) {
                // Vertical lines
                drawLine(
                    color = OceanCyan,
                    start = Offset(i * step, 0f),
                    end = Offset(i * step, size.height),
                    strokeWidth = 1f
                )
                // Horizontal lines
                drawLine(
                    color = OceanCyan,
                    start = Offset(0f, i * step),
                    end = Offset(size.width, i * step),
                    strokeWidth = 1f
                )
            }
        }

        // Central decorative sonar pulse
        DecorativeHeaderSonar(
            size = 180.dp,
            color = OceanCyanLight,
            animated = true
        )

        Column(
            modifier = Modifier.alpha(titleAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SubmarineIcon(
                size = 48.dp,
                tint = OceanFoundEmerald
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "SUBMARINE",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 6.sp,
                    color = Color.White
                )
            )

            Text(
                text = "HUNT",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 8.sp,
                    color = OceanCyanLight
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "SONAR TARGET SYSTEM",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    letterSpacing = 3.sp,
                    color = OceanMutedText
                )
            )
        }
    }
}
