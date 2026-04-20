package com.example.daizcode.ui.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.daizcode.R

class PieChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var goodCount: Int = 0
    private var defectiveCount: Int = 0

    private val goodPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val defectivePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = ContextCompat.getColor(context, R.color.text_primary)
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    fun setData(good: Int, defective: Int) {
        this.goodCount = good
        this.defectiveCount = defective
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val total = goodCount + defectiveCount
        val goodFraction = if (total > 0) goodCount.toFloat() / total else 0.5f
        val defectiveFraction = if (total > 0) defectiveCount.toFloat() / total else 0.5f

        val padding = 40f
        val size = minOf(width, height).toFloat() - padding * 2
        val strokeWidth = size * 0.15f
        
        goodPaint.strokeWidth = strokeWidth
        goodPaint.color = ContextCompat.getColor(context, R.color.accent_green)
        
        defectivePaint.strokeWidth = strokeWidth
        defectivePaint.color = ContextCompat.getColor(context, R.color.accent_red)

        val rect = RectF(
            (width - size) / 2,
            (height - size) / 2,
            (width + size) / 2,
            (height + size) / 2
        )

        val goodSweep = goodFraction * 360f
        val defectiveSweep = defectiveFraction * 360f

        // Draw Good Arc
        canvas.drawArc(rect, -90f, goodSweep, false, goodPaint)
        
        // Draw Defective Arc (with a small gap)
        if (defectiveSweep > 5f) {
            canvas.drawArc(rect, -90f + goodSweep + 4f, (defectiveSweep - 4f).coerceAtLeast(0f), false, defectivePaint)
        }

        // Draw center text
        textPaint.textSize = size * 0.2f
        val percentage = if (total > 0) (goodFraction * 100).toInt().toString() + "%" else "—"
        canvas.drawText(percentage, width / 2f, height / 2f + textPaint.textSize / 3, textPaint)
    }
}
