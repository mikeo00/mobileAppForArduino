package com.example.daizcode.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daizcode.data.model.DetectionEvent
import com.example.daizcode.data.model.DetectionStatus
import com.example.daizcode.ui.theme.AccentGreen
import com.example.daizcode.ui.theme.AccentGreenGlow
import com.example.daizcode.ui.theme.AccentRed
import com.example.daizcode.ui.theme.AccentRedGlow
import com.example.daizcode.ui.theme.DarkSurfaceVariant
import com.example.daizcode.ui.theme.TextPrimary
import com.example.daizcode.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A single detection feed item with animated entry and status indicators.
 * Optimized for smaller screens.
 */
@Composable
fun DetectionFeedItem(
    event: DetectionEvent,
    modifier: Modifier = Modifier
) {
    val isDefective = event.status == DetectionStatus.DEFECTIVE
    val statusColor = if (isDefective) AccentRed else AccentGreen
    val glowColor = if (isDefective) AccentRedGlow else AccentGreenGlow
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = slideInHorizontally(initialOffsetX = { -it / 2 }) + fadeIn()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(DarkSurfaceVariant)
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Status indicator dot with glow
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(24.dp)
                ) {
                    // Glow behind
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(glowColor, CircleShape)
                    )
                    // Solid dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(statusColor, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Event details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${if (isDefective) "Defect" else "Good"} ${event.objectType.displayName}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = timeFormat.format(Date(event.timestamp)),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextTertiary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Confidence badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "%.0f%%".format(event.confidence * 100),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }
        }
    }
}
