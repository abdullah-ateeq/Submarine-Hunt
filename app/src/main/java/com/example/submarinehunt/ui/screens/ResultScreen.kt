package com.example.submarinehunt.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submarinehunt.engine.SubmarineHuntEngine
import com.example.submarinehunt.ui.components.DecorativeHeaderSonar
import com.example.submarinehunt.ui.components.SubmarineIcon
import com.example.submarinehunt.ui.theme.OceanCyan
import com.example.submarinehunt.ui.theme.OceanCyanLight
import com.example.submarinehunt.ui.theme.OceanFoundEmerald
import com.example.submarinehunt.ui.theme.OceanFoundGold
import com.example.submarinehunt.ui.viewmodel.SubmarineHuntViewModel

@Composable
fun ResultScreen(
    viewModel: SubmarineHuntViewModel,
    onPlayAgain: () -> Unit,
    onGoHome: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val statistics by viewModel.statistics.collectAsState()

    val searches = gameState.searches
    val isPerfectHunt = (searches == 1)
    val dynamicMessage = SubmarineHuntEngine.getResultMessage(searches)
    val submarineLabel = gameState.foundCoordinate?.label ?: "TARGET"

    val accuracyFormatted = if (searches > 0) {
        val acc = (1.0 / searches) * 100.0
        if (acc == acc.toLong().toDouble()) {
            "${acc.toLong()}%"
        } else {
            String.format(java.util.Locale.US, "%.1f%%", acc)
        }
    } else {
        "0%"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Celebratory Submarine + Sonar
        Box(
            modifier = Modifier.size(130.dp),
            contentAlignment = Alignment.Center
        ) {
            DecorativeHeaderSonar(
                size = 130.dp,
                color = OceanFoundEmerald,
                animated = true
            )
            SubmarineIcon(
                size = 56.dp,
                tint = OceanFoundEmerald
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isPerfectHunt) "PERFECT HUNT" else "SUBMARINE FOUND",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                color = if (isPerfectHunt) OceanFoundGold else OceanFoundEmerald
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = dynamicMessage,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Target Coordinate Badge
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, OceanFoundEmerald.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Submarine Sector: ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = submarineLabel,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanFoundEmerald
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Results Card (Searches, Score, Best, Accuracy)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                ResultMetricRow(label = "Searches", value = "$searches")
                ResultDivider()
                ResultMetricRow(label = "Score", value = "${gameState.score}", highlightColor = OceanFoundGold)
                ResultDivider()
                ResultMetricRow(
                    label = "Best Searches",
                    value = if (statistics.bestSearches > 0) "${statistics.bestSearches}" else "-"
                )
                ResultDivider()
                ResultMetricRow(
                    label = "Search Accuracy",
                    value = accuracyFormatted,
                    highlightColor = OceanCyanLight
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Action Buttons: PLAY AGAIN & HOME
        Button(
            onClick = {
                viewModel.startNewHunt()
                onPlayAgain()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OceanCyan,
                contentColor = Color.Black
            )
        ) {
            Icon(imageVector = Icons.Default.Replay, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PLAY AGAIN",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onGoHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Home, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "HOME",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 1.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ResultMetricRow(
    label: String,
    value: String,
    highlightColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = if (highlightColor != Color.Unspecified) highlightColor else MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
private fun ResultDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.6.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    )
}
