package com.example.submarinehunt.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submarinehunt.ui.components.DecorativeHeaderSonar
import com.example.submarinehunt.ui.components.HowToPlayBottomSheet
import com.example.submarinehunt.ui.components.StatsCard
import com.example.submarinehunt.ui.components.SubmarineIcon
import com.example.submarinehunt.ui.theme.OceanCyan
import com.example.submarinehunt.ui.theme.OceanCyanLight
import com.example.submarinehunt.ui.theme.OceanFoundEmerald
import com.example.submarinehunt.ui.theme.OceanMutedText
import com.example.submarinehunt.ui.viewmodel.SubmarineHuntViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: SubmarineHuntViewModel,
    onStartGame: () -> Unit
) {
    val statistics by viewModel.statistics.collectAsState()
    var showHowToPlay by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Hero Brand Section with decorative sonar
        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            DecorativeHeaderSonar(
                size = 140.dp,
                color = OceanCyan,
                animated = true
            )
            SubmarineIcon(
                size = 54.dp,
                tint = OceanFoundEmerald
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SUBMARINE HUNT",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Track the signal. Find the target.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Primary HUNT Button
        Button(
            onClick = {
                viewModel.startNewHunt()
                onStartGame()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OceanCyan,
                contentColor = Color.Black
            )
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "HUNT",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // How to play button
        OutlinedButton(
            onClick = { showHowToPlay = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "HOW TO PLAY",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 1.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Home Statistics Card
        StatsCard(
            statistics = statistics
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showHowToPlay) {
        HowToPlayBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showHowToPlay = false }
        )
    }
}
