package com.example.submarinehunt.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.submarinehunt.ui.screens.AboutScreen
import com.example.submarinehunt.ui.screens.GameScreen
import com.example.submarinehunt.ui.screens.HomeScreen
import com.example.submarinehunt.ui.screens.ResultScreen
import com.example.submarinehunt.ui.screens.SettingsScreen
import com.example.submarinehunt.ui.screens.SplashScreen
import com.example.submarinehunt.ui.theme.OceanCyan
import com.example.submarinehunt.ui.theme.OceanSurfaceNavy
import com.example.submarinehunt.ui.viewmodel.SubmarineHuntViewModel

@Composable
fun MainNavigation(
    viewModel: SubmarineHuntViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Show bottom navigation on Home, Settings, and About only!
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Settings.route,
        Screen.About.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    BottomNavItem.items.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.surface,
                                selectedTextColor = OceanCyan,
                                indicatorColor = OceanCyan,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onStartGame = {
                        navController.navigate(Screen.Game.route)
                    }
                )
            }

            composable(Screen.Game.route) {
                GameScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToResult = {
                        navController.navigate(Screen.Result.route) {
                            popUpTo(Screen.Game.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Result.route) {
                ResultScreen(
                    viewModel = viewModel,
                    onPlayAgain = {
                        navController.navigate(Screen.Game.route) {
                            popUpTo(Screen.Result.route) { inclusive = true }
                        }
                    },
                    onGoHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = viewModel)
            }

            composable(Screen.About.route) {
                AboutScreen()
            }
        }
    }
}
