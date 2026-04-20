package com.example.daizcode.ui.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.daizcode.R

/**
 * Optimized Line Chart View.
 * Performs path calculations and shader updates outside of onDraw to ensure smooth performance.
 */
class LineChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var dataPoints: List<Float> = emptyList()
    
    // Cached objects to avoid allocations in onDraw
    private val linePath = Path()
    private val fillPath = Path()
    
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        color = ContextCompat.getColor(context, R.color.chart_line)
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
        color = ContextCompat.getColor(context, R.color.chart_grid_line)
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    private val chartLineFillColor = ContextCompat.getColor(context, R.color.chart_line_fill)

    /**
     * Updates the chart data and triggers a recalculation of paths.
     */
    fun setData(points: List<Float>) {
        if (this.dataPoints == points) return 
        this.dataPoints = points
        calculatePaths()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            updateGradient(h.toFloat())
            calculatePaths()
        }
    }

    private fun updateGradient(viewHeight: Float) {
        val padding = 20f
        fillPaint.shader = LinearGradient(
            0f, padding, 0f, viewHeight - padding,
            chartLineFillColor,
            Color.TRANSPARENT, 
            Shader.TileMode.CLAMP
        )
    }

    private fun calculatePaths() {
        linePath.reset()
        fillPath.reset()
        
        if (dataPoints.size < 2 || width == 0 || height == 0) return

        val padding = 20f
        val w = width.toFloat() - 2 * padding
        val h = height.toFloat() - 2 * padding
        val maxVal = (dataPoints.maxOrNull() ?: 1f).coerceAtLeast(1f)
        val stepX = w / (dataPoints.size - 1)

        for (i in dataPoints.indices) {
            val x = padding + i * stepX
            val y = padding + h - (dataPoints[i] / maxVal * h)

            if (i == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, padding + h)
                fillPath.lineTo(x, y)
            } else {
                val prevX = padding + (i - 1) * stepX
                val prevY = padding + h - (dataPoints[i - 1] / maxVal * h)
                val midX = (prevX + x) / 2
                linePath.cubicTo(midX, prevY, midX, y, x, y)
                fillPath.cubicTo(midX, prevY, midX, y, x, y)
            }
        }

        fillPath.lineTo(padding + w, padding + h)
        fillPath.close()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (dataPoints.size < 2) return

        val padding = 20f
        val w = width.toFloat() - 2 * padding
        val h = height.toFloat() - 2 * padding

        // Draw Grid
        for (i in 0..4) {
            val y = padding + (h / 4) * i
            canvas.drawLine(padding, y, padding + w, y, gridPaint)
        }

        // Draw pre-calculated paths
        canvas.drawPath(fillPath, fillPaint)
        canvas.drawPath(linePath, linePaint)
    }
}
