package com.example.submarinehunt.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.submarinehunt.ui.theme.OceanCyanLight

/**
 * Custom vector-like drawn Submarine Icon.
 * Minimalist, elegant military sonar submarine silhouette.
 */
@Composable
fun SubmarineIcon(
    modifier: Modifier = Modifier,
    tint: Color = OceanCyanLight,
    size: Dp = 24.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Main hull (elongated pill shape)
        val hullPath = Path().apply {
            val hullRect = RoundRect(
                left = w * 0.12f,
                top = h * 0.42f,
                right = w * 0.88f,
                bottom = h * 0.72f,
                cornerRadius = CornerRadius(h * 0.15f, h * 0.15f)
            )
            addRoundRect(hullRect)
        }
        drawPath(hullPath, color = tint, style = Fill)

        // Conning tower / Sail
        val sailPath = Path().apply {
            val sailRect = RoundRect(
                left = w * 0.44f,
                top = h * 0.22f,
                right = w * 0.60f,
                bottom = h * 0.44f,
                cornerRadius = CornerRadius(w * 0.04f, w * 0.04f)
            )
            addRoundRect(sailRect)
        }
        drawPath(sailPath, color = tint, style = Fill)

        // Periscope
        drawLine(
            color = tint,
            start = Offset(w * 0.52f, h * 0.22f),
            end = Offset(w * 0.52f, h * 0.12f),
            strokeWidth = w * 0.05f
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.52f, h * 0.12f),
            end = Offset(w * 0.58f, h * 0.12f),
            strokeWidth = w * 0.05f
        )

        // Tail fins / Propeller rudder
        val finPath = Path().apply {
            moveTo(w * 0.14f, h * 0.35f)
            lineTo(w * 0.06f, h * 0.28f)
            lineTo(w * 0.06f, h * 0.82f)
            lineTo(w * 0.14f, h * 0.75f)
            close()
        }
        drawPath(finPath, color = tint, style = Fill)
    }
}
