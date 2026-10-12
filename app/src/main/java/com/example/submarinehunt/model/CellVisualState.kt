package com.example.submarinehunt.model

/**
 * Visual presentation state of an individual cell on the grid.
 */
enum class CellVisualState {
    UNKNOWN,
    SEARCHED_FAR,
    SEARCHED_CLOSER,
    SEARCHED_FARTHER,
    SEARCHED_VERY_CLOSE,
    FOUND
}
