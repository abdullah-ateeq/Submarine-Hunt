package com.example.submarinehunt.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submarinehunt.ui.theme.OceanCyan
import com.example.submarinehunt.ui.theme.OceanCyanLight
import com.example.submarinehunt.ui.theme.OceanFoundEmerald
import com.example.submarinehunt.ui.theme.OceanVeryCloseCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToPlayBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HOW TO PLAY",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = OceanCyanLight
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            InstructionStep(
                icon = Icons.Default.Radar,
                title = "1. SEARCH",
                description = "Tap any coordinate on the 7×7 grid to scan that sector.",
                tint = OceanCyan
            )

            Spacer(modifier = Modifier.height(14.dp))

            InstructionStep(
                icon = Icons.Default.LocationSearching,
                title = "2. TRACK",
                description = "The sonar tells you whether your search was CLOSER or FARTHER than your previous ping.",
                tint = OceanVeryCloseCyan
            )

            Spacer(modifier = Modifier.height(14.dp))

            InstructionStep(
                icon = Icons.Default.NearMe,
                title = "3. NARROW",
                description = "Triangulate the Manhattan distance cues to narrow down the submarine's location.",
                tint = OceanCyanLight
            )

            Spacer(modifier = Modifier.height(14.dp))

            InstructionStep(
                icon = Icons.Default.Check,
                title = "4. FIND",
                description = "Tap the submarine's exact sector (e.g. D4) to complete the hunt and reveal its position.",
                tint = OceanFoundEmerald
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Example chip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, OceanCyan.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Target Coordinate Example:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "D4",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = OceanFoundEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismissRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OceanCyan,
                    contentColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = "GOT IT",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun InstructionStep(
    icon: ImageVector,
    title: String,
    description: String,
    tint: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = tint
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
