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
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submarinehunt.ui.components.DecorativeHeaderSonar
import com.example.submarinehunt.ui.components.SubmarineIcon
import com.example.submarinehunt.ui.theme.OceanCyan
import com.example.submarinehunt.ui.theme.OceanCyanLight
import com.example.submarinehunt.ui.theme.OceanFoundEmerald
import com.example.submarinehunt.ui.theme.OceanVeryCloseCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ABOUT",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Brand Header
            Box(
                modifier = Modifier.size(90.dp),
                contentAlignment = Alignment.Center
            ) {
                DecorativeHeaderSonar(
                    size = 90.dp,
                    color = OceanCyan,
                    animated = true
                )
                SubmarineIcon(
                    size = 40.dp,
                    tint = OceanFoundEmerald
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "SUBMARINE HUNT",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            )

            Text(
                text = "Version 1.0",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = OceanCyanLight,
                    letterSpacing = 1.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "An offline hidden-position guessing game.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Information Cards
            AboutSectionCard(
                icon = Icons.Default.Radar,
                title = "HOW IT WORKS",
                description = "Search the ocean grid, follow the warmer/colder clues, and locate the hidden submarine.",
                tint = OceanCyan
            )

            Spacer(modifier = Modifier.height(14.dp))

            AboutSectionCard(
                icon = Icons.Default.GridOn,
                title = "BOARD",
                description = "7 × 7 coordinate grid (A–G, 1–7) with Manhattan distance calculation (|Δrow| + |Δcol|).",
                tint = OceanVeryCloseCyan
            )

            Spacer(modifier = Modifier.height(14.dp))

            AboutSectionCard(
                icon = Icons.Default.Lock,
                title = "PRIVACY",
                description = "No account. No internet connection. No personal data collection.",
                tint = OceanFoundEmerald
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AboutSectionCard(
    icon: ImageVector,
    title: String,
    description: String,
    tint: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(8.dp),
                color = tint.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = tint
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}
