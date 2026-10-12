package com.example.submarinehunt.ui.screens

import android.content.res.Configuration
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submarinehunt.model.GamePhase
import com.example.submarinehunt.ui.components.FeedbackCard
import com.example.submarinehunt.ui.components.RecentSearchesCard
import com.example.submarinehunt.ui.components.SubmarineHuntBoard
import com.example.submarinehunt.ui.theme.OceanCyanLight
import com.example.submarinehunt.ui.theme.OceanFoundEmerald
import com.example.submarinehunt.ui.theme.OceanFoundGold
import com.example.submarinehunt.ui.viewmodel.GameUiEvent
import com.example.submarinehunt.ui.viewmodel.SubmarineHuntViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: SubmarineHuntViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToResult: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val statistics by viewModel.statistics.collectAsState()
    val isInputLocked by viewModel.isInputLocked.collectAsState()
    val showRestartDialog by viewModel.showRestartDialog.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Collect UI events (Navigation and snackbars)
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is GameUiEvent.NavigateToResult -> {
                    onNavigateToResult()
                }
                is GameUiEvent.ShowMessage -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SUBMARINE HUNT",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to home"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.requestRestart() },
                        enabled = !isInputLocked
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restart hunt"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        if (isLandscape) {
            // Landscape layout: Grid on left, info/feedback on right
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1.1f),
                    contentAlignment = Alignment.Center
                ) {
                    SubmarineHuntBoard(
                        cellVisualStates = gameState.cellVisualStates,
                        foundCoordinate = gameState.foundCoordinate,
                        lastSearchedCoordinate = gameState.lastCoordinate,
                        showCoordinateLabels = settings.showCoordinateLabels,
                        enabled = !isInputLocked && gameState.phase == GamePhase.PLAYING,
                        animationsEnabled = settings.animationsEnabled,
                        onCellClick = { viewModel.onCellTapped(it) }
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TopStatusBar(
                        searches = gameState.searches,
                        bestSearches = statistics.bestSearches,
                        currentStreak = statistics.currentStreak
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FeedbackCard(
                        feedback = gameState.lastFeedback,
                        distanceCategory = gameState.lastDistanceCategory,
                        currentCoordinate = gameState.lastCoordinate,
                        previousCoordinate = gameState.previousCoordinate
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    RecentSearchesCard(searchHistory = gameState.searchHistory)
                }
            }
        } else {
            // Portrait layout: Header status -> Board -> Feedback Card -> Recent Searches
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                TopStatusBar(
                    searches = gameState.searches,
                    bestSearches = statistics.bestSearches,
                    currentStreak = statistics.currentStreak
                )

                Spacer(modifier = Modifier.height(12.dp))

                SubmarineHuntBoard(
                    cellVisualStates = gameState.cellVisualStates,
                    foundCoordinate = gameState.foundCoordinate,
                    lastSearchedCoordinate = gameState.lastCoordinate,
                    showCoordinateLabels = settings.showCoordinateLabels,
                    enabled = !isInputLocked && gameState.phase == GamePhase.PLAYING,
                    animationsEnabled = settings.animationsEnabled,
                    onCellClick = { viewModel.onCellTapped(it) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                FeedbackCard(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    feedback = gameState.lastFeedback,
                    distanceCategory = gameState.lastDistanceCategory,
                    currentCoordinate = gameState.lastCoordinate,
                    previousCoordinate = gameState.previousCoordinate
                )

                Spacer(modifier = Modifier.height(12.dp))

                RecentSearchesCard(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    searchHistory = gameState.searchHistory
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Restart Confirmation Dialog
    if (showRestartDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissRestartDialog() },
            title = {
                Text(
                    text = "Start a new hunt?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "Current hunt progress will be reset and the submarine will relocate.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.startNewHunt() }
                ) {
                    Text(
                        text = "NEW HUNT",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = OceanFoundEmerald
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissRestartDialog() }
                ) {
                    Text(
                        text = "CANCEL",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        )
    }
}

/**
 * Clean compact status bar showing: Searches, Best, Current streak.
 */
@Composable
private fun TopStatusBar(
    searches: Int,
    bestSearches: Int,
    currentStreak: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SEARCHES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = "$searches",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = OceanCyanLight
                    )
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "BEST",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = if (bestSearches > 0) "$bestSearches" else "-",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = OceanFoundEmerald
                    )
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "STREAK",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = "$currentStreak",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = OceanFoundGold
                    )
                )
            }
        }
    }
}
