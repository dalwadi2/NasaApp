package dev.harshdalwadi.nasaapp.base

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dev.harshdalwadi.nasaapp.R
import dev.harshdalwadi.nasaapp.api.NetworkUtils
import dev.harshdalwadi.nasaapp.progress.CustomProgressDialog
import dev.harshdalwadi.nasaapp.utils.PreferenceHelper
import dev.harshdalwadi.nasaapp.utils.Utils
import dev.harshdalwadi.nasaapp.views.activities.MainActivity
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity(), RootView {
    private var startTIme: Long = -1L
    private val TAG = BaseActivity::class.java.simpleName

    protected lateinit var mActivity: AppCompatActivity

    @Inject
    protected lateinit var helper: PreferenceHelper

    private lateinit var mViewDataBinding: T
    var mProgressDialog: CustomProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this

        mViewDataBinding = DataBindingUtil.setContentView(this, findContentView())
        mProgressDialog = CustomProgressDialog(mActivity)


        onReady(savedInstanceState)
    }

    fun getViewDataBinding(): T {
        return mViewDataBinding
    }

    abstract fun onReady(savedInstanceState: Bundle?)

    @LayoutRes
    abstract fun findContentView(): Int

    fun isNetworkConnected(): Boolean {
        return NetworkUtils.isNetworkConnected(applicationContext)
    }


    fun showToast(msg: String) {
        Handler(Looper.getMainLooper())
            .post { Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show() }
    }

    fun showToast(@StringRes msg: Int) {
        Handler(Looper.getMainLooper())
            .post { Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show() }
    }

    fun showUToast(error1: MutableList<Error>) {
        Handler(Looper.getMainLooper())
            .post {
                Toast.makeText(
                    mActivity,
                    mActivity.resources.getString(R.string.msg_unexpected_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun shareTextContent(shareText: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        startActivity(Intent.createChooser(shareIntent, "Send to..."))
    }

    fun showError() {
        Handler(Looper.getMainLooper())
            .post {
                Toast.makeText(
                    mActivity,
                    mActivity.resources.getString(R.string.msg_internet_connection),
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    fun serverError(throwable: HttpException) {
        hideLoader()
        showToast("Server is in maintenance, please try again later")
    }

    fun authError(throwable: Throwable?) {
        hideLoader()
        showToast(getString(R.string.msg_session_expired))
        Utils.log(TAG, "authError: " + throwable?.localizedMessage)
        exitTheApp()

    }

    abstract fun networkError(throwable: Throwable?)

    fun exitTheApp() {

        showLoader()
        Thread {
            try {
                helper.clearAllPrefs()
                runOnUiThread {
                    hideLoader()
                    startActivity(
                        Intent(mActivity, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                    finish()
                }
            } catch (e: IOException) {
                runOnUiThread {
                    showToast("Logout Error")
                    hideLoader()
                }
                Utils.log(TAG, "logout: " + e.localizedMessage)
            }
        }.start()
    }

    /**
     * show progress Dialog for application
     */
    override fun showLoader() {
        if (startTIme != -1L && System.currentTimeMillis() - startTIme < 500L) {
            Timber.w("Loading return")
            return
        }

        if (mProgressDialog!!.isShowing) {
            hideLoader()
            return
        }
        if (!mProgressDialog!!.isShowing) {
            startTIme = System.currentTimeMillis()
            mProgressDialog!!.show("")
            // disable screen touchable while progress is showing
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }

    /**
     * Hide progress Dialog for whole application
     */
    override fun hideLoader() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss("")
            startTIme = -1L
            // after proress dismiss enable touch
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }


    override fun showSnackBar(message: String) {
        showSnackBar(message, false)
    }

    override fun showSnackBar(message: String, showOk: Boolean) {
        showSnackBar(findViewById(R.id.snackbar_layout), message, showOk)
    }

    override fun showSnackBar(view: View, message: String, showOk: Boolean) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snackBar.setActionTextColor(ContextCompat.getColor(this, R.color.white))

        val sbView = snackBar.view
        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        val textView = sbView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))

        if (showOk)
            snackBar.setAction("Ok") { snackBar.dismiss() }

        snackBar.show()
    }

    override fun showErrorSnackBar() {
        val snackBar = Snackbar.make(findViewById(R.id.nav_graph), R.string.msg_internet_connection, Snackbar.LENGTH_LONG)
        snackBar.setActionTextColor(ContextCompat.getColor(this, R.color.white))

        val sbView = snackBar.view
        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
        val textView = sbView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))

        snackBar.show()
    }


    override fun showKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun hideKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    override fun showDialogWithOneAction(
        title: String?, message: String?,
        positiveButton: String?,
        positiveFunction: (DialogInterface, Int) -> Unit,
    ) {
        showDialogWithTwoActions(title, message, positiveButton, null, positiveFunction) { _, _ -> }
    }

    private var materialAlertDialog: AlertDialog? = null
    override fun showDialogWithTwoActions(
        title: String?, message: String?,
        positiveName: String?, negativeName: String?,
        positiveFunction: (DialogInterface, Int) -> Unit,
        negativeFunction: (DialogInterface, Int) -> Unit,
    ) {

        materialAlertDialog?.dismiss()
        materialAlertDialog = MaterialAlertDialogBuilder(this).setCancelable(false).setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveName, positiveFunction)
            .setNegativeButton(negativeName, negativeFunction).create()
        materialAlertDialog?.show()
    }

    override fun hideDialog() {
        if (materialAlertDialog?.isShowing!!) {
            materialAlertDialog?.dismiss()
        }
    }
}