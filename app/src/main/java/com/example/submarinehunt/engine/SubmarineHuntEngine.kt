package com.example.submarinehunt.engine

import com.example.submarinehunt.model.CellVisualState
import com.example.submarinehunt.model.Coordinate
import com.example.submarinehunt.model.DistanceCategory
import com.example.submarinehunt.model.GamePhase
import com.example.submarinehunt.model.HuntGameState
import com.example.submarinehunt.model.SearchFeedback
import com.example.submarinehunt.model.SearchResult
import com.example.submarinehunt.model.manhattanDistance
import kotlin.random.Random

/**
 * Result of attempting to search a cell.
 */
sealed class SearchAttemptResult {
    data class Success(val result: SearchResult, val newState: HuntGameState) : SearchAttemptResult()
    object AlreadySearched : SearchAttemptResult()
    object GameOver : SearchAttemptResult()
    data class InvalidCoordinate(val message: String) : SearchAttemptResult()
}

/**
 * Engine responsible for Submarine Hunt gameplay rules and state transitions.
 *
 * All coordinates and distance rules:
 * - 7x7 board
 * - Manhattan distance: |row1 - row2| + |col1 - col2|
 * - Strictly keeps the submarine hidden until discovery.
 */
class SubmarineHuntEngine(
    initialSubmarine: Coordinate? = null,
    private val random: Random = Random.Default
) {
    // Hidden submarine position. Private and never exposed directly while playing.
    private var hiddenSubmarine: Coordinate = initialSubmarine ?: generateRandomPosition()

    private var _state: HuntGameState = HuntGameState()
    val state: HuntGameState
        get() = _state

    /**
     * Generates a fair uniform random coordinate within 0..6 for row and column.
     */
    private fun generateRandomPosition(): Coordinate {
        return Coordinate(
            row = random.nextInt(Coordinate.BOARD_SIZE),
            column = random.nextInt(Coordinate.BOARD_SIZE)
        )
    }

    /**
     * Resets the game engine for a new round.
     * Optionally takes a fixed [newSubmarine] coordinate (primarily for testing).
     */
    fun reset(newSubmarine: Coordinate? = null) {
        hiddenSubmarine = newSubmarine ?: generateRandomPosition()
        _state = HuntGameState()
    }

    /**
     * Processes a search at [coordinate].
     */
    fun search(coordinate: Coordinate): SearchAttemptResult {
        if (_state.isFound) {
            return SearchAttemptResult.GameOver
        }

        if (_state.searchedCells.contains(coordinate)) {
            return SearchAttemptResult.AlreadySearched
        }

        val distance = manhattanDistance(coordinate, hiddenSubmarine)
        val distanceCat = DistanceCategory.fromDistance(distance)
        val isFound = (distance == 0)

        val newSearches = _state.searches + 1
        val prevDistance = _state.previousDistance

        val feedback = determineFeedback(
            distance = distance,
            previousDistance = prevDistance,
            isFound = isFound
        )

        val visualState = determineVisualState(
            distance = distance,
            previousDistance = prevDistance,
            feedback = feedback,
            isFound = isFound
        )

        val searchResult = SearchResult(
            coordinate = coordinate,
            distance = distance,
            feedback = feedback,
            distanceCategory = distanceCat,
            found = isFound
        )

        val newScore = calculateScore(newSearches)
        val newAccuracy = calculateAccuracy(newSearches)

        val updatedCells = _state.searchedCells + coordinate
        val updatedVisualStates = _state.cellVisualStates + (coordinate to visualState)
        val updatedHistory = listOf(searchResult) + _state.searchHistory

        val nextPhase = if (isFound) GamePhase.FOUND else GamePhase.PLAYING
        val revealedCoordinate = if (isFound) hiddenSubmarine else null

        _state = HuntGameState(
            phase = nextPhase,
            searches = newSearches,
            searchedCells = updatedCells,
            cellVisualStates = updatedVisualStates,
            lastCoordinate = coordinate,
            previousCoordinate = _state.lastCoordinate,
            previousDistance = distance,
            lastFeedback = feedback,
            lastDistanceCategory = distanceCat,
            foundCoordinate = revealedCoordinate,
            score = newScore,
            accuracy = newAccuracy,
            searchHistory = updatedHistory
        )

        return SearchAttemptResult.Success(searchResult, _state)
    }

    /**
     * Determines the search feedback based on distance and previous search comparison.
     */
    fun determineFeedback(
        distance: Int,
        previousDistance: Int?,
        isFound: Boolean
    ): SearchFeedback {
        if (isFound || distance == 0) {
            return SearchFeedback.FOUND
        }

        // First search (no previous reference)
        if (previousDistance == null) {
            return when {
                distance in 1..2 -> SearchFeedback.VERY_CLOSE
                distance in 3..4 -> SearchFeedback.CLOSE
                else -> SearchFeedback.FAR
            }
        }

        // Subsequent searches compare current distance with previous distance
        return when {
            distance < previousDistance -> SearchFeedback.CLOSER
            distance > previousDistance -> SearchFeedback.FARTHER
            else -> SearchFeedback.SAME_DISTANCE
        }
    }

    /**
     * Determines cell visual state marker.
     */
    private fun determineVisualState(
        distance: Int,
        previousDistance: Int?,
        feedback: SearchFeedback,
        isFound: Boolean
    ): CellVisualState {
        if (isFound) return CellVisualState.FOUND
        if (distance in 1..2) return CellVisualState.SEARCHED_VERY_CLOSE

        return when (feedback) {
            SearchFeedback.CLOSER -> CellVisualState.SEARCHED_CLOSER
            SearchFeedback.FARTHER -> CellVisualState.SEARCHED_FARTHER
            SearchFeedback.VERY_CLOSE -> CellVisualState.SEARCHED_VERY_CLOSE
            SearchFeedback.CLOSE -> CellVisualState.SEARCHED_CLOSER
            SearchFeedback.SAME_DISTANCE -> {
                if (distance >= 5) CellVisualState.SEARCHED_FAR else CellVisualState.SEARCHED_CLOSER
            }
            SearchFeedback.FAR -> CellVisualState.SEARCHED_FAR
            SearchFeedback.FOUND -> CellVisualState.FOUND
        }
    }

    /**
     * Transitions game phase to RESULT after reveal animation finishes.
     */
    fun transitionToResult() {
        if (_state.phase == GamePhase.FOUND) {
            _state = _state.copy(phase = GamePhase.RESULT)
        }
    }

    /**
     * Internal/testing access to the hidden submarine position.
     * MUST NOT be used by production UI.
     */
    internal fun getSubmarinePositionForTesting(): Coordinate {
        return hiddenSubmarine
    }

    companion object {
        /**
         * Calculates score: max(50, 500 - ((searches - 1) * 25))
         */
        fun calculateScore(searches: Int): Int {
            if (searches <= 0) return 500
            val penalty = (searches - 1) * 25
            return maxOf(50, 500 - penalty)
        }

        /**
         * Calculates accuracy percentage: (1.0 / searches) * 100
         */
        fun calculateAccuracy(searches: Int): Double {
            return if (searches > 0) (1.0 / searches) * 100.0 else 0.0
        }

        /**
         * Dynamic message based on search count.
         */
        fun getResultMessage(searches: Int): String {
            return when {
                searches <= 1 -> "Perfect hunt."
                searches in 2..5 -> "Excellent tracking."
                searches in 6..10 -> "Great hunt."
                searches in 11..20 -> "Target located."
                else -> "Persistence paid off."
            }
        }
    }
}
