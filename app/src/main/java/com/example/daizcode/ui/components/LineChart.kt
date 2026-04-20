package com.example.daizcode.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daizcode.ui.theme.ChartGridLine
import com.example.daizcode.ui.theme.ChartLine
import com.example.daizcode.ui.theme.ChartLineFill
import com.example.daizcode.ui.theme.DarkSurface
import com.example.daizcode.ui.theme.DarkSurfaceElevated
import com.example.daizcode.ui.theme.TextSecondary
import com.example.daizcode.ui.theme.TextTertiary

/**
 * Custom line chart drawn on Compose Canvas with neon glow and gradient fill.
 * Shows defects per time window.
 */
@Composable
fun LineChart(
    dataPoints: List<Float>,
    timeLabels: List<String>,
    modifier: Modifier = Modifier,
    lineColor: Color = ChartLine,
    fillColor: Color = ChartLineFill
) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(dataPoints.size) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkSurfaceElevated, DarkSurface)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "📈 DEFECTS OVER TIME",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (dataPoints.size < 2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Collecting data...",
                    fontSize = 13.sp,
                    color = TextTertiary
                )
            }
        } else {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val progress = animationProgress.value
                val width = size.width
                val height = size.height
                val padding = 24f

                val chartWidth = width - padding * 2
                val chartHeight = height - padding * 2

                val maxVal = (dataPoints.maxOrNull() ?: 1f).coerceAtLeast(1f)

                // Draw grid lines
                drawGridLines(chartWidth, chartHeight, padding)

                // Build the line path
                val pointCount = dataPoints.size
                val xStep = chartWidth / (pointCount - 1).coerceAtLeast(1)

                val linePath = Path()
                val fillPath = Path()

                val animatedCount = (pointCount * progress).toInt().coerceAtLeast(2)

                for (i in 0 until animatedCount) {
                    val x = padding + i * xStep
                    val y = padding + chartHeight - (dataPoints[i] / maxVal * chartHeight)

                    if (i == 0) {
                        linePath.moveTo(x, y)
                        fillPath.moveTo(x, padding + chartHeight)
                        fillPath.lineTo(x, y)
                    } else {
                        // Smooth curve using cubic bezier
                        val prevX = padding + (i - 1) * xStep
                        val prevY = padding + chartHeight - (dataPoints[i - 1] / maxVal * chartHeight)
                        val midX = (prevX + x) / 2f
                        linePath.cubicTo(midX, prevY, midX, y, x, y)
                        fillPath.cubicTo(midX, prevY, midX, y, x, y)
                    }
                }

                // Close fill path
                val lastX = padding + (animatedCount - 1) * xStep
                fillPath.lineTo(lastX, padding + chartHeight)
                fillPath.close()

                // Draw gradient fill
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            fillColor,
                            Color.Transparent
                        ),
                        startY = padding,
                        endY = padding + chartHeight
                    )
                )

                // Draw glow line (wider, semi-transparent)
                drawPath(
                    path = linePath,
                    color = lineColor.copy(alpha = 0.3f),
                    style = Stroke(
                        width = 8f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Draw main line
                drawPath(
                    path = linePath,
                    color = lineColor,
                    style = Stroke(
                        width = 3f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Draw data point dots
                for (i in 0 until animatedCount) {
                    val x = padding + i * xStep
                    val y = padding + chartHeight - (dataPoints[i] / maxVal * chartHeight)

                    // Glow
                    drawCircle(
                        color = lineColor.copy(alpha = 0.3f),
                        radius = 8f,
                        center = Offset(x, y)
                    )
                    // Dot
                    drawCircle(
                        color = lineColor,
                        radius = 4f,
                        center = Offset(x, y)
                    )
                }
            }
        }

        // Time labels row
        if (timeLabels.size >= 2) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = timeLabels.first(),
                    fontSize = 9.sp,
                    color = TextTertiary
                )
                Text(
                    text = timeLabels.last(),
                    fontSize = 9.sp,
                    color = TextTertiary
                )
            }
        }
    }
}

/**
 * Draws subtle horizontal grid lines across the chart area.
 */
private fun DrawScope.drawGridLines(
    chartWidth: Float,
    chartHeight: Float,
    padding: Float
) {
    val gridLines = 4
    for (i in 0..gridLines) {
        val y = padding + (chartHeight / gridLines) * i
        drawLine(
            color = ChartGridLine,
            start = Offset(padding, y),
            end = Offset(padding + chartWidth, y),
            strokeWidth = 1f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
        )
    }
}
