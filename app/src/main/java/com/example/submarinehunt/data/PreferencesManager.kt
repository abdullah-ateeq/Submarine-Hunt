package com.example.submarinehunt.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * App theme preference options.
 */
enum class AppThemeSetting {
    SYSTEM,
    LIGHT,
    DARK
}

/**
 * Data class representing all game settings.
 */
data class GameSettings(
    val theme: AppThemeSetting = AppThemeSetting.SYSTEM,
    val dynamicColor: Boolean = true,
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val animationsEnabled: Boolean = true,
    val confirmNewHunt: Boolean = true,
    val showCoordinateLabels: Boolean = true
)

/**
 * Data class representing user game statistics.
 */
data class GameStatistics(
    val huntsCount: Int = 0,
    val bestSearches: Int = 0, // 0 indicates no recorded hunt yet
    val bestScore: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalSearches: Int = 0
) {
    val averageSearches: Double
        get() = if (huntsCount > 0) totalSearches.toDouble() / huntsCount else 0.0

    val formattedAverageSearches: String
        get() = String.format(java.util.Locale.US, "%.1f", averageSearches)
}

/**
 * Manages persistence of settings and statistics using Android SharedPreferences.
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<GameSettings> = _settingsFlow.asStateFlow()

    private val _statisticsFlow = MutableStateFlow(loadStatistics())
    val statisticsFlow: StateFlow<GameStatistics> = _statisticsFlow.asStateFlow()

    // Current session streak (resets on app restart as specified in prompt, but best streak persists)
    private var inMemoryCurrentStreak: Int = 0

    init {
        // Refresh with in-memory streak = 0 on launch
        _statisticsFlow.value = _statisticsFlow.value.copy(currentStreak = inMemoryCurrentStreak)
    }

    private fun loadSettings(): GameSettings {
        val themeOrdinal = prefs.getInt(KEY_THEME, AppThemeSetting.SYSTEM.ordinal)
        val theme = AppThemeSetting.entries.getOrElse(themeOrdinal) { AppThemeSetting.SYSTEM }
        return GameSettings(
            theme = theme,
            dynamicColor = prefs.getBoolean(KEY_DYNAMIC_COLOR, true),
            soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true),
            hapticEnabled = prefs.getBoolean(KEY_HAPTIC_ENABLED, true),
            animationsEnabled = prefs.getBoolean(KEY_ANIMATIONS_ENABLED, true),
            confirmNewHunt = prefs.getBoolean(KEY_CONFIRM_NEW_HUNT, true),
            showCoordinateLabels = prefs.getBoolean(KEY_SHOW_COORDINATE_LABELS, true)
        )
    }

    private fun loadStatistics(): GameStatistics {
        return GameStatistics(
            huntsCount = prefs.getInt(KEY_HUNTS_COUNT, 0),
            bestSearches = prefs.getInt(KEY_BEST_SEARCHES, 0),
            bestScore = prefs.getInt(KEY_BEST_SCORE, 0),
            currentStreak = inMemoryCurrentStreak,
            bestStreak = prefs.getInt(KEY_BEST_STREAK, 0),
            totalSearches = prefs.getInt(KEY_TOTAL_SEARCHES, 0)
        )
    }

    fun updateTheme(theme: AppThemeSetting) {
        prefs.edit().putInt(KEY_THEME, theme.ordinal).apply()
        _settingsFlow.value = _settingsFlow.value.copy(theme = theme)
    }

    fun updateDynamicColor(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(dynamicColor = enabled)
    }

    fun updateSound(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(soundEnabled = enabled)
    }

    fun updateHaptic(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC_ENABLED, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(hapticEnabled = enabled)
    }

    fun updateAnimations(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ANIMATIONS_ENABLED, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(animationsEnabled = enabled)
    }

    fun updateConfirmNewHunt(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_CONFIRM_NEW_HUNT, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(confirmNewHunt = enabled)
    }

    fun updateShowCoordinateLabels(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_COORDINATE_LABELS, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(showCoordinateLabels = enabled)
    }

    /**
     * Records a completed round win.
     */
    fun recordWin(searches: Int, score: Int) {
        val currentStats = _statisticsFlow.value
        val newHuntsCount = currentStats.huntsCount + 1
        val newTotalSearches = currentStats.totalSearches + searches

        val newBestSearches = if (currentStats.bestSearches == 0 || searches < currentStats.bestSearches) {
            searches
        } else {
            currentStats.bestSearches
        }

        val newBestScore = maxOf(currentStats.bestScore, score)

        inMemoryCurrentStreak += 1
        val newBestStreak = maxOf(currentStats.bestStreak, inMemoryCurrentStreak)

        prefs.edit()
            .putInt(KEY_HUNTS_COUNT, newHuntsCount)
            .putInt(KEY_TOTAL_SEARCHES, newTotalSearches)
            .putInt(KEY_BEST_SEARCHES, newBestSearches)
            .putInt(KEY_BEST_SCORE, newBestScore)
            .putInt(KEY_BEST_STREAK, newBestStreak)
            .apply()

        _statisticsFlow.value = GameStatistics(
            huntsCount = newHuntsCount,
            bestSearches = newBestSearches,
            bestScore = newBestScore,
            currentStreak = inMemoryCurrentStreak,
            bestStreak = newBestStreak,
            totalSearches = newTotalSearches
        )
    }

    /**
     * Resets statistics to default values.
     */
    fun resetStatistics() {
        inMemoryCurrentStreak = 0
        prefs.edit()
            .remove(KEY_HUNTS_COUNT)
            .remove(KEY_TOTAL_SEARCHES)
            .remove(KEY_BEST_SEARCHES)
            .remove(KEY_BEST_SCORE)
            .remove(KEY_BEST_STREAK)
            .apply()

        _statisticsFlow.value = GameStatistics()
    }

    /**
     * Resets settings to default values.
     */
    fun resetSettings() {
        prefs.edit()
            .remove(KEY_THEME)
            .remove(KEY_DYNAMIC_COLOR)
            .remove(KEY_SOUND_ENABLED)
            .remove(KEY_HAPTIC_ENABLED)
            .remove(KEY_ANIMATIONS_ENABLED)
            .remove(KEY_CONFIRM_NEW_HUNT)
            .remove(KEY_SHOW_COORDINATE_LABELS)
            .apply()

        _settingsFlow.value = GameSettings()
    }

    companion object {
        private const val PREFS_NAME = "submarine_hunt_prefs"

        private const val KEY_THEME = "pref_theme"
        private const val KEY_DYNAMIC_COLOR = "pref_dynamic_color"
        private const val KEY_SOUND_ENABLED = "pref_sound_enabled"
        private const val KEY_HAPTIC_ENABLED = "pref_haptic_enabled"
        private const val KEY_ANIMATIONS_ENABLED = "pref_animations_enabled"
        private const val KEY_CONFIRM_NEW_HUNT = "pref_confirm_new_hunt"
        private const val KEY_SHOW_COORDINATE_LABELS = "pref_show_coordinate_labels"

        private const val KEY_HUNTS_COUNT = "stat_hunts_count"
        private const val KEY_TOTAL_SEARCHES = "stat_total_searches"
        private const val KEY_BEST_SEARCHES = "stat_best_searches"
        private const val KEY_BEST_SCORE = "stat_best_score"
        private const val KEY_BEST_STREAK = "stat_best_streak"
    }
}
