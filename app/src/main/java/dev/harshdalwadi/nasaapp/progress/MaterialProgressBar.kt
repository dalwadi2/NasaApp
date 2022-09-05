package dev.harshdalwadi.nasaapp.progress

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation.AnimationListener
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import dev.harshdalwadi.nasaapp.R

/*
 * Copyright (c) 2015-2016 Sailfin Technologies, Pvt. Ltd.  All Rights Reserved.
 * This software is the confidential and proprietary information
 * (Confidential Information) of Sailfin Technologies, Pvt. Ltd.  You shall not
 * disclose or use Confidential Information without the express written
 * agreement of Sailfin Technologies, Pvt. Ltd.
 */
/**
 * Class Usage : Image view for custom progress bar
 */
class MaterialProgressBar : AppCompatImageView {
    private var mListener: AnimationListener? = null
    private var mShadowRadius = 0
    private var mBackGroundColor = 0
    private var mProgressColor = 0
    private var mProgressStokeWidth = 0
    private var mArrowWidth = 0
    private var mArrowHeight = 0
    private var mProgress = 0
    var max = 0
    private var mDiameter = 0
    private var mInnerRadius = 0
    private var mTextPaint: Paint? = null
    private var mTextColor = 0
    private var mTextSize = 0
    var isShowProgressText = false
    var isShowArrow = false
    private var mProgressDrawable: MaterialProgressDrawable? = null
    private var mBgCircle: ShapeDrawable? = null
    private var mCircleBackgroundEnabled = false
    private var mColors = intArrayOf(Color.BLACK)

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.MaterialProgressBar, defStyleAttr, 0
        )
        val density = getContext().resources.displayMetrics.density
        mBackGroundColor = a.getColor(
            R.styleable.MaterialProgressBar_background_color, DEFAULT_CIRCLE_BG_LIGHT
        )
        mProgressColor = a.getColor(
            R.styleable.MaterialProgressBar_progress_color, DEFAULT_CIRCLE_BG_LIGHT
        )
        mInnerRadius = a.getDimensionPixelOffset(
            R.styleable.MaterialProgressBar_inner_radius, -1
        )
        mProgressStokeWidth = a.getDimensionPixelOffset(
            R.styleable.MaterialProgressBar_progress_stoke_width, (STROKE_WIDTH_LARGE * density).toInt()
        )
        mArrowWidth = a.getDimensionPixelOffset(
            R.styleable.MaterialProgressBar_arrow_width, -1
        )
        mArrowHeight = a.getDimensionPixelOffset(
            R.styleable.MaterialProgressBar_arrow_height, -1
        )
        mTextSize = a.getDimensionPixelOffset(
            R.styleable.MaterialProgressBar_progress_text_size, (DEFAULT_TEXT_SIZE * density).toInt()
        )
        mTextColor = a.getColor(
            R.styleable.MaterialProgressBar_progress_text_color, Color.BLACK
        )
        isShowArrow = a.getBoolean(R.styleable.MaterialProgressBar_show_arrow, false)
        mCircleBackgroundEnabled = a.getBoolean(R.styleable.MaterialProgressBar_enable_circle_background, true)
        mProgress = a.getInt(R.styleable.MaterialProgressBar_progress, 0)
        max = a.getInt(R.styleable.MaterialProgressBar_max, 100)
        val textVisible = a.getInt(R.styleable.MaterialProgressBar_progress_text_visibility, 1)
        if (textVisible != 1) {
            isShowProgressText = true
        }
        mTextPaint = Paint()
        mTextPaint!!.style = Paint.Style.STROKE
        mTextPaint!!.color = mTextColor
        mTextPaint!!.textSize = mTextSize.toFloat()
        mTextPaint!!.isAntiAlias = true
        a.recycle()
        mProgressDrawable = MaterialProgressDrawable(getContext(), this)
        super.setImageDrawable(mProgressDrawable)
    }

    private fun elevationSupported(): Boolean {
        return Build.VERSION.SDK_INT >= 21
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!elevationSupported()) {
            setMeasuredDimension(
                measuredWidth + mShadowRadius * 2, measuredHeight
                        + mShadowRadius * 2
            )
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val density = context.resources.displayMetrics.density
        mDiameter = Math.min(measuredWidth, measuredHeight)
        if (mDiameter <= 0) {
            mDiameter = density.toInt() * DEFAULT_CIRCLE_DIAMETER
        }
        if (background == null && mCircleBackgroundEnabled) {
            val shadowYOffset = (density * Y_OFFSET).toInt()
            val shadowXOffset = (density * X_OFFSET).toInt()
            mShadowRadius = (density * SHADOW_RADIUS).toInt()
            if (elevationSupported()) {
                mBgCircle = ShapeDrawable(OvalShape())
                ViewCompat.setElevation(this, SHADOW_ELEVATION * density)
            } else {
                val oval: OvalShape = OvalShadow(mShadowRadius, mDiameter)
                mBgCircle = ShapeDrawable(oval)
                ViewCompat.setLayerType(this, LAYER_TYPE_SOFTWARE, mBgCircle!!.paint)
                mBgCircle!!.paint.setShadowLayer(
                    mShadowRadius.toFloat(), shadowXOffset.toFloat(), shadowYOffset.toFloat(),
                    KEY_SHADOW_COLOR
                )
                val padding = mShadowRadius
                // set padding so the inner image sits correctly within the shadow.
                setPadding(padding, padding, padding, padding)
            }
            mBgCircle!!.paint.color = mBackGroundColor
            setBackgroundDrawable(mBgCircle)
        }
        mProgressDrawable!!.setBackgroundColor(mBackGroundColor)
        mProgressDrawable!!.setColorSchemeColors(*mColors)
        mProgressDrawable!!.setSizeParameters(
            mDiameter.toDouble(), mDiameter.toDouble(),
            if (mInnerRadius <= 0) ((mDiameter - mProgressStokeWidth * 2) / 4).toDouble() else mInnerRadius.toDouble(),
            mProgressStokeWidth.toDouble(),
            if (mArrowWidth < 0) (mProgressStokeWidth * 4).toFloat() else mArrowWidth.toFloat(),
            if (mArrowHeight < 0) (mProgressStokeWidth * 2).toFloat() else mArrowHeight.toFloat()
        )
        if (isShowArrow) {
            mProgressDrawable!!.setArrowScale(1f)
            mProgressDrawable!!.showArrow(true)
        }
        super.setImageDrawable(null)
        super.setImageDrawable(mProgressDrawable)
        mProgressDrawable!!.alpha = 254
        mProgressDrawable!!.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isShowProgressText) {
            val text = String.format("%s%%", mProgress)
            val x = width / 2 - text.length * mTextSize / 4
            val y = height / 2 + mTextSize / 4
            canvas.drawText(text, x.toFloat(), y.toFloat(), mTextPaint!!)
        }
    }

    override fun setImageResource(resId: Int) {}

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
    }

    override fun setImageDrawable(drawable: Drawable?) {}
    fun setAnimationListener(listener: AnimationListener?) {
        mListener = listener
    }

    public override fun onAnimationStart() {
        super.onAnimationStart()
        if (mListener != null) {
            mListener!!.onAnimationStart(animation)
        }
    }

    public override fun onAnimationEnd() {
        super.onAnimationEnd()
        if (mListener != null) {
            mListener!!.onAnimationEnd(animation)
        }
    }

    fun setColorSchemeResources(vararg colorResIds: Int) {
        val res = resources
        val colorRes = IntArray(colorResIds.size)
        for (i in 0 until colorResIds.size) {
            colorRes[i] = res.getColor(colorResIds[i])
        }
        setColorSchemeColors(*colorRes)
    }

    fun setColorSchemeColors(vararg colors: Int) {
        mColors = colors
        if (mProgressDrawable != null) {
            mProgressDrawable!!.setColorSchemeColors(*colors)
        }
    }

    @SuppressLint("ResourceType")
    override fun setBackgroundColor(@ColorRes colorRes: Int) {
        if (background is ShapeDrawable) {
            (background as ShapeDrawable).paint.color = resources.getColor(colorRes)
        }
    }

    var progress: Int
        get() = mProgress
        set(progress) {
            if (max > 0) {
                mProgress = progress
            }
        }

    fun circleBackgroundEnabled(): Boolean {
        return mCircleBackgroundEnabled
    }

    fun setCircleBackgroundEnabled(enableCircleBackground: Boolean) {
        mCircleBackgroundEnabled = enableCircleBackground
    }

    override fun getVisibility(): Int {
        return super.getVisibility()
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (mProgressDrawable != null) {
            if (visibility != View.VISIBLE) {
                mProgressDrawable!!.stop()
            }
            mProgressDrawable!!.setVisible(visibility == View.VISIBLE, false)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mProgressDrawable != null) {
            mProgressDrawable!!.stop()
            mProgressDrawable!!.setVisible(visibility == View.VISIBLE, false)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mProgressDrawable != null) {
            mProgressDrawable!!.stop()
            mProgressDrawable!!.setVisible(false, false)
        }
    }

    private inner class OvalShadow(shadowRadius: Int, circleDiameter: Int) : OvalShape() {
        private val mRadialGradient: RadialGradient
        private val mShadowRadius: Int
        private val mShadowPaint: Paint
        private val mCircleDiameter: Int
        override fun draw(canvas: Canvas, paint: Paint) {
            val viewWidth = this@MaterialProgressBar.width
            val viewHeight = this@MaterialProgressBar.height
            canvas.drawCircle(
                viewWidth / 2.toFloat(), viewHeight / 2.toFloat(), (mCircleDiameter / 2 + mShadowRadius).toFloat(),
                mShadowPaint
            )
            canvas.drawCircle(viewWidth / 2.toFloat(), viewHeight / 2.toFloat(), (mCircleDiameter / 2).toFloat(), paint)
        }

        init {
            mShadowPaint = Paint()
            mShadowRadius = shadowRadius
            mCircleDiameter = circleDiameter
            mRadialGradient = RadialGradient(
                (mCircleDiameter / 2).toFloat(), (mCircleDiameter / 2).toFloat(),
                mShadowRadius.toFloat(), intArrayOf(
                    FILL_SHADOW_COLOR, Color.TRANSPARENT
                ), null, Shader.TileMode.CLAMP
            )
            mShadowPaint.shader = mRadialGradient
        }
    }

    companion object {
        private const val KEY_SHADOW_COLOR = 0x1E000000
        //    private static final int KEY_SHADOW_COLOR = 0x00FFFFFF;
        private const val FILL_SHADOW_COLOR = 0x3D000000
        //    private static final int FILL_SHADOW_COLOR = 0x00FFFFFF;
// PX
        private const val X_OFFSET = 0f
        private const val Y_OFFSET = 1.75f
        private const val SHADOW_RADIUS = 3.5f
        private const val SHADOW_ELEVATION = 4
        private const val DEFAULT_CIRCLE_BG_LIGHT = -0x50506
        //    private static final int DEFAULT_CIRCLE_BG_LIGHT = 0x00000000;
        private const val DEFAULT_CIRCLE_DIAMETER = 56
        private const val STROKE_WIDTH_LARGE = 3
        const val DEFAULT_TEXT_SIZE = 9
    }
}