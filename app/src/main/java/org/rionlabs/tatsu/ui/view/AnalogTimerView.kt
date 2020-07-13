package org.rionlabs.tatsu.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.utils.dp
import kotlin.math.*

class AnalogTimerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val ticksPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorTimerTicks)
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 3.dp
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private val progressBackgroundPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorTimerBackground)
        style = Paint.Style.STROKE
        strokeWidth = 8.dp
        isAntiAlias = true
    }

    private val progressPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.color_primary_variant)
        style = Paint.Style.STROKE
        strokeWidth = 8.dp
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private val pointPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.color_secondary)
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val rect = Rect()
    private val rectF = RectF()

    private var angle = 0f

    private var progressRadius = 0F

    private var numberOfTicks = 48

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val outerRect = Rect(0, 0, w, h)

        outerRect.inset(
            max(max(paddingStart, paddingLeft), max(paddingEnd, paddingRight)),
            max(paddingTop, paddingBottom)
        )

        val size = min(outerRect.width(), outerRect.height())
        val centerX = outerRect.centerX()
        val centerY = outerRect.centerY()

        rect.set(
            centerX - size / 2,
            centerY - size / 2,
            centerX + size / 2,
            centerY + size / 2
        )

        progressRadius = (size * 0.7 / 2).toFloat()
        val insetRect = Rect(rect).apply { inset((size * 0.15).toInt(), (size * 0.15).toInt()) }
        rectF.set(insetRect)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = min(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(size, size)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return

        val centerX = rect.exactCenterX()
        val centerY = rect.exactCenterY()
        canvas.drawCircle(
            rect.exactCenterX(),
            rect.exactCenterY(),
            progressRadius,
            progressBackgroundPaint
        )
        val radius = progressRadius + 10.dp
        val tickLength = radius + 8.dp

        var tickAngle: Float
        for (i in 0..numberOfTicks) {
            tickAngle = (2 * PI).toFloat() * i.toFloat() / numberOfTicks.toFloat()

            ticksPaint.strokeWidth = if (i % 3 == 0) 4.dp else 2.dp

            canvas.drawLine(
                centerX + radius * sin(tickAngle),
                centerY + radius * cos(tickAngle),
                centerX + tickLength * sin(tickAngle),
                centerY + tickLength * cos(tickAngle),
                ticksPaint
            )
        }

        canvas.drawArc(rectF, 270F, angle, false, progressPaint)
    }

    fun setTimer(timer: Timer) {
        angle = timer.completionPercent() * 360F
        invalidate()
    }
}