package com.udacity.customviews

import android.animation.*
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.animation.AccelerateInterpolator
import com.udacity.Constants

private const val TAG = "LoadingButtonProgress"

class LoadingButtonProgress(
    val loadButton: LoadingButton,
    val loadingBarFillColor: Int,
    val loadingCircleFillColor: Int
) {
    val loadingBarFillPaint = Paint().apply {
        style = Paint.Style.FILL
        color = loadingBarFillColor
    }
    val loadingCircleFillPaint = Paint().apply {
        style = Paint.Style.FILL
        color = loadingCircleFillColor
        isAntiAlias = true
    }
    var loadingBarProgress = 0f
    var loadingCircleProgress = 0f

    private val listOfValueAnimators = mutableListOf<ValueAnimator>()

    fun createLoadingAnimation(
        endValue: Long,
        animationDuration: Long,
        interpolator: TimeInterpolator
    ) {
        val difference = endValue * 0.01
        val currentLoadingBarProgress = loadingBarProgress
        val currentLoadingCircleProgress = loadingCircleProgress

        val newLoadingBarFillValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            this.interpolator = interpolator
            this.duration = animationDuration
            addUpdateListener {
                loadingBarProgress =
                    currentLoadingBarProgress + ((it.animatedFraction * (loadButton.width * difference)).toFloat())
                loadButton.invalidate()
            }
        }

        val newLoadingCircleFillValueAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            this.interpolator = interpolator
            this.duration = animationDuration
            addUpdateListener {
                Log.d(TAG, "animatedValue inside of createLoadingAnimation: ${it.animatedValue}")
                loadingCircleProgress =
                    (currentLoadingCircleProgress + ((it.animatedValue as Float) * difference)).toFloat()
                loadButton.invalidate()
            }
        }

        if (endValue == 100L) {
            newLoadingBarFillValueAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    loadButton.setButtonStateCompleted()
                }
            })
        }

        listOfValueAnimators.add(newLoadingBarFillValueAnimator)
        listOfValueAnimators.add(newLoadingCircleFillValueAnimator)

        AnimatorSet().apply {
            playTogether(newLoadingBarFillValueAnimator, newLoadingCircleFillValueAnimator)
        }.start()
    }

    fun updateProgress(progress: Long) {
        if (progress >= 65L) {
            createLoadingAnimation(
                progress,
                Constants.IN_PROGRESS_LOADING_ANIMATION_DURATION,
                AccelerateInterpolator()
            )
        }
    }

    fun resetButtonValues() {
        loadingBarProgress = 0f
        loadingCircleProgress = 0f
    }

    fun cancelAllValueAnimators() {
        while (listOfValueAnimators.isNotEmpty()) {
            listOfValueAnimators.removeLast().cancel()
        }
    }
}

fun Canvas.drawLoadingProgress(loadingButtonProgress: LoadingButtonProgress) {
    drawRect(
        0f,
        0f,
        loadingButtonProgress.loadingBarProgress,
        loadingButtonProgress.loadButton.height.toFloat(),
        loadingButtonProgress.loadingBarFillPaint
    )
}

fun Canvas.drawLoadingTextAndCircle(
    loadingButtonProgress: LoadingButtonProgress,
    loadingButtonText: LoadingButtonText
) {
    val xPosOfText = loadingButtonText.parentView.width / 2f
    val yPosOfText =
        (loadingButtonText.parentView.height / 2f - (loadingButtonText.textPaint.descent() + loadingButtonText.textPaint.ascent()) / 2f)

    drawText(loadingButtonText.text, xPosOfText, yPosOfText, loadingButtonText.textPaint)

    val radius =
        minOf(loadingButtonProgress.loadButton.width, loadingButtonProgress.loadButton.height) / 4f

    val cx = xPosOfText + (loadingButtonText.textPaint.measureText(loadingButtonText.text) / 1.5f)
    val cy = loadingButtonProgress.loadButton.height / 2f

    drawArc(
        cx - radius,
        cy - radius,
        cx + radius,
        cy + radius,
        -90f,
        loadingButtonProgress.loadingCircleProgress,
        true,
        loadingButtonProgress.loadingCircleFillPaint
    )
}