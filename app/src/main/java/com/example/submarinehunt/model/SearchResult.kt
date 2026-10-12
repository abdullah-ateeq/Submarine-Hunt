package com.example.submarinehunt.model

/**
 * Result data produced when a player searches a coordinate.
 *
 * NOTE: To prevent accidental UI cheating, this class does NOT expose the hidden submarine's
 * true location.
 */
data class SearchResult(
    val coordinate: Coordinate,
    val distance: Int,
    val feedback: SearchFeedback,
    val distanceCategory: DistanceCategory,
    val found: Boolean
)
