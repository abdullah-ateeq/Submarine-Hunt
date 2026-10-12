package com.example.submarinehunt.model

enum class GamePhase {
    PLAYING,
    FOUND,
    RESULT
}

/**
 * Information on an individual cell's searched state.
 */
data class SearchedCellInfo(
    val coordinate: Coordinate,
    val visualState: CellVisualState,
    val feedback: SearchFeedback,
    val distanceCategory: DistanceCategory,
    val searchIndex: Int
)

/**
 * Immutable state of a Submarine Hunt game round.
 *
 * NOTE: [foundCoordinate] is null until the submarine is found ([GamePhase.FOUND] or [GamePhase.RESULT]).
 * The hidden submarine position is NEVER exposed here before discovery.
 */
data class HuntGameState(
    val phase: GamePhase = GamePhase.PLAYING,
    val searches: Int = 0,
    val searchedCells: Set<Coordinate> = emptySet(),
    val cellVisualStates: Map<Coordinate, CellVisualState> = emptyMap(),
    val lastCoordinate: Coordinate? = null,
    val previousCoordinate: Coordinate? = null,
    val previousDistance: Int? = null,
    val lastFeedback: SearchFeedback? = null,
    val lastDistanceCategory: DistanceCategory? = null,
    val foundCoordinate: Coordinate? = null,
    val score: Int = 0,
    val accuracy: Double = 0.0,
    val searchHistory: List<SearchResult> = emptyList()
) {
    val isFound: Boolean
        get() = phase == GamePhase.FOUND || phase == GamePhase.RESULT
}
