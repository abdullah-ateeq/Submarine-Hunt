package com.example.submarinehunt.model

/**
 * Distance categorization for search results.
 * - FOUND: Distance = 0
 * - VERY_CLOSE: Distance 1..2
 * - CLOSE: Distance 3..4
 * - FAR: Distance >= 5
 */
enum class DistanceCategory(val displayText: String) {
    FOUND("FOUND"),
    VERY_CLOSE("VERY CLOSE"),
    CLOSE("CLOSE"),
    FAR("FAR");

    companion object {
        fun fromDistance(distance: Int): DistanceCategory {
            return when {
                distance == 0 -> FOUND
                distance in 1..2 -> VERY_CLOSE
                distance in 3..4 -> CLOSE
                else -> FAR
            }
        }
    }
}
