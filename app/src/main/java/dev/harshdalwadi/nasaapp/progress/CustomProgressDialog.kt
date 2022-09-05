package dev.harshdalwadi.nasaapp.progress

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import dev.harshdalwadi.nasaapp.R

/**
 * this class is used for custom ProgressDialog
 */
class CustomProgressDialog : Dialog {
    var progress1: MaterialProgressBar? = null
    var mContext: Context? = null
    var dialog: CustomProgressDialog? = null

    constructor(context: Context?) : super(context!!) {
        mContext = context
    }

    constructor(context: Context?, theme: Int) : super(context!!, theme) {}

    fun show(message: CharSequence?): CustomProgressDialog {
        dialog = CustomProgressDialog(mContext, R.style.ProgressDialog)
        dialog!!.setContentView(R.layout.custom_progress_dialog)
        progress1 = dialog!!.findViewById<View>(R.id.progress1) as MaterialProgressBar
        progress1!!.setColorSchemeResources(R.color.purple_200, R.color.purple_500, R.color.purple_700, R.color.teal_200, R.color.teal_700)
        dialog!!.setCancelable(false)
        dialog!!.window!!.attributes.gravity = Gravity.CENTER
        val lp = dialog!!.window!!.attributes
        lp.dimAmount = 0.0f
        dialog!!.window!!.attributes = lp
        dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        if (dialog != null) {
            dismiss()
            dialog!!.show()
        }
        return this.dialog!!
    }

    fun hide(message: CharSequence?): CustomProgressDialog? {
        if (dialog != null) {
            dialog!!.hide()
        }
        return dialog
    }

    fun dismiss(message: CharSequence?): CustomProgressDialog? {
        return try {
            if (dialog != null) {
                dialog!!.dismiss()
            }
            dialog
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}