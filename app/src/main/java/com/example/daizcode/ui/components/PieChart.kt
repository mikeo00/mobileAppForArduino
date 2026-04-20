package com.example.daizcode.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daizcode.ui.theme.AccentGreen
import com.example.daizcode.ui.theme.AccentRed
import com.example.daizcode.ui.theme.DarkSurface
import com.example.daizcode.ui.theme.DarkSurfaceElevated
import com.example.daizcode.ui.theme.TextPrimary
import com.example.daizcode.ui.theme.TextSecondary
import com.example.daizcode.ui.theme.TextTertiary

/**
 * Animated donut/pie chart showing good vs defective ratio.
 * Scaled to fit its container.
 */
@Composable
fun PieChart(
    goodCount: Int,
    defectiveCount: Int,
    modifier: Modifier = Modifier,
    goodColor: Color = AccentGreen,
    defectiveColor: Color = AccentRed
) {
    val total = goodCount + defectiveCount
    val goodFraction = if (total > 0) goodCount.toFloat() / total else 0.5f
    val defectiveFraction = if (total > 0) defectiveCount.toFloat() / total else 0.5f

    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(total) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkSurfaceElevated, DarkSurface)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🥧 QUALITY",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            letterSpacing = 0.5.sp,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Donut chart - using BoxWithConstraints to make it responsive
        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            val canvasSize = minOf(maxWidth, maxHeight)
            val strokeWidth = canvasSize.value * 0.15f // Responsive stroke width

            Canvas(modifier = Modifier.size(canvasSize)) {
                val progress = animationProgress.value

                val goodSweep = goodFraction * 360f * progress
                val defectiveSweep = defectiveFraction * 360f * progress

                // Good arc - glow
                drawArc(
                    color = goodColor.copy(alpha = 0.2f),
                    startAngle = -90f,
                    sweepAngle = goodSweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidth + 8f, cap = StrokeCap.Round)
                )

                // Good arc
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(goodColor, goodColor.copy(alpha = 0.7f), goodColor)
                    ),
                    startAngle = -90f,
                    sweepAngle = goodSweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Defective arc - glow
                drawArc(
                    color = defectiveColor.copy(alpha = 0.2f),
                    startAngle = -90f + goodSweep + 4f,
                    sweepAngle = (defectiveSweep - 4f).coerceAtLeast(0f),
                    useCenter = false,
                    style = Stroke(width = strokeWidth + 8f, cap = StrokeCap.Round)
                )

                // Defective arc
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(defectiveColor, defectiveColor.copy(alpha = 0.7f), defectiveColor)
                    ),
                    startAngle = -90f + goodSweep + 4f,
                    sweepAngle = (defectiveSweep - 4f).coerceAtLeast(0f),
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Center percentage text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (total > 0) "%.0f%%".format(goodFraction * 100) else "—",
                    fontSize = (canvasSize.value * 0.2f).sp, // Responsive font size
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "yield",
                    fontSize = (canvasSize.value * 0.08f).sp,
                    fontWeight = FontWeight.Normal,
                    color = TextTertiary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Legend - stacked vertically if space is tight
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LegendItem(color = goodColor, label = "Good", count = goodCount)
            LegendItem(color = defectiveColor, label = "Defective", count = defectiveCount)
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    count: Int
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label: $count",
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
