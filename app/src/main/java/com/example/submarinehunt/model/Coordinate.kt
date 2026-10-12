package com.example.submarinehunt.model

import kotlin.math.abs

/**
 * Represents a coordinate on the 7x7 Submarine Hunt grid.
 *
 * @property row 0-indexed row (0..6), displayed as rows 1..7.
 * @property column 0-indexed column (0..6), displayed as columns A..G.
 */
data class Coordinate(
    val row: Int,
    val column: Int
) {
    init {
        require(row in 0 until BOARD_SIZE) { "Row must be between 0 and ${BOARD_SIZE - 1}, but was $row" }
        require(column in 0 until BOARD_SIZE) { "Column must be between 0 and ${BOARD_SIZE - 1}, but was $column" }
    }

    /**
     * Human-readable label for this coordinate, e.g. "A1", "C4", "G7".
     */
    val label: String
        get() = coordinateLabel(row, column)

    companion object {
        const val BOARD_SIZE = 7

        /**
         * Converts row and column indexes into a label (e.g. A1, C4, G7).
         */
        fun coordinateLabel(row: Int, column: Int): String {
            require(row in 0 until BOARD_SIZE) { "Row must be between 0 and ${BOARD_SIZE - 1}" }
            require(column in 0 until BOARD_SIZE) { "Column must be between 0 and ${BOARD_SIZE - 1}" }
            val colChar = ('A'.code + column).toChar()
            val rowNum = row + 1
            return "$colChar$rowNum"
        }

        /**
         * Calculates the Manhattan distance between two coordinates.
         * Formula: abs(first.row - second.row) + abs(first.column - second.column)
         */
        fun manhattanDistance(first: Coordinate, second: Coordinate): Int {
            return abs(first.row - second.row) + abs(first.column - second.column)
        }

        /**
         * Parses a coordinate label like "A1" or "D4" into a Coordinate.
         */
        fun fromLabel(label: String): Coordinate? {
            if (label.length != 2) return null
            val colChar = label[0].uppercaseChar()
            val rowChar = label[1]
            val column = colChar.code - 'A'.code
            val row = rowChar.digitToIntOrNull()?.minus(1) ?: return null
            return if (column in 0 until BOARD_SIZE && row in 0 until BOARD_SIZE) {
                Coordinate(row = row, column = column)
            } else {
                null
            }
        }
    }
}

/**
 * Top-level convenience function for coordinateLabel as required by specification.
 */
fun coordinateLabel(row: Int, column: Int): String = Coordinate.coordinateLabel(row, column)

/**
 * Top-level convenience function for manhattanDistance as required by specification.
 */
fun manhattanDistance(first: Coordinate, second: Coordinate): Int = Coordinate.manhattanDistance(first, second)
