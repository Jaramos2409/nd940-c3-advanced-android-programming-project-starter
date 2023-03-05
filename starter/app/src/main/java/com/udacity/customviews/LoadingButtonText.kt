package com.udacity.customviews

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View

class LoadingButtonText(
    val parentView: View,
    val text: String = "",
    val textColor: Int,
    val textSize: Float,
    val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }
) {
    init {
        textPaint.textSize = textSize
        textPaint.color = textColor
    }
}

fun Canvas.drawCompletedDownloadButtonText(loadingButtonText: LoadingButtonText) {
    val xPos = loadingButtonText.parentView.width / 2f
    val yPos =
        (loadingButtonText.parentView.height / 2f - (loadingButtonText.textPaint.descent() + loadingButtonText.textPaint.ascent()) / 2f)

    drawText(loadingButtonText.text, xPos, yPos, loadingButtonText.textPaint)
}