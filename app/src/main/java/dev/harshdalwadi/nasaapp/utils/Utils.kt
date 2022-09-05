package dev.harshdalwadi.nasaapp.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.provider.Settings
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.format.DateUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import dev.harshdalwadi.nasaapp.BuildConfig
import dev.harshdalwadi.nasaapp.R
import dev.harshdalwadi.nasaapp.base.BaseActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.regex.Matcher


class Utils(private val activity: Activity?) {

    fun hideKeyboard() {
        // Check if no view has focus:
        val view1 = activity?.currentFocus
        if (view1 != null) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view1.windowToken, 0)
        }
    }

    fun showKeyboard() {
        val view1 = activity?.currentFocus
        if (view1 != null) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view1, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    companion object {
        val maxLogSize = 3000

        fun log(TAG: String, msg: String) {
            if (BuildConfig.DEBUG) {
                if (BuildConfig.DEBUG) {
                    for (i in 0..msg.length / maxLogSize) {
                        val start = i * maxLogSize
                        var end = (i + 1) * maxLogSize
                        end = if (end > msg.length) msg.length else end
                        Log.e(TAG, msg.substring(start, end))
                    }
                }
            }
        }


        fun logW(TAG: String, msg: String) {
            if (BuildConfig.DEBUG) {
                if (BuildConfig.DEBUG) {
                    for (i in 0..msg.length / maxLogSize) {
                        val start = i * maxLogSize
                        var end = (i + 1) * maxLogSize
                        end = if (end > msg.length) msg.length else end
                        Log.w(TAG, msg.substring(start, end))
                    }
                }
            }
        }

        fun logI(TAG: String, msg: String) {
            if (BuildConfig.DEBUG) {
                for (i in 0..msg.length / maxLogSize) {
                    val start = i * maxLogSize
                    var end = (i + 1) * maxLogSize
                    end = if (end > msg.length) msg.length else end
                    Log.i(TAG, msg.substring(start, end))
                }
            }
        }
    }
}