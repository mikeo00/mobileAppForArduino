package com.example.daizcode.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daizcode.ui.theme.DarkSurface
import com.example.daizcode.ui.theme.DarkSurfaceElevated
import com.example.daizcode.ui.theme.TextSecondary
import com.example.daizcode.ui.theme.TextPrimary

/**
 * A stat card with animated number counter, accent color, and subtle glow.
 */
@Composable
fun StatCard(
    title: String,
    value: Int,
    accentColor: Color,
    glowColor: Color,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "stat_counter"
    )

    Card(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = glowColor,
                spotColor = glowColor
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DarkSurfaceElevated,
                            DarkSurface
                        )
                    )
                )
                .padding(12.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(accentColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "%,d".format(animatedValue),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = (-0.5).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Accent bar at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    accentColor,
                                    accentColor.copy(alpha = 0.1f)
                                )
                            ),
                            shape = RoundedCornerShape(1.dp)
                        )
                )
            }
        }
    }
}

/**
 * Row of three stat cards: Total, Good, Defective
 */
@Composable
fun StatCardsRow(
    totalProcessed: Int,
    goodCount: Int,
    defectiveCount: Int,
    accentBlue: Color,
    blueGlow: Color,
    accentGreen: Color,
    greenGlow: Color,
    accentRed: Color,
    redGlow: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            title = "TOTAL",
            value = totalProcessed,
            accentColor = accentBlue,
            glowColor = blueGlow,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "GOOD",
            value = goodCount,
            accentColor = accentGreen,
            glowColor = greenGlow,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "DEFECTS",
            value = defectiveCount,
            accentColor = accentRed,
            glowColor = redGlow,
            modifier = Modifier.weight(1f)
        )
    }
}
