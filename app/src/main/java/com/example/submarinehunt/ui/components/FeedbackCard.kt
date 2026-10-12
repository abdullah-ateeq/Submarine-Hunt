package com.example.submarinehunt.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submarinehunt.model.Coordinate
import com.example.submarinehunt.model.DistanceCategory
import com.example.submarinehunt.model.SearchFeedback
import com.example.submarinehunt.ui.theme.OceanCloserGreen
import com.example.submarinehunt.ui.theme.OceanCyanLight
import com.example.submarinehunt.ui.theme.OceanFartherRed
import com.example.submarinehunt.ui.theme.OceanFoundEmerald
import com.example.submarinehunt.ui.theme.OceanVeryCloseCyan

@Composable
fun FeedbackCard(
    modifier: Modifier = Modifier,
    feedback: SearchFeedback?,
    distanceCategory: DistanceCategory?,
    currentCoordinate: Coordinate?,
    previousCoordinate: Coordinate?
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = if (feedback != null) {
                    "Your last result: ${feedback.title.lowercase()}"
                } else {
                    "Ready to search"
                }
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(
            width = 1.dp,
            color = when (feedback) {
                SearchFeedback.CLOSER -> OceanCloserGreen.copy(alpha = 0.6f)
                SearchFeedback.FARTHER -> OceanFartherRed.copy(alpha = 0.5f)
                SearchFeedback.VERY_CLOSE -> OceanVeryCloseCyan.copy(alpha = 0.8f)
                SearchFeedback.FOUND -> OceanFoundEmerald
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = feedback,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "feedback_transition"
            ) { targetFeedback ->
                if (targetFeedback == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "AWAITING COORDINATE",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                letterSpacing = 1.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap any grid square to initiate sonar search.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val headerColor = when (targetFeedback) {
                            SearchFeedback.CLOSER -> OceanCloserGreen
                            SearchFeedback.FARTHER -> OceanFartherRed
                            SearchFeedback.VERY_CLOSE -> OceanVeryCloseCyan
                            SearchFeedback.FOUND -> OceanFoundEmerald
                            SearchFeedback.CLOSE -> OceanCyanLight
                            SearchFeedback.SAME_DISTANCE -> MaterialTheme.colorScheme.onSurface
                            SearchFeedback.FAR -> MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = targetFeedback.title,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = headerColor
                                )
                            )

                            if (distanceCategory != null && targetFeedback != SearchFeedback.FOUND) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = headerColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = distanceCategory.displayText,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = headerColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = targetFeedback.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        // Previous vs Current coordinates
                        if (currentCoordinate != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (previousCoordinate != null) {
                                    Text(
                                        text = "Previous: ${previousCoordinate.label}",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                                Text(
                                    text = "Current: ${currentCoordinate.label}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = headerColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
