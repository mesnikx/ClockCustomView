package com.example.clockcustomview

import android.content.Context
import android.graphics.*
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private const val START_ANGLE = -Math.PI / 2

private const val REFRESH_PERIOD = 180L

private const val DEFAULT_WIDTH_IN_DP = 240

private const val DEFAULT_HEIGHT_IN_DP = 240

class ClockView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var clockRadius = 0.0f

    private var centerX = 0.0f

    private var centerY = 0.0f

    private val position: PointF = PointF(0.0f, 0.0f)

    private var baseColor = 0

    private var textColor = 0

    private var frameColor = 0

    private var hourHandColor = 0

    private var minuteHandColor = 0

    var secondHandColor = 0

    private var dotsColor = 0

    init {
        context.withStyledAttributes(attrs, R.styleable.ClockView) {
            baseColor = getColor(
                R.styleable.ClockView_baseColor,
                ContextCompat.getColor(context, R.color.light_gray)
            )
            textColor = getColor(
                R.styleable.ClockView_textColor,
                ContextCompat.getColor(context, R.color.black)
            )
            frameColor = getColor(
                R.styleable.ClockView_frameColor,
                ContextCompat.getColor(context, R.color.black)
            )
            hourHandColor = getColor(
                R.styleable.ClockView_hourHandColor,
                ContextCompat.getColor(context, R.color.black)
            )
            minuteHandColor = getColor(
                R.styleable.ClockView_minuteHandColor,
                ContextCompat.getColor(context, R.color.black)
            )
            secondHandColor = getColor(
                R.styleable.ClockView_secondHandColor,
                ContextCompat.getColor(context, R.color.black)
            )
            dotsColor = getColor(
                R.styleable.ClockView_dotsColor,
                ContextCompat.getColor(context, R.color.black)
            )
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textScaleX = 0.9f
        letterSpacing = -0.15f
        typeface = Typeface.DEFAULT
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        clockRadius = min(width, height) / 2f
        centerX = width / 2f
        centerY = height / 2f
    }

    private fun PointF.computeXYForPoints(pos: Int, radius: Float) {
        val angle = (pos * (Math.PI / 30)).toFloat()
        x = radius * cos(angle) + centerX
        y = radius * sin(angle) + centerY
    }

    private fun PointF.computeXYForHourLabels(hour: Int, radius: Float) {
        val angle = (START_ANGLE + hour * (Math.PI / 6)).toFloat()
        x = radius * cos(angle) + centerX
        val textBaselineToCenter = (paint.descent() + paint.ascent()) / 2
        y = radius * sin(angle) + centerY - textBaselineToCenter
    }

    private fun drawClockBase(canvas: Canvas) {
        paint.color = baseColor
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, clockRadius, paint)
    }

    private fun drawClockFrame(canvas: Canvas) {
        paint.color = frameColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = clockRadius / 12
        val boundaryRadius = clockRadius - paint.strokeWidth / 2
        canvas.drawCircle(centerX, centerY, boundaryRadius, paint)
        paint.strokeWidth = 0f
    }

    private fun drawClockHands(canvas: Canvas) {
        val calendar: Calendar = Calendar.getInstance()
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        hour = if (hour > 12) hour - 12 else hour
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        paint.style = Paint.Style.STROKE
        drawHourHand(canvas, hour + minute / 60f)
        drawMinuteHand(canvas, minute)
        drawSecondHand(canvas, second)
    }

    private fun drawHourHand(canvas: Canvas, hourWithMinutes: Float) {
        paint.color = hourHandColor
        paint.strokeWidth = clockRadius / 15
        val angle = (Math.PI * hourWithMinutes / 6 + START_ANGLE).toFloat()
        canvas.drawLine(
            centerX - cos(angle) * clockRadius * 3 / 14,
            centerY - sin(angle) * clockRadius * 3 / 14,
            centerX + cos(angle) * clockRadius * 7 / 14,
            centerY + sin(angle) * clockRadius * 7 / 14,
            paint
        )
    }

    private fun drawMinuteHand(canvas: Canvas, minute: Int) {
        paint.color = minuteHandColor
        paint.strokeWidth = clockRadius / 40
        val angle = (Math.PI * minute / 30 + START_ANGLE).toFloat()
        canvas.drawLine(
            centerX - cos(angle) * clockRadius * 2 / 7,
            centerY - sin(angle) * clockRadius * 2 / 7,
            centerX + cos(angle) * clockRadius * 5 / 7,
            centerY + sin(angle) * clockRadius * 5 / 7,
            paint
        )
    }

    private fun drawSecondHand(canvas: Canvas, second: Int) {
        paint.color = secondHandColor
        val angle = (Math.PI * second / 30 + START_ANGLE).toFloat()
        paint.strokeWidth = clockRadius / 80
        canvas.drawLine(
            centerX - cos(angle) * clockRadius * 1 / 14,
            centerY - sin(angle) * clockRadius * 1 / 14,
            centerX + cos(angle) * clockRadius * 5 / 7,
            centerY + sin(angle) * clockRadius * 5 / 7,
            paint
        )
        paint.strokeWidth = clockRadius / 50
        canvas.drawLine(
            centerX - cos(angle) * clockRadius * 2 / 7,
            centerY - sin(angle) * clockRadius * 2 / 7,
            centerX - cos(angle) * clockRadius * 1 / 14,
            centerY - sin(angle) * clockRadius * 1 / 14,
            paint
        )
    }

    private fun drawDots(canvas: Canvas) {
        paint.color = dotsColor
        paint.style = Paint.Style.FILL
        val dotsDrawLineRadius = clockRadius * 5 / 6
        for (i in 0 until 60) {
            position.computeXYForPoints(i, dotsDrawLineRadius)
            val dotRadius = if (i % 5 == 0) clockRadius / 96 else clockRadius / 128
            canvas.drawCircle(position.x, position.y, dotRadius, paint)
        }
    }

    private fun drawHourLabels(canvas: Canvas) {
        paint.textSize = clockRadius * 2 / 7
        paint.strokeWidth = 0f
        paint.color = textColor
        val labelsDrawLineRadius = clockRadius * 11 / 16
        for (i in 1..12) {
            position.computeXYForHourLabels(i, labelsDrawLineRadius)
            val label = i.toString()
            canvas.drawText(label, position.x, position.y, paint)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawClockBase(canvas)
        drawClockFrame(canvas)
        drawDots(canvas)
        drawHourLabels(canvas)
        drawClockHands(canvas)
        postInvalidateDelayed(REFRESH_PERIOD)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val defaultWidth = (DEFAULT_WIDTH_IN_DP * resources.displayMetrics.density).toInt()
        val defaultHeight = (DEFAULT_HEIGHT_IN_DP * resources.displayMetrics.density).toInt()

        val widthToSet = resolveSize(defaultWidth, widthMeasureSpec)
        val heightToSet = resolveSize(defaultHeight, heightMeasureSpec)

        setMeasuredDimension(widthToSet, heightToSet)
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putInt("baseColor", baseColor)
        bundle.putInt("textColor", textColor)
        bundle.putInt("frameColor", frameColor)
        bundle.putInt("hourHandColor", hourHandColor)
        bundle.putInt("minuteHandColor", minuteHandColor)
        bundle.putInt("secondHandColor", secondHandColor)
        bundle.putInt("dotsColor", dotsColor)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState: Parcelable? = null
        if (state is Bundle) {
            baseColor = state.getInt("baseColor")
            textColor = state.getInt("textColor")
            frameColor = state.getInt("frameColor")
            dotsColor = state.getInt("dotsColor")
            hourHandColor = state.getInt("hourHandColor")
            minuteHandColor = state.getInt("minuteHandColor")
            secondHandColor = state.getInt("secondHandColor")
            superState =
                if (SDK_INT >= 33) state.getParcelable("superState", Parcelable::class.java)
                else @Suppress("DEPRECATION") state.getParcelable("superState")
        }
        super.onRestoreInstanceState(superState)
    }
}