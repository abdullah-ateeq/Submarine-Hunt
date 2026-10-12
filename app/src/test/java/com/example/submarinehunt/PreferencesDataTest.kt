package com.example.submarinehunt

import com.example.submarinehunt.data.AppThemeSetting
import com.example.submarinehunt.data.GameSettings
import com.example.submarinehunt.data.GameStatistics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PreferencesDataTest {

    @Test
    fun testGameStatisticsAverageCalculation() {
        val stats0 = GameStatistics(huntsCount = 0, totalSearches = 0)
        assertEquals(0.0, stats0.averageSearches, 0.001)
        assertEquals("0.0", stats0.formattedAverageSearches)

        val stats1 = GameStatistics(huntsCount = 4, totalSearches = 22)
        assertEquals(5.5, stats1.averageSearches, 0.001)
        assertEquals("5.5", stats1.formattedAverageSearches)

        val stats2 = GameStatistics(huntsCount = 3, totalSearches = 10)
        assertEquals(3.333, stats2.averageSearches, 0.01)
        assertEquals("3.3", stats2.formattedAverageSearches)
    }

    @Test
    fun testGameStatisticsBestSearchesAndScore() {
        var stats = GameStatistics()

        // First win: searches = 8, score = 325
        val win1Searches = 8
        val win1Score = 325
        stats = stats.copy(
            huntsCount = stats.huntsCount + 1,
            totalSearches = stats.totalSearches + win1Searches,
            bestSearches = if (stats.bestSearches == 0 || win1Searches < stats.bestSearches) win1Searches else stats.bestSearches,
            bestScore = maxOf(stats.bestScore, win1Score),
            currentStreak = stats.currentStreak + 1,
            bestStreak = maxOf(stats.bestStreak, stats.currentStreak + 1)
        )

        assertEquals(1, stats.huntsCount)
        assertEquals(8, stats.bestSearches)
        assertEquals(325, stats.bestScore)
        assertEquals(1, stats.currentStreak)
        assertEquals(1, stats.bestStreak)

        // Second win: searches = 4, score = 425
        val win2Searches = 4
        val win2Score = 425
        stats = stats.copy(
            huntsCount = stats.huntsCount + 1,
            totalSearches = stats.totalSearches + win2Searches,
            bestSearches = if (stats.bestSearches == 0 || win2Searches < stats.bestSearches) win2Searches else stats.bestSearches,
            bestScore = maxOf(stats.bestScore, win2Score),
            currentStreak = stats.currentStreak + 1,
            bestStreak = maxOf(stats.bestStreak, stats.currentStreak + 1)
        )

        assertEquals(2, stats.huntsCount)
        assertEquals(4, stats.bestSearches)
        assertEquals(425, stats.bestScore)
        assertEquals(2, stats.currentStreak)
        assertEquals(2, stats.bestStreak)

        // Third win with worse score: searches = 10, score = 275
        val win3Searches = 10
        val win3Score = 275
        stats = stats.copy(
            huntsCount = stats.huntsCount + 1,
            totalSearches = stats.totalSearches + win3Searches,
            bestSearches = if (stats.bestSearches == 0 || win3Searches < stats.bestSearches) win3Searches else stats.bestSearches,
            bestScore = maxOf(stats.bestScore, win3Score),
            currentStreak = stats.currentStreak + 1,
            bestStreak = maxOf(stats.bestStreak, stats.currentStreak + 1)
        )

        assertEquals(3, stats.huntsCount)
        assertEquals(4, stats.bestSearches) // Remains 4 (best is minimum)
        assertEquals(425, stats.bestScore) // Remains 425 (best is maximum)
        assertEquals(3, stats.currentStreak)
        assertEquals(3, stats.bestStreak)
    }

    @Test
    fun testDefaultSettings() {
        val settings = GameSettings()
        assertEquals(AppThemeSetting.SYSTEM, settings.theme)
        assertTrue(settings.dynamicColor)
        assertTrue(settings.soundEnabled)
        assertTrue(settings.hapticEnabled)
        assertTrue(settings.animationsEnabled)
        assertTrue(settings.confirmNewHunt)
        assertTrue(settings.showCoordinateLabels)
    }
}
