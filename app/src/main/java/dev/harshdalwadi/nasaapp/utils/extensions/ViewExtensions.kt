package dev.harshdalwadi.nasaapp.utils.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import dev.harshdalwadi.nasaapp.utils.SafeOnClickListener

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    post {
        requestFocus()
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun View.closeSoftKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.setMargins(
    startMarginDp: Int? = null,
    topMarginDp: Int? = null,
    endMarginDp: Int? = null,
    bottomMarginDp: Int? = null,
) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.run {
            startMarginDp?.let { marginStart = it.dpToPx(context) }
            topMarginDp?.let { topMargin = it.dpToPx(context) }
            endMarginDp?.let { marginEnd = it.dpToPx(context) }
            bottomMarginDp?.let { bottomMargin = it.dpToPx(context) }
        }
        requestLayout()
    }
}

fun Int.dpToPx(context: Context): Int {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics).toInt()
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}

fun BottomSheetDialog.setExpandedStateOnShow(id: Int = com.google.android.material.R.id.design_bottom_sheet) {
    setOnShowListener {
        val bottomSheetInternal = this.findViewById<View>(id) as View
        val behavior = BottomSheetBehavior.from(bottomSheetInternal)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
    }
}

fun TextView.underlineTextView(text: String) {
    val spannableString = SpannableString(text)
    spannableString.setSpan(UnderlineSpan(), 0, text.length, 0)
    this.text = spannableString
}

fun EditText.setTextEditable(text: String) {
    this.setText(text, TextView.BufferType.EDITABLE)
}

fun View.safeClickListener(action: (v: View?) -> Unit) {
    this.setOnClickListener(object : SafeOnClickListener() {
        override fun onOneClick(v: View?) {
            action(v)
        }
    })
}

@SuppressLint("ClickableViewAccessibility")
fun TextView.onRightDrawableClicked(onClicked: (view: TextView) -> Unit) {
    this.setOnTouchListener { v, event ->
        var hasConsumed = false
        if (v is TextView) {
            if (event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
        }
        hasConsumed
    }
}

/**
 * This function is to initialize recycler by given layout manager and adapter block.
 * Recyclerview adapter is set by executing the adapterInitializationBlock.
 * Layout manager is set by using the manager parameter. By default LinearLayoutManager will be set.
 *
 * This function is inline to optimize the usage of lambda argument in function.
 * Usage:
 * rvProjectList.apply {
 *   initialize(manager = LinearLayoutManager(context)) {
 *           projectListAdapter
 *       }
 *   }
 * @param manager -> RecyclerView.LayoutManager which is set to the given recycler view.
 * @param adapterInitializationBlock -> lambda function which will get executed to set the given recycler view adapter.
 */
inline fun RecyclerView.initialize(
    manager: RecyclerView.LayoutManager = LinearLayoutManager(this.context),
    adapterInitializationBlock: () -> RecyclerView.Adapter<out RecyclerView.ViewHolder>,
) {
    layoutManager = manager
    adapter = adapterInitializationBlock()
}

/*
* For making any view in visible state.
* */
fun View.show() {
    this.visibility = View.VISIBLE
}

/*
* For hiding any view.
* */
fun View.hide() {
    this.visibility = View.GONE
}

fun View?.showIf(show: Boolean) {
    when {
        this == null -> return
        show -> show()
        else -> hide()
    }
}

/**
 * This function is to underline the TextView.
 */
fun TextView.underLineText() {
    paintFlags = getUnderLinePaintFlagFor(paintFlags)
}

/**
 * This function is to underline the Button text.
 */
fun Button.underLineText() {
    paintFlags = getUnderLinePaintFlagFor(paintFlags)
}

fun getUnderLinePaintFlagFor(paintFlags: Int) = paintFlags or Paint.UNDERLINE_TEXT_FLAG

/**
 * This function is to strike the text of the TextView.
 */
fun TextView.strikeText() {
    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}

/**
 * This function is to remove strike the text of the TextView.
 */
fun TextView.removeStrikeText() {
    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
}

object ViewExtensions {
    const val MINUS_SIGN = "-"
}

/**
 * This function is to mark view as header to accessibility.
 *
 * Since the attribute "android:accessibilityHeading" is accessible from android P, ViewCompact
 * is used here to do the same.
 */
fun View.setViewAccessibilityHeading(boolean: Boolean = true) {
    ViewCompat.setAccessibilityHeading(this, boolean)
}

fun View.addAccessibilityAction(action: String) {
    ViewCompat.setAccessibilityDelegate(
        this,
        object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                val customClick = AccessibilityNodeInfoCompat.AccessibilityActionCompat(
                    AccessibilityNodeInfoCompat.ACTION_CLICK, action
                )
                info.addAction(customClick)
            }
        }
    )
}

fun ImageView?.applyRotateAnimation() {
    if (this == null) {
        return
    }
    val rotate = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
        duration = 1000
        repeatCount = Animation.INFINITE
        repeatMode = Animation.RESTART
    }
    startAnimation(rotate)
}

inline fun View.waitForLayout(crossinline f: () -> Unit) {
    val vto = viewTreeObserver
    vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            when {
                vto.isAlive -> {
                    vto.removeOnGlobalLayoutListener(this)
                    f()
                }
                else -> viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
    })
}

/**
 * Hides soft-keyboard and clears focus when clicked to start icon while focused,
 * otherwise shows soft-keyboard and requests focus
 */
fun TextInputLayout.applyKeyboardToggleButtonBehavior() = setStartIconOnClickListener {
    editText?.apply {
        when {
            isFocused -> {
                closeSoftKeyboard()
                clearFocus()
            }
            else -> {
                requestFocus()
                showKeyboard()
            }
        }
    }
}

fun View.changeAccessibilityNodeText(contentDescription: String) {
    ViewCompat.setAccessibilityDelegate(
        this,
        object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(v: View, info: AccessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(v, info)
                info.text = contentDescription
            }
        }
    )
}
