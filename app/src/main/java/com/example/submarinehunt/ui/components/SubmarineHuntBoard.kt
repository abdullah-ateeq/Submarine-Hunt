package com.example.submarinehunt.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submarinehunt.model.CellVisualState
import com.example.submarinehunt.model.Coordinate
import com.example.submarinehunt.ui.theme.OceanBorder
import com.example.submarinehunt.ui.theme.OceanCardNavy
import com.example.submarinehunt.ui.theme.OceanCloserGreen
import com.example.submarinehunt.ui.theme.OceanCyan
import com.example.submarinehunt.ui.theme.OceanCyanLight
import com.example.submarinehunt.ui.theme.OceanDeepNavy
import com.example.submarinehunt.ui.theme.OceanFartherRed
import com.example.submarinehunt.ui.theme.OceanFoundEmerald
import com.example.submarinehunt.ui.theme.OceanFoundGold
import com.example.submarinehunt.ui.theme.OceanMutedText
import com.example.submarinehunt.ui.theme.OceanSurfaceNavy
import com.example.submarinehunt.ui.theme.OceanVeryCloseCyan

private val COLUMN_LETTERS = listOf("A", "B", "C", "D", "E", "F", "G")
private val ROW_NUMBERS = listOf("1", "2", "3", "4", "5", "6", "7")

/**
 * Responsive 7x7 Submarine Hunt Ocean Board.
 *
 * NOTE: The submarine's true location is NEVER passed or known by unsearched cells.
 * All unsearched cells render identically to prevent cheating.
 */
@Composable
fun SubmarineHuntBoard(
    modifier: Modifier = Modifier,
    cellVisualStates: Map<Coordinate, CellVisualState>,
    foundCoordinate: Coordinate?,
    lastSearchedCoordinate: Coordinate?,
    showCoordinateLabels: Boolean = true,
    enabled: Boolean = true,
    animationsEnabled: Boolean = true,
    onCellClick: (Coordinate) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        val totalWidth = maxWidth
        // Leave room for row labels if enabled
        val labelMargin = if (showCoordinateLabels) 26.dp else 0.dp
        val availableGridWidth = totalWidth - labelMargin
        val cellSize = (availableGridWidth / Coordinate.BOARD_SIZE).coerceAtLeast(36.dp)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Top Column Labels (A..G)
            if (showCoordinateLabels) {
                Row(
                    modifier = Modifier.padding(start = labelMargin, bottom = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    COLUMN_LETTERS.forEach { letter ->
                        Box(
                            modifier = Modifier.size(cellSize),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // Grid Rows (1..7)
            for (row in 0 until Coordinate.BOARD_SIZE) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Side Row Number
                    if (showCoordinateLabels) {
                        Box(
                            modifier = Modifier
                                .size(width = labelMargin, height = cellSize)
                                .padding(end = 6.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = ROW_NUMBERS[row],
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    // 7 Cells in this row
                    for (col in 0 until Coordinate.BOARD_SIZE) {
                        val coordinate = Coordinate(row = row, column = col)
                        val visualState = when {
                            coordinate == foundCoordinate -> CellVisualState.FOUND
                            else -> cellVisualStates[coordinate] ?: CellVisualState.UNKNOWN
                        }
                        val isJustSearched = (coordinate == lastSearchedCoordinate)

                        BoardCell(
                            coordinate = coordinate,
                            visualState = visualState,
                            isJustSearched = isJustSearched,
                            isFound = (coordinate == foundCoordinate),
                            cellSize = cellSize,
                            enabled = enabled,
                            animationsEnabled = animationsEnabled,
                            onClick = { onCellClick(coordinate) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardCell(
    coordinate: Coordinate,
    visualState: CellVisualState,
    isJustSearched: Boolean,
    isFound: Boolean,
    cellSize: Dp,
    enabled: Boolean,
    animationsEnabled: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Pulsing scale for found animation
    val pulseScale = remember { Animatable(1f) }
    LaunchedEffect(isFound) {
        if (isFound && animationsEnabled) {
            pulseScale.animateTo(
                targetValue = 1.25f,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
            pulseScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
        }
    }

    // Accessible description according to security requirements:
    // MUST NOT leak submarine position if unsearched!
    val cellDescription = when (visualState) {
        CellVisualState.UNKNOWN -> "Coordinate ${coordinate.label}, unsearched"
        CellVisualState.SEARCHED_FAR -> "Coordinate ${coordinate.label}, searched, far"
        CellVisualState.SEARCHED_CLOSER -> "Coordinate ${coordinate.label}, searched, closer"
        CellVisualState.SEARCHED_FARTHER -> "Coordinate ${coordinate.label}, searched, farther"
        CellVisualState.SEARCHED_VERY_CLOSE -> "Coordinate ${coordinate.label}, searched, very close"
        CellVisualState.FOUND -> "Coordinate ${coordinate.label}, submarine found"
    }

    val cellBorder = when (visualState) {
        CellVisualState.UNKNOWN -> BorderStroke(0.6.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        CellVisualState.SEARCHED_FAR -> BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        CellVisualState.SEARCHED_CLOSER -> BorderStroke(1.2.dp, OceanCloserGreen.copy(alpha = 0.7f))
        CellVisualState.SEARCHED_FARTHER -> BorderStroke(1.dp, OceanFartherRed.copy(alpha = 0.6f))
        CellVisualState.SEARCHED_VERY_CLOSE -> BorderStroke(1.5.dp, OceanVeryCloseCyan)
        CellVisualState.FOUND -> BorderStroke(2.dp, OceanFoundEmerald)
    }

    val cellBackground = when (visualState) {
        CellVisualState.UNKNOWN -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        CellVisualState.SEARCHED_FAR -> MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        CellVisualState.SEARCHED_CLOSER -> OceanCloserGreen.copy(alpha = 0.12f)
        CellVisualState.SEARCHED_FARTHER -> OceanFartherRed.copy(alpha = 0.08f)
        CellVisualState.SEARCHED_VERY_CLOSE -> OceanVeryCloseCyan.copy(alpha = 0.20f)
        CellVisualState.FOUND -> OceanFoundEmerald.copy(alpha = 0.30f)
    }

    Box(
        modifier = Modifier
            .size(cellSize)
            .padding(1.5.dp)
            .scale(if (isFound) pulseScale.value else 1f)
            .clip(RoundedCornerShape(4.dp))
            .background(cellBackground)
            .border(cellBorder, RoundedCornerShape(4.dp))
            .semantics { contentDescription = cellDescription }
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        when (visualState) {
            CellVisualState.UNKNOWN -> {
                // Subtle sonar dot in center
                Canvas(modifier = Modifier.size(6.dp)) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.12f),
                        radius = size.minDimension / 2f
                    )
                }
            }

            CellVisualState.SEARCHED_FAR -> {
                // Subtle small dot
                Canvas(modifier = Modifier.size(8.dp)) {
                    drawCircle(
                        color = Color.Gray.copy(alpha = 0.6f),
                        radius = size.minDimension / 2f
                    )
                }
            }

            CellVisualState.SEARCHED_CLOSER -> {
                // Closer ring
                Canvas(modifier = Modifier.size(16.dp)) {
                    drawCircle(
                        color = OceanCloserGreen,
                        radius = size.minDimension / 2.5f,
                        style = Stroke(width = 2.2f)
                    )
                }
            }

            CellVisualState.SEARCHED_FARTHER -> {
                // Farther subtle marker
                Canvas(modifier = Modifier.size(14.dp)) {
                    drawCircle(
                        color = OceanFartherRed.copy(alpha = 0.75f),
                        radius = size.minDimension / 2.5f,
                        style = Stroke(width = 1.8f)
                    )
                }
            }

            CellVisualState.SEARCHED_VERY_CLOSE -> {
                // Stronger double ring
                Canvas(modifier = Modifier.size(20.dp)) {
                    drawCircle(
                        color = OceanVeryCloseCyan,
                        radius = size.minDimension / 2.3f,
                        style = Stroke(width = 2.2f)
                    )
                    drawCircle(
                        color = OceanVeryCloseCyan.copy(alpha = 0.45f),
                        radius = size.minDimension / 3.8f,
                        style = Stroke(width = 1.5f)
                    )
                }
            }

            CellVisualState.FOUND -> {
                // Pulsing submarine icon
                SubmarineIcon(
                    modifier = Modifier.padding(2.dp),
                    tint = OceanFoundEmerald,
                    size = (cellSize * 0.72f).coerceAtLeast(18.dp)
                )
            }
        }
    }
}
