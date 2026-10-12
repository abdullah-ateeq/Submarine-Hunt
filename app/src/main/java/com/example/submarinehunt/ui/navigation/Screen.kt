package com.example.submarinehunt.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation routes in Submarine Hunt.
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Game : Screen("game")
    object Result : Screen("result")
    object Settings : Screen("settings")
    object About : Screen("about")
}

/**
 * Items appearing on the bottom navigation bar (Home, Settings, About).
 * Hidden during Splash, Game, and Result.
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(Screen.Home.route, "Home", Icons.Default.Home)
    object Settings : BottomNavItem(Screen.Settings.route, "Settings", Icons.Default.Settings)
    object About : BottomNavItem(Screen.About.route, "About", Icons.Default.Info)

    companion object {
        val items = listOf(Home, Settings, About)
    }
}
