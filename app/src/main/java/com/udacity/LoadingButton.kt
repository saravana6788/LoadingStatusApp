package com.udacity

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.RESTART
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private var widthSize = 0
    private var heightSize = 0
    private var valueAnimator = ValueAnimator()

    private var buttonText: String

    private var defaultButtonColor: Int = 0

    private var loadingButtonColor: Int = 0

    private var loadingCircleColor: Int = 0

    private var progressCircle = 0f

    private var progressWidth = 0f

    private val paint = Paint()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Clicked -> {
                buttonText = "Clicked"
            }

            ButtonState.Loading -> {
                buttonText = "Loading..."
                valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())

                valueAnimator.addUpdateListener {
                    progressWidth = it.animatedValue as Float
                    progressCircle = (widthSize.toFloat() / 365) * progressWidth
                    invalidate()
                }
                valueAnimator.duration = 5000

                valueAnimator.repeatMode = ValueAnimator.REVERSE
                valueAnimator.repeatCount = INFINITE
                valueAnimator.start()
                invalidate()
            }

            ButtonState.Completed -> {
                buttonText = "Download"
                valueAnimator.cancel()
                progressWidth = 0f
                progressCircle = 0f
                invalidate()
                requestLayout()

            }
        }

    }


    init {

        buttonText = "Download"
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            defaultButtonColor = getColor(R.styleable.LoadingButton_buttonDefaultColor, 0)
            loadingButtonColor = getColor(R.styleable.LoadingButton_buttonLoadingColor, 0)
            loadingCircleColor = getColor(R.styleable.LoadingButton_loadingCircleColor, 0)
        }
        paint.color = defaultButtonColor
        paint.isAntiAlias = true

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBackgroundColor(canvas)
        drawProgressBackground(canvas)
        drawProgressCircle(canvas)
        drawText(canvas)

    }


    private fun drawBackgroundColor(canvas: Canvas?) {
        paint.color = defaultButtonColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
    }

    private fun drawText(canvas: Canvas?) {
        paint.color = Color.WHITE
        paint.textSize = 50f
        paint.textAlign = Paint.Align.CENTER
        canvas?.drawText(buttonText, widthSize / 2f, heightSize / 2f, paint)
    }


    private fun drawProgressBackground(canvas: Canvas?) {
        paint.color = loadingButtonColor
        canvas?.drawRect(0f, 0f, widthSize * progressWidth / 100, heightSize.toFloat(), paint)
    }

    private fun drawProgressCircle(canvas: Canvas?) {
        val arcDiameter = 20f
        val arcSize = measuredHeight.toFloat() - paddingBottom.toFloat() - arcDiameter
        paint.color = loadingCircleColor
        canvas?.drawArc(paddingStart + arcDiameter, paddingTop.toFloat() + arcDiameter, arcSize, arcSize,
            0f, progressCircle, false, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}