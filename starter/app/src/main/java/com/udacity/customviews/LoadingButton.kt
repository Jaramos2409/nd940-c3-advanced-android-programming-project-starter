package com.udacity.customviews

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.withStyledAttributes
import androidx.databinding.BindingAdapter
import com.udacity.Constants
import com.udacity.R
import kotlin.properties.Delegates

private const val TAG = "LoadingButton"

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, oldValue, newValue ->
        run {
            if (newValue == ButtonState.Clicked) {
                Log.d(TAG, "ButtonState changed to Clicked.")
                loadingButtonProgress.createLoadingAnimation(
                    50L,
                    Constants.INITIAL_LOADING_ANIMATION_DURATION,
                    AccelerateDecelerateInterpolator()
                )
                setButtonStateLoading()
            } else if (newValue == ButtonState.Loading) {
                disableClicking()
            } else if (oldValue == ButtonState.Loading && newValue == ButtonState.Completed) {
                Log.d(TAG, "ButtonState changed to Completed.")
                loadingButtonProgress.cancelAllValueAnimators()
                loadingButtonProgress.resetButtonValues()
                enableClicking()
            }
        }
    }

    private var completedLoadingButtonText: LoadingButtonText
    private var downloadInProgressLoadingButtonText: LoadingButtonText

    var loadingButtonProgress: LoadingButtonProgress
        private set

    init {
        enableClicking()
        var completedDownloadText = ""
        var completedDownloadTextColor = 0
        var completedDownloadTextSize = 0f
        var downloadInProgressText = ""
        var downloadInProgressTextColor = 0
        var downloadInProgressTextSize = 0f
        var loadingBarFillColor = 0
        var loadingCircleFillColor = 0

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            completedDownloadText = getString(R.styleable.LoadingButton_completedDownloadText) ?: ""
            completedDownloadTextColor =
                getColor(R.styleable.LoadingButton_completedDownloadTextColor, 0)
            completedDownloadTextSize =
                getDimension(R.styleable.LoadingButton_completedDownloadTextSize, 0F)
            downloadInProgressText =
                getString(R.styleable.LoadingButton_downloadInProgressText) ?: ""
            downloadInProgressTextColor =
                getColor(R.styleable.LoadingButton_completedDownloadTextColor, 0)
            downloadInProgressTextSize =
                getDimension(R.styleable.LoadingButton_downloadInProgressTextSize, 0F)
            loadingBarFillColor = getColor(R.styleable.LoadingButton_loadingBarFillColor, 0)
            loadingCircleFillColor = getColor(R.styleable.LoadingButton_loadingCircleFillColor, 0)
        }

        completedLoadingButtonText = LoadingButtonText(
            parentView = this,
            text = completedDownloadText,
            textColor = completedDownloadTextColor,
            textSize = completedDownloadTextSize
        )

        downloadInProgressLoadingButtonText = LoadingButtonText(
            parentView = this,
            text = downloadInProgressText,
            textColor = downloadInProgressTextColor,
            textSize = downloadInProgressTextSize
        )

        loadingButtonProgress = LoadingButtonProgress(
            loadButton = this,
            loadingBarFillColor = loadingBarFillColor,
            loadingCircleFillColor = loadingCircleFillColor
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (buttonState == ButtonState.Loading) {
            canvas?.drawLoadingProgress(loadingButtonProgress)
            canvas?.drawLoadingTextAndCircle(
                loadingButtonProgress,
                downloadInProgressLoadingButtonText
            )
        } else if (buttonState == ButtonState.Completed) {
            canvas?.drawCompletedDownloadButtonText(completedLoadingButtonText)
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minimumWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minimumWidth, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun setButtonStateClicked() {
        buttonState = ButtonState.Clicked
    }

    private fun setButtonStateLoading() {
        buttonState = ButtonState.Loading
    }

    fun setButtonStateCompleted() {
        buttonState = ButtonState.Completed
    }

    fun triggerAnimationWithoutSelection() {
        setButtonStateLoading()
        loadingButtonProgress.createLoadingAnimation(
            100L,
            Constants.NO_SELECTION_ANIMATION_DURATION,
            DecelerateInterpolator()
        )
    }

    fun enableClicking() {
        isClickable = true
    }

    fun disableClicking() {
        isClickable = false
    }
}

@BindingAdapter("progress")
fun updateLoadingProgress(view: LoadingButton, progress: Long?) {
    Log.d(TAG, "Value of Progress in updateLoadingProgress Binding Adapter: $progress")
    progress?.let {
        view.loadingButtonProgress.updateProgress(progress)
    }
}

