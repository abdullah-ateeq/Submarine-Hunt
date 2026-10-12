package com.example.submarinehunt.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.submarinehunt.data.AppThemeSetting

private val DarkColorScheme = darkColorScheme(
    primary = OceanCyan,
    onPrimary = OceanBlack,
    primaryContainer = OceanCardNavy,
    onPrimaryContainer = OceanCyanLight,
    secondary = OceanTeal,
    onSecondary = OceanBlack,
    secondaryContainer = OceanSurfaceNavy,
    onSecondaryContainer = OceanSoftWhite,
    tertiary = OceanVeryCloseCyan,
    onTertiary = OceanBlack,
    background = OceanBlack,
    onBackground = OceanSoftWhite,
    surface = OceanDeepNavy,
    onSurface = OceanSoftWhite,
    surfaceVariant = OceanSurfaceNavy,
    onSurfaceVariant = OceanMutedText,
    outline = OceanBorder
)

private val LightColorScheme = lightColorScheme(
    primary = OceanLightPrimary,
    onPrimary = Color.White,
    primaryContainer = OceanLightCard,
    onPrimaryContainer = OceanLightNavyText,
    secondary = OceanLightSecondary,
    onSecondary = Color.White,
    secondaryContainer = OceanLightCard,
    onSecondaryContainer = OceanLightNavyText,
    tertiary = OceanLightAccent,
    onTertiary = Color.White,
    background = OceanLightBackground,
    onBackground = OceanLightNavyText,
    surface = OceanLightSurface,
    onSurface = OceanLightNavyText,
    surfaceVariant = OceanLightCard,
    onSurfaceVariant = OceanLightMutedText,
    outline = OceanLightBorder
)

@Composable
fun SubmarineHuntTheme(
    themeSetting: AppThemeSetting = AppThemeSetting.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeSetting) {
        AppThemeSetting.SYSTEM -> isSystemDark
        AppThemeSetting.LIGHT -> false
        AppThemeSetting.DARK -> true
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}