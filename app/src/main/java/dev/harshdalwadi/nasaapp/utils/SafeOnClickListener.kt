package dev.harshdalwadi.nasaapp.utils

import android.view.View

abstract class SafeOnClickListener : View.OnClickListener {

    private var lastClickMs: Long = 0
    private val TOO_SOON_DURATION_MS: Long = 700

    /**
     * Override onOneClick() instead.
     */
    override fun onClick(v: View) {
        val nowMs = System.currentTimeMillis()
        if (lastClickMs != 0L && nowMs - lastClickMs < TOO_SOON_DURATION_MS) {
            return
        }
        lastClickMs = nowMs
        onOneClick(v)
    }

    /**
     * Override this function to handle clicks.
     * reset() must be called after each click for this function to be called
     * again.
     *
     * @param v
     */
    abstract fun onOneClick(v: View?)
}
