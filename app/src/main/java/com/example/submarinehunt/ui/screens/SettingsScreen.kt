package com.example.submarinehunt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submarinehunt.data.AppThemeSetting
import com.example.submarinehunt.ui.theme.OceanCyan
import com.example.submarinehunt.ui.theme.OceanCyanLight
import com.example.submarinehunt.ui.theme.OceanFartherRed
import com.example.submarinehunt.ui.viewmodel.SubmarineHuntViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SubmarineHuntViewModel
) {
    val settings by viewModel.settings.collectAsState()

    var showResetStatsDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SETTINGS",
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // THEME SECTION
            SectionTitle(title = "APPEARANCE")
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Theme",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeChip(
                            label = "System",
                            selected = settings.theme == AppThemeSetting.SYSTEM,
                            onClick = { viewModel.setTheme(AppThemeSetting.SYSTEM) }
                        )
                        ThemeChip(
                            label = "Light",
                            selected = settings.theme == AppThemeSetting.LIGHT,
                            onClick = { viewModel.setTheme(AppThemeSetting.LIGHT) }
                        )
                        ThemeChip(
                            label = "Dark",
                            selected = settings.theme == AppThemeSetting.DARK,
                            onClick = { viewModel.setTheme(AppThemeSetting.DARK) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    SettingSwitchRow(
                        title = "Dynamic Color",
                        subtitle = "Use Material You system accent colors on Android 12+",
                        checked = settings.dynamicColor,
                        onCheckedChange = { viewModel.setDynamicColor(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // GAMEPLAY SECTION
            SectionTitle(title = "GAMEPLAY & SENSORY")
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingSwitchRow(
                        title = "Sound Effects",
                        subtitle = "Play sonar audio cues for closer, farther, and found",
                        checked = settings.soundEnabled,
                        onCheckedChange = { viewModel.setSound(it) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    SettingSwitchRow(
                        title = "Haptic Feedback",
                        subtitle = "Vibrate on search pings and submarine discovery",
                        checked = settings.hapticEnabled,
                        onCheckedChange = { viewModel.setHaptics(it) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    SettingSwitchRow(
                        title = "Animations",
                        subtitle = "Sonar ripple and cell reveal visual transitions",
                        checked = settings.animationsEnabled,
                        onCheckedChange = { viewModel.setAnimations(it) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    SettingSwitchRow(
                        title = "Show Coordinate Labels",
                        subtitle = "Display A–G columns and 1–7 row labels on the ocean grid",
                        checked = settings.showCoordinateLabels,
                        onCheckedChange = { viewModel.setShowCoordinateLabels(it) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    SettingSwitchRow(
                        title = "Confirm New Hunt",
                        subtitle = "Prompt for confirmation before resetting an ongoing hunt",
                        checked = settings.confirmNewHunt,
                        onCheckedChange = { viewModel.setConfirmNewHunt(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // DATA MANAGEMENT SECTION
            SectionTitle(title = "DATA MANAGEMENT")
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showResetStatsDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Reset Statistics")
                    }

                    OutlinedButton(
                        onClick = { showResetSettingsDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Reset Settings to Default")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Reset Statistics Confirmation Dialog
    if (showResetStatsDialog) {
        AlertDialog(
            onDismissRequest = { showResetStatsDialog = false },
            title = { Text("Reset Statistics?") },
            text = { Text("This will erase all recorded hunts, best score, streak, and search history.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetStatistics()
                        showResetStatsDialog = false
                    }
                ) {
                    Text("RESET", color = OceanFartherRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetStatsDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }

    // Reset Settings Confirmation Dialog
    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = { Text("Reset Settings?") },
            text = { Text("This will restore all preferences (theme, audio, haptics, grid labels) to defaults.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetSettings()
                        showResetSettingsDialog = false
                    }
                ) {
                    Text("RESET", color = OceanFartherRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
            letterSpacing = 1.5.sp,
            color = OceanCyanLight,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = OceanCyan,
                checkedTrackColor = OceanCyan.copy(alpha = 0.4f)
            )
        )
    }
}

@Composable
private fun ThemeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = OceanCyan,
            selectedLabelColor = MaterialTheme.colorScheme.surface
        )
    )
}
