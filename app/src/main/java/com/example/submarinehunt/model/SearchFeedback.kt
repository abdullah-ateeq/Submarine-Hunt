package com.example.submarinehunt.model

/**
 * Primary feedback states indicating player's search result or progress compared to previous search.
 */
enum class SearchFeedback(
    val title: String,
    val description: String
) {
    FAR(
        title = "FAR",
        description = "No strong signal. Keep scanning the ocean grid."
    ),
    CLOSE(
        title = "CLOSE",
        description = "Closing in on the target signal."
    ),
    VERY_CLOSE(
        title = "VERY CLOSE",
        description = "The submarine is directly nearby!"
    ),
    CLOSER(
        title = "CLOSER",
        description = "You're getting warmer. Moving toward the submarine."
    ),
    FARTHER(
        title = "FARTHER",
        description = "Getting colder. That search moved you away."
    ),
    SAME_DISTANCE(
        title = "SAME DISTANCE",
        description = "Equal distance to the submarine as your last search."
    ),
    FOUND(
        title = "SUBMARINE FOUND",
        description = "Direct hit! You located the hidden submarine."
    )
}
