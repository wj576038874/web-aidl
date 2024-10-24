package com.example.example

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout

/**
 * author: wenjie
 * date: 2023/7/11 11:26
 * description:
 */
class WebViewProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * 进度条颜色
     */
    private var mColor = 0

    /**
     * 进度条的画笔
     */
    private var mPaint: Paint = Paint()

    /**
     * 进度条动画
     */
    private var mAnimator: Animator? = null

    /**
     * 控件的宽度
     */
    private var mTargetWidth = 0


    /**
     * 当前匀速动画最大的时长
     */
    private var mCurrentMaxUniformSpeedDuration = MAX_UNIFORM_SPEED_DURATION

    /**
     * 当前加速后减速动画最大时长
     */
    private var mCurrentMaxDecelerateSpeedDuration = MAX_DECELERATE_SPEED_DURATION

    /**
     * 结束动画时长
     */
    private var mCurrentDoEndAnimationDuration = DO_END_ANIMATION_DURATION

    /**
     * 当前进度条的状态
     */
    private var status = 0

    private var mCurrentProgress = 0f

    companion object {
        /**
         * 默认匀速动画最大的时长
         */
        private const val MAX_UNIFORM_SPEED_DURATION = 8 * 1000

        /**
         * 默认加速后减速动画最大时长
         */
        private const val MAX_DECELERATE_SPEED_DURATION = 450

        /**
         * 结束动画时长 ， Fade out 。
         */
        private const val DO_END_ANIMATION_DURATION = 600

        private const val UN_START = 0
        private const val STARTED = 1
        private const val FINISH = 2
    }

    init {
        attrs?.let {
//            val ta = context.obtainStyledAttributes(attrs, R.styleable.WebViewProgressBar)
//            mColor = ta.getColor(
//                R.styleable.WebViewProgressBar_wv_pb_color,
//                ContextCompat.getColor(context, R.color.ThemePrimaryRed)
//            )
//            ta.recycle()
        }
        mColor = Color.RED
        mPaint.isAntiAlias = true
        mPaint.color = mColor
        mPaint.isDither = true
        mPaint.strokeCap = Paint.Cap.SQUARE
        mTargetWidth = context.resources.displayMetrics.widthPixels
    }

    override fun onDraw(canvas: Canvas) {

    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.drawRect(
            0f,
            0f,
            mCurrentProgress / 100 * java.lang.Float.valueOf(this.width.toFloat()),
            this.height.toFloat(),
            mPaint
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mTargetWidth = measuredWidth
        val screenWidth = context.resources.displayMetrics.widthPixels
        if (mTargetWidth >= screenWidth) {
            mCurrentMaxDecelerateSpeedDuration = MAX_DECELERATE_SPEED_DURATION
            mCurrentMaxUniformSpeedDuration = MAX_UNIFORM_SPEED_DURATION
            mCurrentDoEndAnimationDuration = MAX_DECELERATE_SPEED_DURATION
        } else {
            //取比值
            val rate = mTargetWidth / java.lang.Float.valueOf(screenWidth.toFloat())
            mCurrentMaxUniformSpeedDuration =
                (MAX_UNIFORM_SPEED_DURATION * rate).toInt()
            mCurrentMaxDecelerateSpeedDuration =
                (MAX_DECELERATE_SPEED_DURATION * rate).toInt()
            mCurrentDoEndAnimationDuration = (DO_END_ANIMATION_DURATION * rate).toInt()
        }
    }

    private fun setProgress(progress: Float) {
        if (visibility == GONE) {
            visibility = VISIBLE
        }
        if (progress < 95f) {
            return
        }
        if (status != FINISH) {
            startAnim(true)
        }
    }


    private fun startAnim(isFinished: Boolean) {
        val v = (if (isFinished) 100 else 95).toFloat()
        if (mAnimator?.isStarted == true) {
            mAnimator?.cancel()
        }
        mCurrentProgress = if (mCurrentProgress == 0f) 0.00000001f else mCurrentProgress
        if (!isFinished) {
            val animatorSet = AnimatorSet()
            val p1 = v * 0.60f
            val animator = ValueAnimator.ofFloat(mCurrentProgress, p1)
            val animator0 = ValueAnimator.ofFloat(p1, v)
            val residue = 1f - mCurrentProgress / 100 - 0.05f
            val duration = (residue * mCurrentMaxUniformSpeedDuration).toLong()
            val duration6 = (duration * 0.6f).toLong()
            val duration4 = (duration * 0.4f).toLong()
            animator.interpolator = LinearInterpolator()
            animator.duration = duration4
            animator.addUpdateListener(mAnimatorUpdateListener)
            animator0.interpolator = LinearInterpolator()
            animator0.duration = duration6
            animator0.addUpdateListener(mAnimatorUpdateListener)
            animatorSet.play(animator0).after(animator)
            animatorSet.start()
            mAnimator = animatorSet
        } else {
            var segment95Animator: ValueAnimator? = null
            if (mCurrentProgress < 95f) {
                segment95Animator = ValueAnimator.ofFloat(mCurrentProgress, 95f)
                val residue = 1f - mCurrentProgress / 100f - 0.05f
                segment95Animator.duration = (residue * mCurrentMaxDecelerateSpeedDuration).toLong()
                segment95Animator.interpolator = DecelerateInterpolator()
                segment95Animator.addUpdateListener(mAnimatorUpdateListener)
            }
            val mObjectAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
            mObjectAnimator.duration = mCurrentDoEndAnimationDuration.toLong()
            val mValueAnimatorEnd = ValueAnimator.ofFloat(95f, 100f)
            mValueAnimatorEnd.duration = mCurrentDoEndAnimationDuration.toLong()
            mValueAnimatorEnd.addUpdateListener(mAnimatorUpdateListener)
            var animatorSet = AnimatorSet()
            animatorSet.playTogether(mObjectAnimator, mValueAnimatorEnd)
            if (segment95Animator != null) {
                val animatorSet0 = AnimatorSet()
                animatorSet0.play(animatorSet).after(segment95Animator)
                animatorSet = animatorSet0
            }
            animatorSet.addListener(mAnimatorListenerAdapter)
            animatorSet.start()
            mAnimator = animatorSet
        }
        status = STARTED
    }

    private val mAnimatorUpdateListener =
        AnimatorUpdateListener { animation ->
            val t = animation.animatedValue as Float
            this.mCurrentProgress = t
            this.invalidate()
        }

    private val mAnimatorListenerAdapter: AnimatorListenerAdapter =
        object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                doEnd()
            }
        }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mAnimator?.isStarted == true) {
            mAnimator?.cancel()
            mAnimator = null
        }
    }

    private fun doEnd() {
        if (status == FINISH && mCurrentProgress == 100f) {
            visibility = GONE
            mCurrentProgress = 0f
            this.alpha = 1f
        }
        status = UN_START
    }


    private fun show() {
        if (visibility == GONE) {
            this.visibility = VISIBLE
            mCurrentProgress = 0f
            startAnim(false)
        }
    }

    private fun hide() {
        status = FINISH
    }

    fun setProgress(newProgress: Int) {
        when (newProgress) {
            0 -> {
               reset()
            }
            in 1..10 -> {
                show()
            }
            in 11..94 -> {
                setProgress(newProgress.toFloat())
            }

            else -> {
                setProgress(newProgress.toFloat())
                hide()
            }
        }

    }

    private fun reset() {
        mCurrentProgress = 0f
        if (mAnimator?.isStarted == true) {
            mAnimator?.cancel()
        }
    }

}