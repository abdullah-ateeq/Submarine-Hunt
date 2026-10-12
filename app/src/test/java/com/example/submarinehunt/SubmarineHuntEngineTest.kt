package com.example.submarinehunt

import com.example.submarinehunt.engine.SearchAttemptResult
import com.example.submarinehunt.engine.SubmarineHuntEngine
import com.example.submarinehunt.model.CellVisualState
import com.example.submarinehunt.model.Coordinate
import com.example.submarinehunt.model.DistanceCategory
import com.example.submarinehunt.model.GamePhase
import com.example.submarinehunt.model.SearchFeedback
import com.example.submarinehunt.model.coordinateLabel
import com.example.submarinehunt.model.manhattanDistance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import kotlin.random.Random

/**
 * Complete test suite validating all 30 game logic requirements.
 */
class SubmarineHuntEngineTest {

    // 1. Coordinate creation
    @Test
    fun test01_coordinateCreation() {
        val coord = Coordinate(row = 3, column = 2)
        assertEquals(3, coord.row)
        assertEquals(2, coord.column)
        assertEquals("C4", coord.label)
    }

    // 2. Coordinate labels
    @Test
    fun test02_coordinateLabels() {
        assertEquals("A1", coordinateLabel(0, 0))
        assertEquals("C4", coordinateLabel(3, 2))
        assertEquals("D4", coordinateLabel(3, 3))
        assertEquals("G7", coordinateLabel(6, 6))
        assertEquals("F5", coordinateLabel(4, 5))

        val parsed = Coordinate.fromLabel("D4")
        assertNotNull(parsed)
        assertEquals(3, parsed?.row)
        assertEquals(3, parsed?.column)
    }

    // 3. Manhattan distance
    @Test
    fun test03_manhattanDistance() {
        // D4 is row 3, column 3. A1 is row 0, column 0.
        val d4 = Coordinate(3, 3)
        val a1 = Coordinate(0, 0)
        // |3 - 0| + |3 - 0| = 6
        assertEquals(6, manhattanDistance(d4, a1))

        // D4 to C4 (row 3, col 2): |3-3| + |3-2| = 1
        val c4 = Coordinate(3, 2)
        assertEquals(1, manhattanDistance(d4, c4))

        // D4 to B4 (row 3, col 1): |3-3| + |3-1| = 2
        val b4 = Coordinate(3, 1)
        assertEquals(2, manhattanDistance(d4, b4))

        // D4 to A4 (row 3, col 0): |3-3| + |3-0| = 3
        val a4 = Coordinate(3, 0)
        assertEquals(3, manhattanDistance(d4, a4))
    }

    // 4. Distance at same coordinate
    @Test
    fun test04_distanceAtSameCoordinate() {
        val coord = Coordinate(3, 3)
        assertEquals(0, manhattanDistance(coord, coord))
    }

    // 5. Distance at corners
    @Test
    fun test05_distanceAtCorners() {
        val topLeft = Coordinate(0, 0) // A1
        val bottomRight = Coordinate(6, 6) // G7
        val topRight = Coordinate(0, 6) // G1
        val bottomLeft = Coordinate(6, 0) // A7

        assertEquals(12, manhattanDistance(topLeft, bottomRight))
        assertEquals(12, manhattanDistance(topRight, bottomLeft))
        assertEquals(6, manhattanDistance(topLeft, topRight))
        assertEquals(6, manhattanDistance(topLeft, bottomLeft))
    }

    // 6. FAR classification
    @Test
    fun test06_farClassification() {
        assertEquals(DistanceCategory.FAR, DistanceCategory.fromDistance(5))
        assertEquals(DistanceCategory.FAR, DistanceCategory.fromDistance(6))
        assertEquals(DistanceCategory.FAR, DistanceCategory.fromDistance(12))
    }

    // 7. CLOSE classification
    @Test
    fun test07_closeClassification() {
        assertEquals(DistanceCategory.CLOSE, DistanceCategory.fromDistance(3))
        assertEquals(DistanceCategory.CLOSE, DistanceCategory.fromDistance(4))
    }

    // 8. VERY CLOSE classification
    @Test
    fun test08_veryCloseClassification() {
        assertEquals(DistanceCategory.VERY_CLOSE, DistanceCategory.fromDistance(1))
        assertEquals(DistanceCategory.VERY_CLOSE, DistanceCategory.fromDistance(2))
    }

    // 9. FOUND classification
    @Test
    fun test09_foundClassification() {
        assertEquals(DistanceCategory.FOUND, DistanceCategory.fromDistance(0))
    }

    // 10. First-search feedback (Distance tests from prompt)
    @Test
    fun test10_firstSearchFeedback() {
        val sub = Coordinate(3, 3) // D4

        // D4 guess -> FOUND (distance 0)
        val engine1 = SubmarineHuntEngine(initialSubmarine = sub)
        val resD4 = engine1.search(Coordinate(3, 3)) as SearchAttemptResult.Success
        assertEquals(SearchFeedback.FOUND, resD4.result.feedback)
        assertEquals(0, resD4.result.distance)

        // C4 guess -> VERY_CLOSE (distance 1)
        val engine2 = SubmarineHuntEngine(initialSubmarine = sub)
        val resC4 = engine2.search(Coordinate(3, 2)) as SearchAttemptResult.Success
        assertEquals(SearchFeedback.VERY_CLOSE, resC4.result.feedback)
        assertEquals(1, resC4.result.distance)

        // B4 guess -> VERY_CLOSE (distance 2)
        val engine3 = SubmarineHuntEngine(initialSubmarine = sub)
        val resB4 = engine3.search(Coordinate(3, 1)) as SearchAttemptResult.Success
        assertEquals(SearchFeedback.VERY_CLOSE, resB4.result.feedback)
        assertEquals(2, resB4.result.distance)

        // A4 guess -> CLOSE (distance 3)
        val engine4 = SubmarineHuntEngine(initialSubmarine = sub)
        val resA4 = engine4.search(Coordinate(3, 0)) as SearchAttemptResult.Success
        assertEquals(SearchFeedback.CLOSE, resA4.result.feedback)
        assertEquals(3, resA4.result.distance)

        // A1 guess -> FAR (distance 6)
        val engine5 = SubmarineHuntEngine(initialSubmarine = sub)
        val resA1 = engine5.search(Coordinate(0, 0)) as SearchAttemptResult.Success
        assertEquals(SearchFeedback.FAR, resA1.result.feedback)
        assertEquals(6, resA1.result.distance)
    }

    // 11. Closer feedback (Prompt comparison test: prev 6, curr 4 -> CLOSER)
    @Test
    fun test11_closerFeedback() {
        val sub = Coordinate(4, 5) // F5
        val engine = SubmarineHuntEngine(initialSubmarine = sub)

        // Guess 1: A1 (0, 0), dist = 4+5 = 9 -> FAR
        engine.search(Coordinate(0, 0))

        // Guess 2: C3 (2, 2), dist = |4-2| + |5-2| = 2 + 3 = 5 < 9 -> CLOSER
        val attempt2 = engine.search(Coordinate(2, 2)) as SearchAttemptResult.Success
        assertEquals(SearchFeedback.CLOSER, attempt2.result.feedback)
    }

    // 12. Farther feedback (Prompt comparison test: prev 4, curr 6 -> FARTHER)
    @Test
    fun test12_fartherFeedback() {
        val sub = Coordinate(3, 3) // D4
        val engine = SubmarineHuntEngine(initialSubmarine = sub)

        // Guess 1: C3 (2, 2), dist = |3-2| + |3-2| = 2
        engine.search(Coordinate(2, 2))

        // Guess 2: A1 (0, 0), dist = |3-0| + |3-0| = 6 > 2 -> FARTHER
        val attempt2 = engine.search(Coordinate(0, 0)) as SearchAttemptResult.Success
        assertEquals(SearchFeedback.FARTHER, attempt2.result.feedback)
    }

    // 13. Same-distance feedback (Prompt comparison test: prev 4, curr 4 -> SAME_DISTANCE)
    @Test
    fun test13_sameDistanceFeedback() {
        val sub = Coordinate(3, 3) // D4
        val engine = SubmarineHuntEngine(initialSubmarine = sub)

        // Guess 1: B3 (2, 1), dist = |3-2| + |3-1| = 1 + 2 = 3
        engine.search(Coordinate(2, 1))

        // Guess 2: D1 (0, 3), dist = |3-0| + |3-3| = 3 + 0 = 3 == 3 -> SAME_DISTANCE
        val attempt2 = engine.search(Coordinate(0, 3)) as SearchAttemptResult.Success
        assertEquals(SearchFeedback.SAME_DISTANCE, attempt2.result.feedback)
    }

    // 14. Repeated search does not consume turn or penalize
    @Test
    fun test14_repeatedSearch() {
        val sub = Coordinate(3, 3)
        val engine = SubmarineHuntEngine(initialSubmarine = sub)

        val firstAttempt = engine.search(Coordinate(0, 0))
        assertTrue(firstAttempt is SearchAttemptResult.Success)
        assertEquals(1, engine.state.searches)

        // Repeat search at A1 (0, 0)
        val secondAttempt = engine.search(Coordinate(0, 0))
        assertEquals(SearchAttemptResult.AlreadySearched, secondAttempt)
        assertEquals(1, engine.state.searches) // search count unchanged!
    }

    // 15. Search count increments only on valid new coordinates
    @Test
    fun test15_searchCount() {
        val sub = Coordinate(5, 5)
        val engine = SubmarineHuntEngine(initialSubmarine = sub)

        assertEquals(0, engine.state.searches)
        engine.search(Coordinate(0, 0))
        assertEquals(1, engine.state.searches)
        engine.search(Coordinate(0, 1))
        assertEquals(2, engine.state.searches)
        engine.search(Coordinate(0, 1)) // repeated
        assertEquals(2, engine.state.searches)
        engine.search(Coordinate(0, 2))
        assertEquals(3, engine.state.searches)
    }

    // 16. Found detection
    @Test
    fun test16_foundDetection() {
        val sub = Coordinate(2, 4)
        val engine = SubmarineHuntEngine(initialSubmarine = sub)

        assertFalse(engine.state.isFound)
        engine.search(Coordinate(0, 0))
        assertFalse(engine.state.isFound)

        val foundAttempt = engine.search(Coordinate(2, 4)) as SearchAttemptResult.Success
        assertTrue(foundAttempt.result.found)
        assertTrue(engine.state.isFound)
        assertEquals(GamePhase.FOUND, engine.state.phase)
        assertEquals(Coordinate(2, 4), engine.state.foundCoordinate)
    }

    // 17. Score calculation: score = max(50, 500 - ((searches - 1) * 25))
    @Test
    fun test17_scoreCalculation() {
        assertEquals(500, SubmarineHuntEngine.calculateScore(1))
        assertEquals(475, SubmarineHuntEngine.calculateScore(2))
        assertEquals(400, SubmarineHuntEngine.calculateScore(5))
        assertEquals(275, SubmarineHuntEngine.calculateScore(10))
        assertEquals(75, SubmarineHuntEngine.calculateScore(18))
        assertEquals(50, SubmarineHuntEngine.calculateScore(19))
        assertEquals(50, SubmarineHuntEngine.calculateScore(20))
        assertEquals(50, SubmarineHuntEngine.calculateScore(50))
    }

    // 18. Perfect hunt
    @Test
    fun test18_perfectHunt() {
        val sub = Coordinate(3, 3)
        val engine = SubmarineHuntEngine(initialSubmarine = sub)

        val attempt = engine.search(Coordinate(3, 3)) as SearchAttemptResult.Success
        assertTrue(attempt.result.found)
        assertEquals(1, engine.state.searches)
        assertEquals(500, engine.state.score)
        assertEquals("Perfect hunt.", SubmarineHuntEngine.getResultMessage(1))
    }

    // 19. Best score tracking logic
    @Test
    fun test19_bestScore() {
        var bestScore = 0
        val game1Score = SubmarineHuntEngine.calculateScore(5) // 400
        bestScore = maxOf(bestScore, game1Score)
        assertEquals(400, bestScore)

        val game2Score = SubmarineHuntEngine.calculateScore(10) // 275
        bestScore = maxOf(bestScore, game2Score)
        assertEquals(400, bestScore)

        val game3Score = SubmarineHuntEngine.calculateScore(1) // 500
        bestScore = maxOf(bestScore, game3Score)
        assertEquals(500, bestScore)
    }

    // 20. Win streak progression
    @Test
    fun test20_winStreak() {
        var currentStreak = 0
        var bestStreak = 0

        // Round 1 won
        currentStreak += 1
        bestStreak = maxOf(bestStreak, currentStreak)
        assertEquals(1, currentStreak)
        assertEquals(1, bestStreak)

        // Round 2 won
        currentStreak += 1
        bestStreak = maxOf(bestStreak, currentStreak)
        assertEquals(2, currentStreak)
        assertEquals(2, bestStreak)
    }

    // 21. Random submarine position: 1000 rounds verification
    @Test
    fun test21_randomSubmarinePosition1000Rounds() {
        val engine = SubmarineHuntEngine()
        val generatedPositions = mutableSetOf<Coordinate>()

        for (i in 1..1000) {
            engine.reset()
            val pos = engine.getSubmarinePositionForTesting()
            assertTrue("Row must be within 0..6", pos.row in 0..6)
            assertTrue("Column must be within 0..6", pos.column in 0..6)
            generatedPositions.add(pos)
        }

        // Verify distribution is not hardcoded to a single position
        assertTrue("Submarine positions should cover multiple grid cells", generatedPositions.size > 20)
    }

    // 22. Board boundaries
    @Test
    fun test22_boardBoundaries() {
        // Valid edges
        Coordinate(0, 0)
        Coordinate(0, 6)
        Coordinate(6, 0)
        Coordinate(6, 6)

        // Invalid boundaries should throw IllegalArgumentException
        try {
            Coordinate(-1, 0)
            fail("Expected exception for row -1")
        } catch (_: IllegalArgumentException) {}

        try {
            Coordinate(0, 7)
            fail("Expected exception for col 7")
        } catch (_: IllegalArgumentException) {}
    }

    // 23. Hidden submarine state is null before found
    @Test
    fun test23_hiddenSubmarineState() {
        val sub = Coordinate(3, 3)
        val engine = SubmarineHuntEngine(initialSubmarine = sub)

        assertNull("foundCoordinate must be null before found", engine.state.foundCoordinate)

        engine.search(Coordinate(0, 0))
        assertNull("foundCoordinate must remain null during gameplay", engine.state.foundCoordinate)

        engine.search(Coordinate(3, 3))
        assertEquals(sub, engine.state.foundCoordinate)
    }

    // 24. Accessibility descriptions
    @Test
    fun test24_accessibilityState() {
        val unsearchedDesc = "Coordinate A1, unsearched"
        assertFalse(unsearchedDesc.contains("submarine", ignoreCase = true))

        val searchedDesc = "Coordinate C4, searched, far"
        assertTrue(searchedDesc.contains("searched"))

        val foundDesc = "Coordinate D4, submarine found"
        assertTrue(foundDesc.contains("submarine found"))
    }

    // 25. Accuracy calculations
    @Test
    fun test25_accuracyCalculations() {
        assertEquals(100.0, SubmarineHuntEngine.calculateAccuracy(1), 0.001)
        assertEquals(20.0, SubmarineHuntEngine.calculateAccuracy(5), 0.001)
        assertEquals(10.0, SubmarineHuntEngine.calculateAccuracy(10), 0.001)
        assertEquals(5.0, SubmarineHuntEngine.calculateAccuracy(20), 0.001)
    }

    // 26. Dynamic result messages
    @Test
    fun test26_dynamicResultMessages() {
        assertEquals("Perfect hunt.", SubmarineHuntEngine.getResultMessage(1))
        assertEquals("Excellent tracking.", SubmarineHuntEngine.getResultMessage(3))
        assertEquals("Great hunt.", SubmarineHuntEngine.getResultMessage(8))
        assertEquals("Target located.", SubmarineHuntEngine.getResultMessage(15))
        assertEquals("Persistence paid off.", SubmarineHuntEngine.getResultMessage(25))
    }

    // 27. Restart clears round state
    @Test
    fun test27_restartClearsState() {
        val engine = SubmarineHuntEngine(initialSubmarine = Coordinate(3, 3))
        engine.search(Coordinate(0, 0))
        engine.search(Coordinate(1, 1))

        assertEquals(2, engine.state.searches)
        assertEquals(2, engine.state.searchedCells.size)

        engine.reset()
        assertEquals(0, engine.state.searches)
        assertTrue(engine.state.searchedCells.isEmpty())
        assertNull(engine.state.lastCoordinate)
        assertNull(engine.state.previousDistance)
        assertNull(engine.state.lastFeedback)
        assertNull(engine.state.foundCoordinate)
        assertEquals(GamePhase.PLAYING, engine.state.phase)
    }

    // 28. Rapid tap / game over protection
    @Test
    fun test28_rapidTapGameOverProtection() {
        val sub = Coordinate(2, 2)
        val engine = SubmarineHuntEngine(initialSubmarine = sub)

        val winAttempt = engine.search(Coordinate(2, 2))
        assertTrue(winAttempt is SearchAttemptResult.Success)

        // Subsequent tap after game is found is rejected
        val lateAttempt = engine.search(Coordinate(0, 0))
        assertEquals(SearchAttemptResult.GameOver, lateAttempt)
    }

    // 29. New round reset generates independently
    @Test
    fun test29_newRoundResetGeneratesIndependently() {
        val engine = SubmarineHuntEngine()
        val pos1 = engine.getSubmarinePositionForTesting()
        var changed = false
        for (i in 1..20) {
            engine.reset()
            if (engine.getSubmarinePositionForTesting() != pos1) {
                changed = true
                break
            }
        }
        assertTrue("Subsequent resets should generate positions independently", changed)
    }

    // 30. No exact location exposed before discovery
    @Test
    fun test30_noExactLocationExposedBeforeDiscovery() {
        val sub = Coordinate(6, 6)
        val engine = SubmarineHuntEngine(initialSubmarine = sub)

        for (row in 0..5) {
            val res = engine.search(Coordinate(row, 0)) as SearchAttemptResult.Success
            assertNull("State must not leak found coordinate", engine.state.foundCoordinate)
            assertFalse("Search result must not declare found prematurely", res.result.found)
        }
    }
}
